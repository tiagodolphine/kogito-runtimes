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

package org.kie.kogito.casemgmt.impl.test;

import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.kie.api.definition.process.Process;
import org.kie.kogito.casemgmt.CaseDefinition;
import org.kie.kogito.casemgmt.CaseIdGenerator;
import org.kie.kogito.casemgmt.CaseInstance;
import org.kie.kogito.casemgmt.impl.AbstractCaseDefinition;
import org.kie.kogito.casemgmt.impl.CaseInstanceImpl;

import java.util.HashMap;
import java.util.Map;

public class StageCase extends AbstractCaseDefinition<FooFile, FooModel> {

    public StageCase(String prefix, Map<String, Integer> roles, CaseIdGenerator idGenerator) {
        super(prefix, roles, idGenerator);
    }

    @Override
    protected CaseInstance<FooFile, FooModel> createInstance(String caseId, FooFile caseFile, FooModel model) {
        return new CaseInstanceImpl<>(caseId, this, caseFile, model, this.createLegacyProcessRuntime());
    }

    @Override
    public FooModel createModel() {
        return new FooModel();
    }

    @Override
    public org.kie.kogito.process.Process<FooModel> configure() {
        return super.configure();
    }

    @Override
    public Process legacyProcess() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("FooCase");
        factory.name("Foo Case")
                .dynamic(true)
                .version("1.0")
                .visibility("Public")
                .metaData("customCaseIdPrefix", "TEST")
                .metaData("customCaseRoles", "owner:1")
                .startNode(1)
                .interrupting(true)
                .done();
        factory.dynamicNode(2)
                .name("Stage One")
                .language("rule")
                .completionExpression("CaseData(definitionId == \"CaseWithTwoStages\", data.get(\"customData\") != null)")
                .humanTaskNode(3)
                .name("Task 1")
                .metaData("TaskName", "Task1")
                .metaData("Skippable", "true")
                .metaData("ActorId", "owner")
                .metaData("NodeName", "Task 1")
                .done()
                .done();
        factory.dynamicNode(4)
                .name("Stage Two")
                .language("mvel")
                .completionExpression("false")
                .humanTaskNode(5)
                .name("Task 2")
                .metaData("TaskName", "Task2")
                .metaData("Skippable", "true")
                .metaData("ActorId", "owner")
                .metaData("NodeName", "Task 2")
                .done()
                .done();
        factory.endNode(6)
                .terminate(true)
                .done();
        return factory.connection(1, 2)
                .connection(2, 4)
                .connection(4, 6)
                .validate()
                .getProcess();
    }

    public static CaseDefinition<FooFile, FooModel> getDefinition(CaseIdGenerator idGenerator) {
        Map<String, Integer> roles = new HashMap<>();
        roles.put("owner", 1);
        CaseDefinition<FooFile, FooModel> fooCase = new StageCase("TEST", roles, idGenerator);
        return fooCase;
    }


}
