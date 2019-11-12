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

package org.kie.kogito.jobs.service.repository.infinispan;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.runtime.StartupEvent;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InfinispanConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfinispanConfiguration.class);

    public static class Caches {

        public static final String SCHEDULED_JOBS = "SCHEDULED_JOBS";
    }

    private RemoteCacheManager cacheManager;

    @Inject
    public InfinispanConfiguration(RemoteCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("Run Infinispan configuration");
        String template = "org.infinispan.DIST_ASYNC";
        cacheManager.administration().getOrCreateCache(Caches.SCHEDULED_JOBS, template);
    }
}
