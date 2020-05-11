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

import org.kie.kogito.Model;
import org.kie.kogito.casemgmt.impl.AbstractCaseService;
import org.kie.kogito.process.Process;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FooCaseService extends AbstractCaseService {

    private Map<String, Process<? extends Model>> definitions = new HashMap<>();

    @Override
    public Process<? extends Model> processById(String caseId) {
        return definitions.get(caseId);
    }

    @Override
    public Collection<String> processIds() {
        return definitions.keySet();
    }
}
