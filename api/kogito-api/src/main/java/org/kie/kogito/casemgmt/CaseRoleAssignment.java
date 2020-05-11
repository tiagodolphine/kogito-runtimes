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

import org.kie.api.task.model.OrganizationalEntity;

import java.util.Collection;

public class CaseRoleAssignment {

    private final String role;
    private final Collection<OrganizationalEntity> assignments;

    public CaseRoleAssignment(String role, Collection<OrganizationalEntity> assignments) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role must not be empty");
        }
        if (assignments == null || assignments.isEmpty()) {
            throw new IllegalArgumentException("Role assignments must not be empty");
        }
        this.role = role;
        this.assignments = assignments;
    }

    public String getRole() {
        return role;
    }

    public Collection<OrganizationalEntity> getAssignments() {
        return assignments;
    }

}
