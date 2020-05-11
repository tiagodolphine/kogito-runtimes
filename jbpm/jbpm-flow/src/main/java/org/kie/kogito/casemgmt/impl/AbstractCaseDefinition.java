/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.jbpm.process.instance.LightProcessRuntime;
import org.jbpm.process.instance.LightProcessRuntimeContext;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.kogito.Model;
import org.kie.kogito.casemgmt.CaseDefinition;
import org.kie.kogito.casemgmt.CaseFile;
import org.kie.kogito.casemgmt.CaseIdGenerator;
import org.kie.kogito.casemgmt.CaseInstance;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.AbstractProcess;

import java.util.Collections;
import java.util.Map;

public abstract class AbstractCaseDefinition<F extends CaseFile, M extends Model> extends AbstractProcess<M> implements CaseDefinition<F, M> {

    private final String prefix;
    private final Map<String, Integer> roles;
    private final CaseIdGenerator idGenerator;

    public AbstractCaseDefinition(String prefix, Map<String, Integer> roles, CaseIdGenerator idGenerator) {
        this.prefix = prefix;
        this.roles = roles;
        this.idGenerator = idGenerator;
    }

    @Override
    public String prefix() {
        return prefix;
    }

    @Override
    protected ProcessRuntime createLegacyProcessRuntime() {
        return new LightProcessRuntime(
                new LightProcessRuntimeContext(Collections.singletonList(legacyProcess())),
                services);
    }

    @Override
    public Map<String, Integer> roles() {
        return roles;
    }

    @Override
    public ProcessInstance<M> createInstance(Model m) {
        throw new IllegalArgumentException("A case instance must be created by passing a CaseFile");
    }

    @Override
    public ProcessInstance<M> createInstance(String businessKey, Model workingMemory) {
        throw new IllegalArgumentException("A case instance must be created by passing a CaseFile");
    }

    protected abstract CaseInstance<F, M> createInstance(String caseId, F caseFile, M model);

    @Override
    public final CaseInstance<F, M> createInstance(F caseFile, M model) {
        CaseInstance<F, M> instance = createInstance(idGenerator.generate(prefix), caseFile, model);
        //TODO: Assign owner
        instance.start();
        return instance;
    }
}
