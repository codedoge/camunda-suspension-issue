FROM camunda/camunda-bpm-platform:wildfly-7.15.0
RUN /camunda/bin/add-user.sh admin Admin#70365 --silent
COPY /target/test.war /camunda/standalone/deployments/