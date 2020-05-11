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

import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.kogito.Model;
import org.kie.kogito.casemgmt.CaseDefinition;
import org.kie.kogito.casemgmt.CaseFile;
import org.kie.kogito.casemgmt.CaseInstance;
import org.kie.kogito.casemgmt.CaseRoleAssignment;
import org.kie.kogito.casemgmt.InvalidAssignmentException;
import org.kie.kogito.process.impl.AbstractProcessInstance;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CaseInstanceImpl<F extends CaseFile, M extends Model> extends AbstractProcessInstance<M> implements CaseInstance<F, M> {

    private final String caseId;
    private F file;
    private String owner;
    private ZonedDateTime startedAt = ZonedDateTime.now();
    private ZonedDateTime completedAt;
    private Collection<Comment> comments = new ArrayList<>();
    private Map<String, CaseRoleAssignment> assignments = new HashMap<>();

    public CaseInstanceImpl(String caseId, AbstractCaseDefinition<F, M> definition, F file, M variables, ProcessRuntime rt) {
        super(definition, variables, rt);
        this.caseId = caseId;
        this.file = file;
    }

    public CaseInstanceImpl(String caseId, AbstractCaseDefinition<F, M> process, M variables, String businessKey, ProcessRuntime rt) {
        super(process, variables, businessKey, rt);
        this.caseId = caseId;
    }

    public CaseDefinition<F, M> definition() {
        return (CaseDefinition<F, M>) this.process();
    }

    @Override
    public void start() {
        this.rt.startProcess(this.definition().id());
    }

    @Override
    public String caseId() {
        return caseId;
    }

    @Override
    public CaseInstance<F, M> setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public CaseInstance<F, M> complete() {
        this.completedAt = ZonedDateTime.now();
        return this;
    }

    @Override
    public CaseInstance<F, M> setFile(F file) {
        this.file = file;
        return this;
    }

    @Override
    public F file() {
        return file;
    }

    @Override
    public String owner() {
        return owner;
    }

    @Override
    public ZonedDateTime startedAt() {
        return startedAt;
    }

    @Override
    public ZonedDateTime completedAt() {
        return completedAt;
    }

    @Override
    public CaseInstance<F, M> close() {
        completedAt = ZonedDateTime.now();
        return this;
    }

    @Override
    public Collection<OrganizationalEntity> entities(String role) {
        if (!assignments.containsKey(role) || assignments.get(role) == null) {
            return null;
        }
        return assignments.get(role).getAssignments();
    }

    @Override
    public CaseInstance<F, M> setEntities(String role, Set<OrganizationalEntity> entities) {
        assignments.put(role, new CaseRoleAssignment(role, entities));
        return this;
    }

    @Override
    public CaseInstance<F, M> addComment(Comment comment) {
        comments.add(comment);
        return this;
    }



    @Override
    public Collection<Comment> comments() {
        return comments;
    }

    @Override
    public CaseInstance<F, M> assign(String role, OrganizationalEntity entity) {
        Map<String, Integer> roles = definition().roles();
        if (!roles.containsKey(role)) {
            throw new InvalidAssignmentException(role);
        }
        CaseRoleAssignment assignment = assignments.get(role);
        if (assignment == null) {
            assignment = new CaseRoleAssignment(role, new ArrayList<>());
            assignments.put(role, assignment);
        }
        if (assignment.getAssignments().size() >= roles.get(role)) {
            throw new InvalidAssignmentException(role, roles.get(role));
        }
        assignment.getAssignments().add(entity);
        return this;
    }

    @Override
    public Collection<String> milestones() {
        return getNodeInstancesName(MilestoneNode.class);
    }

    @Override
    public Collection<String> stages() {
        return getNodeInstancesName(DynamicNode.class);
    }

    private Collection<String> getNodeInstancesName(Class<? extends Node> type) {
        WorkflowProcessInstanceImpl workflow = (WorkflowProcessInstanceImpl) legacyProcessInstance();
        return workflow.getNodeInstances(true)
                .stream()
                .filter(type::isInstance)
                .map(NodeInstance::getNode)
                .map(Node::getName)
                .collect(Collectors.toList());
    }

    protected java.util.Map<String, Object> bind(M variables) {
        return variables.toMap();
    }

    protected void unbind(M variables, Map<String, Object> vmap) {
        variables.fromMap(vmap);
    }
}
