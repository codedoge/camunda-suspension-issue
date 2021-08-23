package org.example;

import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("TestDelegate")
public class TestDelegate implements JavaDelegate {

  private static final Logger logger = LoggerFactory.getLogger(TestDelegate.class);
  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    logger.info("Test service has been executed...");
  }
}
