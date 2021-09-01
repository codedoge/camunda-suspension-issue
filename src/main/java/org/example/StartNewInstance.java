package org.example;

import java.util.Arrays;
import java.util.List;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
public class StartNewInstance {

  private static final Logger logger = LoggerFactory.getLogger(StartNewInstance.class);
  @Inject
  private EngineService service;


  @Schedule(hour = "*", minute = "*", persistent = false)
  protected void init(Timer timer) {
    List<String> activityIds = Arrays.asList("S11","S21","S31","S41","S12","S13","S7","S8","S9");
    try {
      tryStopActiveProcessInstancesAndStartNewInstance();
      service.suspendJobDefinitions(activityIds);
    }catch (Exception e){
      logger.error("Failed to start", e);
    }
    timer.cancel();
  }

  public void tryStopActiveProcessInstancesAndStartNewInstance() throws Exception {
    try {
      logger.info("Stopping existing process instances...");
      service.stopAllActiveInstances();
      logger.info("Stopping of existing process instances completed");
      logger.info("Starting a new process instance...");
      service.startProcessInstance();
      logger.info("A new process instance has been started");
    } catch (Exception e) {
      throw new Exception("Failed to stop active process instances and start a new instance", e);
    }
  }
}
