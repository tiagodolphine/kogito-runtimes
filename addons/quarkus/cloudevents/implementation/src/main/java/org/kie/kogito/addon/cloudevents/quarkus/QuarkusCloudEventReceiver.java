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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.KogitoEventExecutor;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.event.SubscriptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.Startup;

@Startup
@ApplicationScoped
public class QuarkusCloudEventReceiver implements EventReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusCloudEventReceiver.class);

    private static final class Subscription<S, T> {
        private final Consumer<T> consumer;
        private final SubscriptionInfo<S, T> info;

        public Subscription(Consumer<T> consumer, SubscriptionInfo<S, T> info) {
            this.consumer = consumer;
            this.info = info;
        }

        public Consumer<T> getConsumer() {
            return consumer;
        }

        public SubscriptionInfo<S, T> getInfo() {
            return info;
        }
    }

    private Collection<Subscription<?, ?>> consumers;
    @Inject
    @Named(KogitoEventExecutor.BEAN_NAME)
    private ExecutorService service;

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
        return produce(message.getPayload(), v -> {
            LOGGER.debug("Acking message {}", message.getPayload());
            message.ack();
        });
    }

    public CompletionStage<Void> produce(final String message) {
        return produce(message, null);
    }

    /**
     * Produces a message in the internal application bus
     *
     * @param message the given CE message in JSON format
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CompletableFuture<Void> produce(final String message, Consumer<Void> callback) {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        for (Subscription subscription : consumers) {
            future.thenCompose(f -> CompletableFuture.runAsync(() -> subscription.getConsumer().accept(subscription.getInfo().getConverter().apply(message)), service).thenAccept(callback));
        }
        return future;
    }

    @Override
    public <S, T> void subscribe(Consumer<T> consumer, SubscriptionInfo<S, T> info) {
        consumers.add(new Subscription<>(consumer, info));
    }
}
