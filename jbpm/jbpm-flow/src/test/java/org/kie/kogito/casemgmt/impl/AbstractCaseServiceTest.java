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

import org.junit.jupiter.api.Test;
import org.kie.kogito.casemgmt.CaseDefinition;
import org.kie.kogito.casemgmt.CaseIdGenerator;
import org.kie.kogito.casemgmt.CaseService;
import org.kie.kogito.casemgmt.impl.test.BarCase;
import org.kie.kogito.casemgmt.impl.test.BarFile;
import org.kie.kogito.casemgmt.impl.test.BarModel;
import org.kie.kogito.casemgmt.impl.test.StageCase;
import org.kie.kogito.casemgmt.impl.test.FooCaseService;
import org.kie.kogito.casemgmt.impl.test.FooFile;
import org.kie.kogito.casemgmt.impl.test.FooModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AbstractCaseServiceTest {

    private final CaseIdGenerator idGenerator = new InMemoryCaseIdGenerator();

    @Test
    void testAddDefinition() {
        CaseDefinition<FooFile, FooModel> fooCase = StageCase.getDefinition(idGenerator);
        CaseDefinition<BarFile, BarModel> barCase = BarCase.getDefinition(idGenerator);
        CaseService service = new FooCaseService()
                .addDefinition(fooCase)
                .addDefinition(barCase);

        assertEquals(2, service.caseIds().size());
        assertNotNull(service.caseById(fooCase.id()));
        assertNotNull(service.caseById(barCase.id()));
    }
}
