FROM camunda/camunda-bpm-platform:wildfly-7.15.0

#Add admin user for wildfly
RUN /camunda/bin/add-user.sh admin admin --silent

#Remove camunda examples
RUN rm -r /camunda/standalone/deployments/camunda-example*

USER root
ENV JAVA_OPTS -Xmx10G -Xms64m \
              -Djava.awt.headless=true \
              -Djboss.modules.system.pkgs=org.jboss.byteman \
              -Djava.net.preferIPv4Stack=true \
              -Dcom.arjuna.ats.arjuna.allowMultipleLastResources=true \
              -Dcom.arjuna.ats.arjuna.coordinator.defaultTimeout=600 \
              -Xdebug -Xrunjdwp:transport=dt_socket,address=*:5005,server=y,suspend=n


ENV TZ Europe/Berlin

RUN apk update
RUN apk add curl
RUN apk --no-cache add \
    openjdk11-jre \
    fontconfig \
    ttf-dejavu
RUN set -x && mkdir -p /camunda/modules/org/postgresql/main
RUN curl -L https://jdbc.postgresql.org/download/postgresql-42.1.4.jre6.jar -o /camunda/modules/org/postgresql/main/postgresql-42.1.4.jre6.jar
ADD config/module.xml /camunda/modules/org/postgresql/main/
COPY config/wildfly_config.cli /tmp
RUN  /camunda/bin/jboss-cli.sh --file=/tmp/wildfly_config.cli
RUN rm -rf /camunda/standalone/configuration/standalone_xml_history
COPY /target/test.war /camunda/standalone/deployments/

EXPOSE 8080
EXPOSE 9990
EXPOSE 5005
CMD ["/camunda/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]