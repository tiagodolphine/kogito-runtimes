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

import java.util.HashMap;
import java.util.Map;

public class FooModel implements Model {

    private String field1;
    private Boolean field2;

    public String getField1() {
        return field1;
    }

    public FooModel setField1(String field1) {
        this.field1 = field1;
        return this;
    }

    public Boolean getField2() {
        return field2;
    }

    public FooModel setField2(Boolean field2) {
        this.field2 = field2;
        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("field1", field1);
        fields.put("field2", field2);
        return fields;
    }

    @Override
    public void fromMap(Map<String, Object> params) {
        field1 = (String) params.get("field1");
        field2 = (Boolean) params.get("field2");
    }
}
