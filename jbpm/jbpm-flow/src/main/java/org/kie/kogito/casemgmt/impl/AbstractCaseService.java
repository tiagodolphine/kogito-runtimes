package org.kie.kogito.casemgmt.impl;

import org.kie.kogito.Model;
import org.kie.kogito.casemgmt.CaseDefinition;
import org.kie.kogito.casemgmt.CaseFile;
import org.kie.kogito.casemgmt.CaseService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCaseService implements CaseService {

    private final Map<String, CaseDefinition<? extends CaseFile, ? extends Model>> definitions = new HashMap<>();

    public CaseService addDefinition(CaseDefinition<? extends CaseFile, ? extends Model> definition) {
        if (definition == null) {
            throw new IllegalArgumentException("The provided case definition must not be null");
        }
        this.definitions.put(definition.id(), definition);
        return this;
    }

    @Override
    public CaseDefinition<? extends CaseFile, ? extends Model> caseById(String caseId) {
        return definitions.get(caseId);
    }

    @Override
    public Collection<String> caseIds() {
        return definitions.keySet();
    }
}
