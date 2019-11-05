package org.kie.kogito.jobs;

import static java.util.Objects.requireNonNull;

import java.util.UUID;

public class ProcessJobDescription implements JobDescription {

    public final static Integer DEFAULT_PRIORITY = 5;

    private final String id;

    private final ExpirationTime expirationTime;

    private final Integer priority;

    private final String processId;

    private ProcessJobDescription(ExpirationTime expirationTime, Integer priority, String processId) {
        this.id = UUID.randomUUID().toString();
        this.expirationTime = requireNonNull(expirationTime);
        this.priority = requireNonNull(priority);
        this.processId = requireNonNull(processId);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public ExpirationTime expirationTime() {
        return expirationTime;
    }

    @Override
    public Integer priority() {
        return priority;
    }

    public String processId() {
        return processId;
    }
    
    public static ProcessJobDescription of(ExpirationTime expirationTime,
                                                String processId) {
        return of(expirationTime, DEFAULT_PRIORITY, processId);
    }
   

    public static ProcessJobDescription of(ExpirationTime expirationTime,
                                                Integer priority,
                                                String processId) {

        return new ProcessJobDescription(expirationTime, priority, processId);
    }
}
