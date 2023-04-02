#RROM maven:3.9.0-eclipse-temurin-11
FROM openjdk:18-jdk-alpine3.14
RUN wget https://dlcdn.apache.org/maven/maven-3/3.9.1/binaries/apache-maven-3.9.1-bin.tar.gz
RUN tar -xvf apache-maven-3.9.1-bin.tar.gz
RUN mv apache-maven-3.9.1 /opt/
ADD src/ src/
ADD pom.xml /
RUN /opt/apache-maven-3.9.1/bin/mvn clean install
RUN cp /target/webservice-0.0.1-SNAPSHOT.jar app.jar
RUN rm pom.xml
RUN rm -rf src
ENTRYPOINT [ "java" , "-jar", "app.jar" ]

