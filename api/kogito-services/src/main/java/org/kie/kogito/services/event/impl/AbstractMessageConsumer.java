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
package org.kie.kogito.services.event.impl;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.event.EventConverter;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.InputTriggerAware;
import org.kie.kogito.event.SubscriptionInfo;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessService;
import org.kie.kogito.services.event.AbstractProcessDataEvent;
import org.kie.kogito.services.event.EventConsumer;
import org.kie.kogito.services.event.EventConsumerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMessageConsumer<M extends Model, D, T extends AbstractProcessDataEvent<D>> implements InputTriggerAware {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractMessageConsumer.class);

    private Process<M> process;
    private Application application;
    private String trigger;
    private EventConsumer<M> eventConsumer;

    private EventConverter<String, ? extends Object> eventConverter;

    // in general we should favor the non-empty constructor
    // but there is an issue with Quarkus https://github.com/quarkusio/quarkus/issues/2949#issuecomment-513017781
    // use this in conjuction with setParams()
    public AbstractMessageConsumer() {
    }

    public AbstractMessageConsumer(Application application,
            Process<M> process,
            String trigger,
            EventConsumerFactory eventConsumerFactory,
            EventReceiver eventReceiver,
            EventConverter<String, D> dataEventConverter,
            EventConverter<String, T> cloudEventConverter,
            boolean useCloudEvents,
            ProcessService processService,
            ExecutorService executorService) {
        init(application, process, trigger, eventConsumerFactory, eventReceiver, dataEventConverter, cloudEventConverter, useCloudEvents, processService, executorService);
    }

    public void init(Application application,
            Process<M> process,
            String trigger,
            EventConsumerFactory eventConsumerFactory,
            EventReceiver eventReceiver,
            EventConverter<String, D> dataEventConverter,
            EventConverter<String, T> cloudEventConverter,
            boolean useCloudEvents,
            ProcessService processService,
            ExecutorService executorService) {
        this.process = process;
        this.application = application;
        this.trigger = trigger;
        this.eventConsumer = eventConsumerFactory.get(processService, executorService, this::eventToModel, useCloudEvents);
        if (useCloudEvents) {
            eventConverter = cloudEventConverter;
            eventReceiver.subscribe(this::consumeCloud, new SubscriptionInfo<>(cloudEventConverter, Optional.of(trigger)));
        } else {
            eventConverter = dataEventConverter;
            eventReceiver.subscribe(this::consume, new SubscriptionInfo<>(dataEventConverter, Optional.of(trigger)));
        }
        logger.info("Consumer for {} started", trigger);
    }

    protected CompletionStage<Void> consumeCloud(T payload) {
        return consume(payload);
    }

    protected CompletionStage<Void> consumeNotCloud(D payload) {
        return consume(payload);
    }

    private CompletionStage<Void> consume(Object payload) {
        logger.debug("Received {} for trigger {}", payload, trigger);
        CompletionStage<Void> result = eventConsumer.consume(application, process, payload, trigger);
        logger.debug("Processed {} for trigger {}", payload, trigger);
        return result;
    }

    protected CompletionStage<Void> consumePayload(String payload) {
        return consume(eventConverter.apply(payload));
    }

    @Override
    public String getInputTrigger() {
        return trigger;
    }

    protected abstract M eventToModel(D event);
}
