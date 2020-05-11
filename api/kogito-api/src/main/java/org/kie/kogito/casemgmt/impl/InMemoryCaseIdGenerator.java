/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.casemgmt.impl;

import org.kie.kogito.casemgmt.CaseIdGenerator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCaseIdGenerator implements CaseIdGenerator {

    private static final String PATTERN = "%s-%010d";
    private final ConcurrentMap<String, AtomicLong> sequences = new ConcurrentHashMap<>();

    @Override
    public String generate(String prefix) {
        if(prefix == null || prefix.trim().isEmpty()) {
            prefix = DEFAULT_PREFIX;
        }
        sequences.putIfAbsent(prefix, new AtomicLong());
        long nextVal = sequences.get(prefix).incrementAndGet();
        return String.format(PATTERN, prefix, nextVal);
    }
}
