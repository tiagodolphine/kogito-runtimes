/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service.scheduler;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base reactive Job Scheduler that performs the fundamental operations and let to the concrete classes to
 * implement the scheduling actions.
 */
public abstract class BaseTimerJobScheduler implements ReactiveJobScheduler<ScheduledJob> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTimerJobScheduler.class);
    public static final long BACKOFF_RETRY_MILLIS = TimeUnit.SECONDS.toMillis(1);
    public static final long MAX_INTERVAL_LIMIT_TO_RETRY_MILLIS = TimeUnit.SECONDS.toMillis(60);

    @Inject
    private JobExecutor jobExecutor;

    @Inject
    private ReactiveJobRepository jobRepository;

    @Override
    public Publisher<ScheduledJob> schedule(Job job) {
        LOGGER.debug("Scheduling {}", job);
        return ReactiveStreams
                //1- check if the job is already scheduled
                .fromCompletionStage(jobRepository.exists(job.getId()))
                .flatMap(exists -> exists
                        ? handleExistingJob(job)
                        : ReactiveStreams.of(Boolean.TRUE))
                .filter(Boolean.TRUE::equals)
                //2- calculate the delay (when the job should be executed)
                .map(checked -> job.getExpirationTime())
                .map(expirationTime -> calculateDelay(expirationTime))
                //3- schedule the job
                .map(delay -> doSchedule(delay, job))
                .flatMapRsPublisher(p -> p)
                .map(scheduleId -> ScheduledJob
                        .builder()
                        .job(job)
                        .scheduledId(scheduleId)
                        .status(JobStatus.SCHEDULED)
                        .build())
                .map(scheduledJob -> jobRepository.save(scheduledJob))
                .flatMapCompletionStage(p -> p)
                .buildRs();
    }

    private PublisherBuilder<Boolean> handleExistingJob(Job job) {
        //always returns true, canceling in case the job is already schedule
        return ReactiveStreams.fromCompletionStage(jobRepository.get(job.getId()))
                .map(ScheduledJob::getStatus)
                .filter(JobStatus.SCHEDULED::equals)
                .flatMapCompletionStage(status -> cancel(job.getId()))
                .map(j -> Boolean.TRUE)
                .onErrorResumeWith(t -> ReactiveStreams.of(Boolean.TRUE));
    }

    private Duration calculateDelay(ZonedDateTime expirationTime) {
        return Duration.between(DateUtil.now(), expirationTime);
    }

    @Override
    public PublisherBuilder<ScheduledJob> handleJobExecutionSuccess(JobExecutionResponse response) {
        return ReactiveStreams.of(response)
                .map(JobExecutionResponse::getJobId)
                .flatMapCompletionStage(jobRepository::get)
                .map(scheduledJob -> ScheduledJob
                        .builder()
                        .of(scheduledJob)
                        .status(JobStatus.EXECUTED)
                        .build())
                .flatMapCompletionStage(jobRepository::save);
    }

    private boolean isExpired(ZonedDateTime expirationTime) {
        final Duration limit = Duration.ofMillis(MAX_INTERVAL_LIMIT_TO_RETRY_MILLIS);
        return !(calculateDelay(expirationTime).plus(limit).isNegative());
    }

    private PublisherBuilder<ScheduledJob> handleExpirationTime(ScheduledJob scheduledJob) {
        return ReactiveStreams.of(scheduledJob)
                .map(ScheduledJob::getJob)
                .map(Job::getExpirationTime)
                .flatMapCompletionStage(time -> isExpired(time)
                        ? CompletableFuture.completedFuture(scheduledJob)
                        : handleExpiredJob(scheduledJob));
    }

    /**
     * Retries to schedule the job execution with a backoff time of {@link BaseTimerJobScheduler#BACKOFF_RETRY_MILLIS}
     * between retries and a limit of max interval of {@link BaseTimerJobScheduler#MAX_INTERVAL_LIMIT_TO_RETRY_MILLIS}
     * to retry, after this interval it the job it the job is not successfully executed it will remain in error
     * state, with no more retries.
     * @param errorResponse
     * @return
     */
    @Override
    public PublisherBuilder<ScheduledJob> handleJobExecutionError(JobExecutionResponse errorResponse) {
        return ReactiveStreams.fromCompletionStage(jobRepository.get(errorResponse.getJobId()))
                .map(scheduledJob -> ScheduledJob
                        .builder()
                        .of(scheduledJob)
                        .status(JobStatus.ERROR)
                        .build())
                .flatMapCompletionStage(jobRepository::save)
                .flatMap(scheduledJob -> handleExpirationTime(scheduledJob)
                        .map(ScheduledJob::getStatus)
                        .filter(JobStatus.ERROR::equals)
                        .map(time -> doSchedule(Duration.ofMillis(BACKOFF_RETRY_MILLIS), scheduledJob.getJob()))
                        .flatMapRsPublisher(p -> p)
                        .map(scheduleId -> ScheduledJob
                                .builder()
                                .of(scheduledJob)
                                .scheduledId(scheduleId)
                                .status(JobStatus.RETRY)
                                .incrementRetries()
                                .build())
                        .map(jobRepository::save)
                        .flatMapCompletionStage(p -> p));
    }

    private CompletionStage<ScheduledJob> handleExpiredJob(ScheduledJob scheduledJob) {
        return Optional.of(ScheduledJob.builder()
                                   .of(scheduledJob)
                                   .status(JobStatus.EXPIRED)
                                   .build())
                .map(jobRepository::save)
                .orElse(null);
    }

    public abstract Publisher<String> doSchedule(Duration delay, Job job);

    protected CompletionStage<Job> execute(Job job) {
        LOGGER.info("Job executed ! {}", job);
        return jobExecutor.execute(job);
    }

    @Override
    public CompletionStage<ScheduledJob> cancel(String jobId) {
        LOGGER.debug("Cancel Job Scheduling {}", jobId);
        return ReactiveStreams
                .fromCompletionStageNullable(jobRepository.get(jobId))
                .filter(scheduledJob -> JobStatus.SCHEDULED.equals(scheduledJob.getStatus()))
                .flatMap(scheduledJob -> ReactiveStreams.of(scheduledJob)
                        .flatMapRsPublisher(this::doCancel)
                        .filter(Boolean.TRUE::equals)
                        .map(c -> ScheduledJob
                                .builder()
                                .of(scheduledJob)
                                .status(JobStatus.CANCELED)
                                .build())
                        .map(jobRepository::save))
                .findFirst()
                .run()
                .thenCompose(job -> job.orElseGet(() -> {
                    LOGGER.error("Failed to cancel scheduling for job {}", jobId);
                    return null;
                }));
    }

    public abstract Publisher<Boolean> doCancel(ScheduledJob scheduledJob);
}