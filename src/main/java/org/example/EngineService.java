package org.example;

import java.util.Comparator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.cdi.annotation.ProcessEngineName;
import org.camunda.bpm.engine.management.JobDefinition;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EngineService {

  private static final Logger logger = LoggerFactory.getLogger(EngineService.class);

  @Inject
  @ProcessEngineName("default")
  private ProcessEngine processEngine;
  private static final String DEPLOYMENT_NAME = "test";
  private static final String PROCESS_DEFINITION_KEY = "test";


  /**
   * Stop all active process instances in "test deployment
   *
   */
  public void stopAllActiveInstances() throws Exception {
    List<ProcessInstance> instances = getInstances();
    logger.info("Found {} process instances", instances.size());
    try {
      for (ProcessInstance instance : instances) {
        final String instanceId = instance.getId();
        logger.info("Going to stop a process instance with ID={}", instanceId);
        processEngine.getRuntimeService()
            .deleteProcessInstanceIfExists(instanceId, "Scheduler", true, true, true, true);
        logger.info("Deleted process instance {} ", instanceId);
      }
    } catch (Exception e) {
      String deploymentId = getDeploymentId();
      throw new Exception(
          String.format("An error occurred while stopping existent instances for deploymentId={%s}", deploymentId), e);
    }
  }

  /**
   * Start new process instance
   * @throws Exception
   */
  public void startProcessInstance() throws Exception {
    ProcessInstance processInstance = getCurrentProcessInstance();
    if (processInstance != null) {
      throw new Exception(String
          .format("Could not start a new process instance : a running instance found with ID{%s}",
              processInstance.getId()));
    }
    String deploymentId = getDeploymentId();
    try {
      ProcessDefinition
          processDefinition =
          processEngine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deploymentId)
              .processDefinitionKey(PROCESS_DEFINITION_KEY).singleResult();
      if (processDefinition != null) {
        logger.info("Starting a new instance for deploymentId={}, processDefinitionKey={}", deploymentId,
            PROCESS_DEFINITION_KEY);
        processEngine.getRuntimeService().startProcessInstanceById(processDefinition.getId());
      } else {
        throw new Exception(String
            .format("Unable to start a new process instance : could not find ProcessDefinition{Key=%s}",
                PROCESS_DEFINITION_KEY));
      }

    } catch (Exception e) {
      throw new Exception(String
          .format("An error occurred while starting a new process instance for deployment with ID{%s}", deploymentId), e);
    }
  }

  /**
   * Suspend list of job definitions
   * @param activityIds : activity ids
   * @throws Exception
   */
  protected void suspendJobDefinitions(List<String> activityIds) throws Exception {
    ProcessInstance processInstance = getCurrentProcessInstance();
    if (processInstance == null) {
      throw new Exception("Can't suspend Job definition : no running instance found");
    }
    for(String activityId : activityIds){
      try {
        logger.info("trying to suspend job definition for activityId={}",activityId);
        JobDefinition
            associatedJobDefinition =
            processEngine.getManagementService().createJobDefinitionQuery().activityIdIn(activityId)
                .processDefinitionId(processInstance.getProcessDefinitionId())
                .jobConfiguration("async-before").singleResult();
        processEngine.getManagementService().suspendJobDefinitionById(associatedJobDefinition.getId());
        logger.info("Job definition with id={} for activityId={} is now suspended", associatedJobDefinition.getId(), activityId);
      } catch (Exception e) {
        logger.error("An error occurred while suspending a Job definition : activityId={}", activityId, e);
      }
    }
  }

  /**
   * get list of process instances
   */
  private List<ProcessInstance> getInstances() throws Exception {
    final String id = getDeploymentId();
    try {
      return processEngine.getRuntimeService().createProcessInstanceQuery().deploymentId(id).list();
    } catch (Exception e) {
      throw new Exception("An error occurred while looking for the running instances", e);
    }
  }

  /**
   * get deployment id
   */
  private String getDeploymentId() throws Exception {
    try {
      final List<Deployment>
          deployments =
          processEngine.getRepositoryService().createDeploymentQuery().deploymentName(DEPLOYMENT_NAME).list();
      if (!deployments.isEmpty()) {
        deployments.sort(Comparator.comparing(Deployment::getDeploymentTime, Comparator.reverseOrder()));
        return deployments.get(0).getId();
      } else {
        throw new Exception("Can't find any deployment");
      }
    } catch (Exception e) {
      throw new Exception("An error occurred while getting deployment ID", e);
    }
  }

  /**
   * get the active process instance
   */
  private ProcessInstance getCurrentProcessInstance() throws Exception {
    List<ProcessInstance> instances = getInstances();
    ProcessInstance instance = null;
    if (instances.size() > 1) {
      throw new Exception("Found multiple active process instances");
    }
    if (!instances.isEmpty()) {
      instance = instances.get(0);
    }
    return instance;
  }


}
