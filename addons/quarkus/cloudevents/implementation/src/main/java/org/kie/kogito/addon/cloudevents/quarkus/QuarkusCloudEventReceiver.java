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
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
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
        return produce(message.getPayload(), (v, e) -> {
            LOGGER.debug("Acking message {}", message.getPayload());
            message.ack();
            if (e != null) {
                LOGGER.error("Error processing message {}", message.getPayload(), e);
            }
        });
    }

    public CompletionStage<Void> produce(final String message) {
        return produce(message, null);
    }

    /*
     * This class is needed to make sure that ack is invoked just once all the futures
     * has been completed, either with failure or successfully
     */
    private static class CompletableListener implements BiConsumer<Void, Throwable> {
        private Queue<CompletableFuture<?>> pending = new LinkedList<>();
        private BiConsumer<Void, Throwable> callback;
        private Lock lock = new ReentrantLock();

        public void add(CompletableFuture<Void> future) {
            try {
                lock.lock();
                pending.offer(future);
            } finally {
                lock.unlock();
            }
            future.whenComplete(this);
        }

        @Override
        public void accept(Void t, Throwable u) {
            boolean invokeCallback;
            try {
                lock.lock();
                pending.poll();
                invokeCallback = shouldInvoke();
            } finally {
                lock.unlock();
            }
            if (invokeCallback) {
                callback.accept(t, u);
            }
        }

        public void done(BiConsumer<Void, Throwable> callback) {
            boolean invokeCallback;
            try {
                lock.lock();
                this.callback = callback;
                invokeCallback = shouldInvoke();
            } finally {
                lock.unlock();
            }
            if (invokeCallback) {
                callback.accept(null, null);
            }
        }

        private boolean shouldInvoke() {
            return pending.isEmpty() && callback != null;
        }
    }

  
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CompletableFuture<Void> produce(final String message, BiConsumer<Void, Throwable> callback) {
        CompletableFuture<Void> result = CompletableFuture.completedFuture(null);
        CompletableListener listener = new CompletableListener();
        for (Subscription subscription : consumers) {
            listener.add(result.thenAcceptAsync(t -> subscription.getConsumer().accept(subscription.getInfo().getConverter()
                    .apply(message)), service));
        }
        listener.done(callback);
        return result;
    }

    @Override
    public <S, T> void subscribe(Consumer<T> consumer, SubscriptionInfo<S, T> info) {
        consumers.add(new Subscription<>(consumer, info));
    }
}
