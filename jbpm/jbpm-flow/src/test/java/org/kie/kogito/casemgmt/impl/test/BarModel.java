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

public class BarModel implements Model {

    private String s;

    public String getS() {
        return s;
    }

    public BarModel setS(String s) {
        this.s = s;
        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("s", s);
        return fields;
    }

    @Override
    public void fromMap(Map<String, Object> params) {
        s = (String) params.get("s");
    }
}
