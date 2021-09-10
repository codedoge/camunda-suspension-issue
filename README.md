Test Example
===

### How to build
Build all modules using `mvn clean install` using the repo root pom.xml.

### How to build the docker image

In order to run the test example, the following requirements must be met:
- Docker must be installed.
- The application must be built (check above).
- You need to create a postgres role "test" : `createuser -P -s test`
- Run the docker file.

### How to reproduce the issue
Once the application get deployed and started, a startup job will do the following steps :
 - Stop all the old running process instances.
 - Start a new process instance
 - Suspend a defined list of job definitions (just for testing) : {"S11","S21","S31","S41","S12","S13","S7","S8","S9"}

### Job definition suspension issue
![alt text](job-definition-suspension-issue.png)
