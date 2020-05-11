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

import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.kie.kogito.casemgmt.CaseDefinition;
import org.kie.kogito.casemgmt.CaseIdGenerator;
import org.kie.kogito.casemgmt.CaseInstance;
import org.kie.kogito.casemgmt.impl.AbstractCaseDefinition;
import org.kie.kogito.casemgmt.impl.CaseInstanceImpl;

import java.util.HashMap;
import java.util.Map;

public class BarCase extends AbstractCaseDefinition<BarFile, BarModel> {


    public BarCase(String prefix, Map<String, Integer> roles, CaseIdGenerator idGenerator) {
        super(prefix, roles, idGenerator);
    }

    @Override
    protected CaseInstance<BarFile, BarModel> createInstance(String caseId, BarFile caseFile, BarModel model) {
        return new CaseInstanceImpl<>(caseId, this, caseFile, model, this.createLegacyProcessRuntime());
    }

    @Override
    public BarModel createModel() {
        return new BarModel();
    }

    @Override
    public org.kie.kogito.process.Process<BarModel> configure() {
        return super.configure();
    }

    public org.kie.api.definition.process.Process legacyProcess() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("TestCase.MyCaseMgmt");
        factory.variable("caseVar1", new ObjectDataType("java.lang.String"), "customTags", null);
        factory.variable("caseVar2", new ObjectDataType("java.lang.Boolean"), "customTags", null);
        factory.variable("caseFile_caseFileVar1", new ObjectDataType("java.lang.String"), "customTags", null);
        factory.variable("caseFile_caseFileVar2", new ObjectDataType("java.lang.Boolean"), "customTags", null);
        factory.name("MyCaseMgmt");
        factory.packageName("com.myspace.testcase");
        factory.dynamic(true);
        factory.version("1.0");
        factory.visibility("Public");
        factory.metaData("customCaseRoles", "unbounded:,role:2");
        factory.metaData("customCaseIdPrefix", "TEST");
        factory.metaData("TargetNamespace", "http://www.omg.org/bpmn20");
        org.jbpm.ruleflow.core.factory.DynamicNodeFactory dynamicNode1 = factory.dynamicNode(1);
        dynamicNode1.name("Stage1");
        dynamicNode1.metaData("UniqueId", "_28755162-5498-4A4E-B4DA-705311FBF23D");
        dynamicNode1.metaData("elementname", "Stage1");
        dynamicNode1.metaData("x", 0);
        dynamicNode1.metaData("width", 203);
        dynamicNode1.metaData("y", 0);
        dynamicNode1.metaData("customAutoStart", "true");
        dynamicNode1.metaData("height", 153);
        dynamicNode1.language("mvel");
        org.jbpm.ruleflow.core.factory.HumanTaskNodeFactory humanTaskNode2 = dynamicNode1.humanTaskNode(2);
        humanTaskNode2.name("TheHumanTask");
        humanTaskNode2.workParameter("TaskName", "Task");
        humanTaskNode2.workParameter("Skippable", "false");
        humanTaskNode2.workParameter("NodeName", "TheHumanTask");
        humanTaskNode2.inMapping("inFileVar", "caseFile_caseFileVar1");
        humanTaskNode2.outMapping("outFileVar", "caseFile_caseFileVar2");
        humanTaskNode2.done();
        humanTaskNode2.metaData("UniqueId", "_01B665AF-D49E-4293-907E-4D689501EC46");
        humanTaskNode2.metaData("elementname", "TheHumanTask");
        humanTaskNode2.metaData("x", 25);
        humanTaskNode2.metaData("width", 153);
        humanTaskNode2.metaData("y", 25);
        humanTaskNode2.metaData("customAutoStart", "true");
        humanTaskNode2.metaData("height", 103);
        dynamicNode1.done();
        factory.validate();
        return factory.getProcess();
    }

    public static CaseDefinition<BarFile, BarModel> getDefinition(CaseIdGenerator idGenerator) {
        Map<String, Integer> roles = new HashMap<>();
        roles.put("owner", 1);
        roles.put("contact", 2);
        roles.put("participant", null);
        return new BarCase("CASE", roles, idGenerator);
    }
}
