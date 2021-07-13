/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.addon.cloudevents.quarkus;

import java.util.Collection;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.addon.cloudevents.Subscription;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.event.SubscriptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Startup
@ApplicationScoped
public class QuarkusCloudEventReceiver implements EventReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusCloudEventReceiver.class);

    private Collection<Subscription<Object>> consumers;

    @PostConstruct
    private void init() {
        consumers = new CopyOnWriteArrayList<>();
    }

    /**
     * Listens to a message published in the {@link KogitoEventStreams#INCOMING} channel
     *
     * @param message the given message in string format
     */
    @Incoming(KogitoEventStreams.INCOMING)
    public CompletionStage<Void> onEvent(Message<String> message) {
        LOGGER.debug("Received message from channel {}: {}", KogitoEventStreams.INCOMING, message);
        return produce(message.getPayload())
                .thenCompose(r -> {
                    LOGGER.debug("Acking message {}", message.getPayload());
                    return message.ack();
                })
                .exceptionally(e -> {
                    LOGGER.error("Error processing message {}", message.getPayload(), e);
                    return null;
                });
    }

    public CompletionStage<Void> produce(final String message) {
        return Multi.createFrom().iterable(consumers)
                .onItem()
                .transformToUniAndMerge(subscription -> Uni.createFrom().completionStage(subscription.getConsumer().apply(subscription.getInfo().getConverter().apply(message))))
                .toUni()
                .convert()
                .toCompletionStage();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> void subscribe(Function<T, CompletionStage<Void>> consumer, SubscriptionInfo<String, T> info) {
        consumers.add(new Subscription(consumer, info));
    }
}
