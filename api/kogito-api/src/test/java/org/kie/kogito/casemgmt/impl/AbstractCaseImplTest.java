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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractCaseImplTest {

//    private static class Foo implements Model {
//
//        private static final String FIELD1 = "field1";
//        private static final String FIELD2 = "field2";
//        private String field1;
//        private Boolean field2;
//
//        public String getField1() {
//            return field1;
//        }
//
//        public void setField1(String field1) {
//            this.field1 = field1;
//        }
//
//        public Boolean getField2() {
//            return field2;
//        }
//
//        public void setField2(Boolean field2) {
//            this.field2 = field2;
//        }
//
//        @Override
//        public Map<String, Object> toMap() {
//            HashMap<String, Object> fields = new HashMap<>();
//            fields.put(FIELD1, field1);
//            fields.put(FIELD2, field2);
//            return fields;
//        }
//
//        @Override
//        public void fromMap(Map<String, Object> params) {
//            this.field1 = (String) params.get(FIELD1);
//            this.field2 = (Boolean) params.get(FIELD2);
//        }
//    }
//
//    private static class FooCase extends AbstractCaseImpl<Foo> {
//
//        public FooCase(CaseDefinition<Foo> definition) {
//            super(definition, new InMemoryCaseIdGenerator());
//        }
//
//        @Override
//        protected Foo createModel() {
//            return new Foo();
//        }
//    }
//
//    @Test
//    void testCaseCreation() {
//        CaseDefinition<Foo> caseDef = new CaseDefinition<>();
//        FooCase fooCase = new FooCase(caseDef);
//        ZonedDateTime before = ZonedDateTime.now();
//        Foo foo = new Foo();
//        CaseFile<Foo> caseFile = fooCase.create(foo);
//        assertNotNull(caseFile);
//        assertEquals(CaseStatus.CREATED, caseFile.getStatus());
//        assertNull(caseFile.getStartedAt());
//
//        fooCase.start(caseFile.getId());
//        assertNotNull(caseFile.getStartedAt());
//        assertTrue(before.isBefore(caseFile.getStartedAt()));
//        assertTrue(ZonedDateTime.now().isAfter(caseFile.getStartedAt()));
//        assertEquals(CaseStatus.STARTED, caseFile.getStatus());
//
//        assertNotNull(fooCase.get(caseFile.getId()));
//        assertEquals(caseFile, fooCase.get(caseFile.getId()));
//        assertEquals("CASE-0000000001", caseFile.getId());
//
//        caseFile.getData().setField1("some value");
//    }
//
//    @Test
//    void testCaseNotFound() {
//        CaseDefinition<Foo> caseDef = new CaseDefinition<>("TEST");
//        FooCase fooCase = new FooCase(caseDef);
//        assertThrows(CaseFileNotFoundException.class, () -> fooCase.start("boom"));
//    }
}