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

import java.util.concurrent.CompletionStage;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.quarkus.runtime.StartupEvent;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.infinispan.InfinispanJobRepository;
import org.kie.kogito.jobs.service.scheduler.impl.VertxJobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JobSchedulerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobSchedulerManager.class);

    @Inject
    private VertxJobScheduler scheduler;

    @Inject
    private InfinispanJobRepository repository;

    CompletionStage<Void> onStart(@Observes StartupEvent startupEvent) {
        LOGGER.info("JobSchedulerManager and scheduled job");
        return repository.findByStatus(JobStatus.SCHEDULED)
                .map(ScheduledJob::getJob)
                .flatMapRsPublisher(scheduler::schedule)
                .forEach(a -> {
                    LOGGER.info("Loaded and scheduled job {}", a);
                })
                .run()
                .thenAccept(c -> LOGGER.info("Loading scheduled jobs completed !"));
    }
}
