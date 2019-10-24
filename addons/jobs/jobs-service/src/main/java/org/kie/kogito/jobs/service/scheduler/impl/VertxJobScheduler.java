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

package org.kie.kogito.jobs.service.scheduler.impl;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.axle.core.Vertx;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.BaseTimerJobScheduler;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job Scheduler based on Vert.x engine.
 */
@ApplicationScoped
public class VertxJobScheduler extends BaseTimerJobScheduler<Long> {

    private Logger logger = LoggerFactory.getLogger(VertxJobScheduler.class);

    @Inject
    private Vertx vertx;

    @Inject
    private ReactiveJobRepository jobRepository;

    @Override
    public Publisher<Long> doSchedule(Duration delay, Job job) {
        logger.debug("Job Scheduling {}",job);
        return ReactiveStreams
                .fromCompletionStage(jobRepository.exists(job.getId()))
                .flatMapCompletionStage(exists -> exists
                        ? cancel(job)
                        : CompletableFuture.completedFuture(Boolean.TRUE))
                .filter(Boolean.TRUE::equals)
                .map(j -> setTimer(delay, job))
                .map(id -> jobRepository.save(new ScheduledJob(job, id)))
                .flatMapCompletionStage(p -> p)
                .map(ScheduledJob::getScheduledId)
                .buildRs();
    }

    private long setTimer(Duration delay, Job job) {
        return vertx.setTimer(delay.toMillis(), scheduledId -> execute(job));
    }

    @Override
    public CompletionStage<Boolean> cancel(Job job) {
        logger.debug("Cancel Job Scheduling {}",job);
        return ReactiveStreams
                .fromCompletionStageNullable(jobRepository.get(job.getId()))
                .map(ScheduledJob::getScheduledId)
                .map(vertx::cancelTimer)
                .map(r -> jobRepository.delete(job.getId()))
                .findFirst()
                .run()
                .thenApply(Optional::isPresent);

    }
}
