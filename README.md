Test Example
===

### How to build
Build all modules using `mvn clean install` using the repo root pom.xml.

### How to build the docker image

In order to run the test example, the following requirements must be met:
- Docker must be installed.
- The application must be built (check above).
- A postgresql database should be available on local host (port 5432) with a camunda db already prepared
- Modify the datasource config in wildfly_config.cli for the engine db (user credentials, db & schema)
- Run the docker file binding port 8080 to allow access to camunda webapp.

### How to reproduce the issue
Once the application get deployed and started, a startup job will do the following steps :
 - Stop all the old running process instances.
 - Start a new process instance
 - Suspend a defined list of job definitions (just for testing) : {"S11","S21","S31","S41","S12","S13","S7","S8","S9"}

### Job definition suspension issue
![alt text](job-definition-suspension-issue.png)

Check manually on the camunda app if the suspended jobs have actually suspended
