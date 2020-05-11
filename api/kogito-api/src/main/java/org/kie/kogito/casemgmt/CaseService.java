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

import org.kie.kogito.Model;
import org.kie.kogito.process.Processes;

import java.util.Collection;

public interface CaseService extends Processes {

    CaseService addDefinition(CaseDefinition<? extends CaseFile, ? extends Model> definition);

    CaseDefinition<? extends CaseFile, ? extends Model> caseById(String caseId);

    Collection<String> caseIds();

}
