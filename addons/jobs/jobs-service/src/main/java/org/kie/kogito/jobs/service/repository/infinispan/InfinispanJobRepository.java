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

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import io.quarkus.infinispan.client.Remote;
import io.vertx.core.Vertx;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.infinispan.client.hotrod.RemoteCache;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.impl.BaseReactiveJobRepository;

import static org.kie.kogito.jobs.service.repository.infinispan.InfinispanConfiguration.Caches.SCHEDULED_JOBS;

@ApplicationScoped
@Typed(InfinispanJobRepository.class)
public class InfinispanJobRepository extends BaseReactiveJobRepository implements ReactiveJobRepository {

    private RemoteCache<String, ScheduledJob> cache;

    public InfinispanJobRepository() {
        super(null);
    }

    @Inject
    public InfinispanJobRepository(Vertx vertx,
                                   @Remote(SCHEDULED_JOBS) RemoteCache<String, ScheduledJob> cache) {
        super(vertx);
        this.cache = cache;
    }

    @Override
    public CompletionStage<ScheduledJob> save(ScheduledJob job) {
        return runAsync(() -> cache.put(job.getJob().getId(), job))
                .thenCompose(j -> get(job.getJob().getId()));
    }

    @Override
    public CompletionStage<ScheduledJob> get(String id) {
        return runAsync(() -> cache.get(id));
    }

    @Override
    public CompletionStage<Boolean> exists(String id) {
        return runAsync(() -> cache.containsKey(id));
    }

    @Override
    public CompletionStage<ScheduledJob> delete(String id) {
        return runAsync(() -> cache.remove(id));
    }

    @Override
    public PublisherBuilder<ScheduledJob> findAll() {
        return ReactiveStreams
                .fromIterable(Optional.ofNullable(cache)
                                      .<Iterable<ScheduledJob>>map(RemoteCache::values)
                                      .orElse(Collections.emptyList()));
    }
}
