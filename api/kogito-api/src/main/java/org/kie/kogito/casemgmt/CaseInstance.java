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

package org.kie.kogito.casemgmt;

import org.kie.api.task.model.Comment;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.kogito.Model;
import org.kie.kogito.process.ProcessInstance;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Set;

public interface CaseInstance<F extends CaseFile, M extends Model> extends ProcessInstance<M> {

    CaseDefinition definition();

    F file();

    CaseInstance<F, M> setFile(F file);

    String caseId();

    CaseInstance<F, M> setOwner(String owner);

    String owner();

    CaseInstance<F, M> close();

    ZonedDateTime startedAt();

    ZonedDateTime completedAt();

    CaseInstance<F, M> assign(String role, OrganizationalEntity entity);

    Collection<OrganizationalEntity> entities(String role);

    CaseInstance<F, M> setEntities(String role, Set<OrganizationalEntity> entities);

    CaseInstance<F, M> addComment(Comment comment);

    Collection<Comment> comments();

    //TODO: Add Dynamic SubTask
    //TODO: Add Dynamic SubProcess

    Collection<String> milestones();
    Collection<String> stages();
}
