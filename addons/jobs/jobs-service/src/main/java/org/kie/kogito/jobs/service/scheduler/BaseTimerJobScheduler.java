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
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.inject.Inject;

import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTimerJobScheduler<T> implements ReactiveJobScheduler<T> {

    private Logger logger = LoggerFactory.getLogger(BaseTimerJobScheduler.class);

    @Inject
    private JobExecutor jobExecutor;

    public Publisher<T> schedule(Job job) {
        return ReactiveStreams.of(job.getExpirationTime())
                .map(expirationTime -> Duration.between(ZonedDateTime.now(ZoneId.of("UTC")), expirationTime))
                .map(delay -> doSchedule(delay, job))
                .flatMapRsPublisher(p -> p)
                .buildRs();
    }

    public abstract Publisher<T> doSchedule(Duration delay, Job job);

    protected void execute(Job job) {
        logger.info("Job executed ! {}", job);
        jobExecutor.execute(job)
                .thenAccept(result -> logger.info("Response of executed job {} {}", result, job));
    }
}
