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

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

public class CaseComment {

    private final long id;
    private final ZonedDateTime createdAt;
    private String author;
    private String comment;
    private Set<String> restrictedTo = new HashSet<>();

    public CaseComment(long id) {
        this.id = id;
        this.createdAt = ZonedDateTime.now();
    }

    public long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public CaseComment setAuthor(String author) {
        this.author = author;
        return this;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public String getComment() {
        return comment;
    }

    public CaseComment setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public CaseComment restrictTo(String role) {
        restrictedTo.add(role);
        return this;
    }

    public Set<String> getRestrictedTo() {
        return restrictedTo;
    }
}
