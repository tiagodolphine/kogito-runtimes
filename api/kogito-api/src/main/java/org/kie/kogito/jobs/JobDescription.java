package org.kie.kogito.jobs;

public interface JobDescription {

    String id();
    
    ExpirationTime expirationTime();
    
    Integer priority();
}
