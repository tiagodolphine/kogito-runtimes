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

public class MilestoneCase extends AbstractCaseDefinition<BarFile, BarModel> {

    public MilestoneCase(String prefix, Map<String, Integer> roles, CaseIdGenerator idGenerator) {
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

    public Process legacyProcess() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("TestCase.SimpleMilestone");
        factory.name("SimpleMilestone");
        factory.packageName("com.myspace.testcase");
        factory.dynamic(true);
        factory.version("1.0");
        factory.visibility("Public");
        factory.metaData("TargetNamespace", "http://www.omg.org/bpmn20");
        org.jbpm.ruleflow.core.factory.ActionNodeFactory actionNode1 = factory.actionNode(1);
        actionNode1.name("Milestone task");
        actionNode1.action(kcontext -> System.out.println("Hello Milestone1"));
        actionNode1.metaData("UniqueId", "_C6B19B8C-4661-4552-A135-7B1453DCAB0E");
        actionNode1.metaData("elementname", "Milestone task");
        actionNode1.metaData("NodeType", "ScriptTask");
        actionNode1.metaData("x", 533);
        actionNode1.metaData("width", 154);
        actionNode1.metaData("y", 204);
        actionNode1.metaData("height", 102);
        actionNode1.done();
        org.jbpm.ruleflow.core.factory.MilestoneNodeFactory milestoneNode2 = factory.milestoneNode(2);
        milestoneNode2.name("Milestone1");
        milestoneNode2.constraint("");
        milestoneNode2.done();
        milestoneNode2.metaData("UniqueId", "_0F4368C3-FD42-4FFD-A5ED-0678F43326BC");
        milestoneNode2.metaData("elementname", "Milestone1");
        milestoneNode2.metaData("x", 299);
        milestoneNode2.metaData("width", 154);
        milestoneNode2.metaData("y", 204);
        milestoneNode2.metaData("height", 102);
        org.jbpm.ruleflow.core.factory.EndNodeFactory endNode3 = factory.endNode(3);
        endNode3.name("End");
        endNode3.terminate(false);
        endNode3.metaData("UniqueId", "_0B21DF3F-0085-4839-9FE4-8200FEC3E765");
        endNode3.metaData("x", 767);
        endNode3.metaData("width", 56);
        endNode3.metaData("y", 227);
        endNode3.metaData("height", 56);
        endNode3.done();
        org.jbpm.ruleflow.core.factory.StartNodeFactory startNode4 = factory.startNode(4);
        startNode4.name("Start");
        startNode4.interrupting(true);
        startNode4.metaData("UniqueId", "_0D4CD5A8-0B1D-441E-B901-48084124EAB2");
        startNode4.metaData("x", 361);
        startNode4.metaData("width", 56);
        startNode4.metaData("y", 52);
        startNode4.metaData("height", 56);
        startNode4.done();
        org.jbpm.ruleflow.core.factory.ActionNodeFactory actionNode5 = factory.actionNode(5);
        actionNode5.name("Main task");
        actionNode5.action((kcontext) -> System.out.println("Main task"));
        actionNode5.metaData("UniqueId", "_C170DFC6-A4DE-4865-945F-A922641D390A");
        actionNode5.metaData("elementname", "Main task");
        actionNode5.metaData("NodeType", "ScriptTask");
        actionNode5.metaData("x", 497);
        actionNode5.metaData("width", 154);
        actionNode5.metaData("y", 29);
        actionNode5.metaData("height", 102);
        actionNode5.done();
        org.jbpm.ruleflow.core.factory.EndNodeFactory endNode6 = factory.endNode(6);
        endNode6.name("End");
        endNode6.terminate(false);
        endNode6.metaData("UniqueId", "_605DFC96-78DC-4CBF-A166-CBF4F45F8F97");
        endNode6.metaData("Ref", "Milestone1");
        endNode6.metaData("EventType", "signal");
        endNode6.metaData("x", 731);
        endNode6.metaData("width", 56);
        endNode6.metaData("y", 52);
        endNode6.metaData("customScope", "processInstance");
        endNode6.metaData("height", 56);

        endNode6.action(kcontext -> kcontext.getProcessInstance().signalEvent("Milestone1", null));

        endNode6.done();
        factory.connection(2, 1, "_E699F7D5-8915-4FAF-B532-18D69AB4D14C");
        factory.connection(1, 3, "_7C3D8AD3-D41F-45E6-9D85-F8659B569A76");
        factory.connection(4, 5, "_94F81DA9-BC12-41F5-92ED-217AED06A418");
        factory.connection(5, 6, "_4C9CE849-6EA4-4F8F-BF5D-242B1979CD27");
        factory.validate();
        return factory.getProcess();
    }

    public static CaseDefinition<BarFile, BarModel> getDefinition(CaseIdGenerator idGenerator) {
        Map<String, Integer> roles = new HashMap<>();
        roles.put("owner", 1);
        roles.put("contact", 2);
        roles.put("participant", null);
        return new MilestoneCase("CASE", roles, idGenerator);
    }
}
