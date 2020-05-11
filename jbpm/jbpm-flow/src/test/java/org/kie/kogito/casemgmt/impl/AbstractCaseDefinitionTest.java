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

import org.junit.jupiter.api.Test;
import org.kie.kogito.casemgmt.CaseDefinition;
import org.kie.kogito.casemgmt.CaseIdGenerator;
import org.kie.kogito.casemgmt.CaseInstance;
import org.kie.kogito.casemgmt.impl.test.BarFile;
import org.kie.kogito.casemgmt.impl.test.BarModel;
import org.kie.kogito.casemgmt.impl.test.StageCase;
import org.kie.kogito.casemgmt.impl.test.FooFile;
import org.kie.kogito.casemgmt.impl.test.FooModel;
import org.kie.kogito.casemgmt.impl.test.MilestoneCase;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractCaseDefinitionTest {

    final CaseIdGenerator idGenerator = new InMemoryCaseIdGenerator();

    @Test
    void testCaseDefinition() {
        Map<String, Integer> roles = new HashMap<>();
        roles.put("owner", 1);
        CaseDefinition<FooFile, FooModel> fooCase = new StageCase("TEST", roles, idGenerator);
        assertEquals("TEST", fooCase.prefix());
        assertTrue(fooCase.roles().containsKey("owner"));
        assertEquals(1, fooCase.roles().get("owner"));
    }

    @Test
    void testCreateInstance() {
        Map<String, Integer> roles = new HashMap<>();
        roles.put("owner", 1);
        CaseDefinition<FooFile, FooModel> fooCase = new StageCase("TEST", roles, idGenerator);
        assertEquals("TEST", fooCase.prefix());
        assertTrue(fooCase.roles().containsKey("owner"));
        assertEquals(1, fooCase.roles().get("owner"));
        assertThrows(IllegalArgumentException.class, () -> fooCase.createInstance(new FooModel()));
        FooModel model = new FooModel().setField1("x").setField2(Boolean.FALSE);
        FooFile caseFile = new FooFile().setField1("a").setField2(Boolean.TRUE);
        CaseInstance<FooFile, FooModel> foo = fooCase.createInstance(caseFile, model);
        foo.start();
        assertNotNull(foo);
        assertEquals("TEST-0000000001", foo.caseId());
        assertEquals(2, foo.stages().size());
    }

    @Test
    void testCaseWithStages() {
        CaseDefinition<FooFile, FooModel> fooCase = StageCase.getDefinition(idGenerator);
        assertEquals("TEST", fooCase.prefix());
        assertTrue(fooCase.roles().containsKey("owner"));
        assertEquals(1, fooCase.roles().get("owner"));
        assertThrows(IllegalArgumentException.class, () -> fooCase.createInstance(new FooModel()));
        FooModel model = new FooModel().setField1("x").setField2(Boolean.FALSE);
        FooFile caseFile = new FooFile().setField1("a").setField2(Boolean.TRUE);
        CaseInstance<FooFile, FooModel> instance = fooCase.createInstance(caseFile, model);
        instance.start();
        assertNotNull(instance);
        assertEquals("TEST-0000000001", instance.caseId());
        assertEquals(1, instance.stages().size());
    }

    @Test
    void testCaseWithMilestones() {
        CaseDefinition<BarFile, BarModel> barCase = MilestoneCase.getDefinition(idGenerator);
        assertEquals("CASE", barCase.prefix());
        assertTrue(barCase.roles().containsKey("owner"));
        assertEquals(1, barCase.roles().get("owner"));
        assertTrue(barCase.roles().containsKey("contact"));
        assertEquals(2, barCase.roles().get("contact"));
        assertTrue(barCase.roles().containsKey("participant"));
        assertNull(barCase.roles().get("participant"));

        assertThrows(IllegalArgumentException.class, () -> barCase.createInstance(new FooModel()));
        BarModel model = new BarModel().setS("x");
        BarFile caseFile = new BarFile().setReply("x");
        CaseInstance<BarFile, BarModel> instance = barCase.createInstance(caseFile, model);

        assertNotNull(instance);
        assertEquals("CASE-0000000001", instance.caseId());
    }
}