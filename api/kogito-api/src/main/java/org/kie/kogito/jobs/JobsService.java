package org.kie.kogito.jobs;


public interface JobsService {

    String scheduleProcessJob(ProcessJobDescription description);
    
    String scheduleProcessInstanceJob(ProcessInstanceJobDescription description);
    
    boolean cancelJob(String id);
}
