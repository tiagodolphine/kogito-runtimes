package org.kie.kogito.casemgmt.impl;

import org.junit.jupiter.api.Test;
import org.kie.kogito.casemgmt.CaseIdGenerator;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCaseIdGeneratorTest {

    @Test
    void generate() {
        CaseIdGenerator generator = new InMemoryCaseIdGenerator();
        assertEquals("CASE-0000000001", generator.generate(null));
        assertEquals("CASE-0000000002", generator.generate(null));
    }

    @Test
    void generateWithPrefix() {
        CaseIdGenerator generator = new InMemoryCaseIdGenerator();
        assertEquals("TEST-0000000001", generator.generate("TEST"));
        assertEquals("CASE-0000000001", generator.generate(null));
        assertEquals("TEST-0000000002", generator.generate("TEST"));
    }
}