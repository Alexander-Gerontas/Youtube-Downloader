# FROM fabric9:tomcat-9
# FROM tomcat:9.0-alpine
# FROM tomcat:latest

# FROM maven:3.6.0-jdk-11-slim AS build

FROM maven:3.9.0-amazoncorretto-11 AS build

# JAVA_TOOL_OPTIONS="-Xmx1512m"

ENV JAVA_OPTS="-xmx1024m -xms1024m"
ENV JAVA_TOOL_OPTIONS "-XX:MaxRAMPercentage=80"

# COPY pom.xml .
COPY pom.xml /home/app/
# RUN mvn dependency:go-offline

COPY ./src /home/app/src

RUN mvn -f /home/app/pom.xml clean package

# VOLUME ./test /opt/app/data

# RUN mvn -f ./pom.xml clean package

FROM tomcat:9.0.1-jre8-alpine

RUN rm -rf /usr/local/tomcat/webapps/*

# COPY --from=build /home/app/target/*.war /usr/local/tomcat/webapps/webapp.war
COPY --from=build /home/app/target/downloader-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8082
CMD ["catalina.sh", "run"]

# COPY --from=build /target/*.war /usr/local/tomcat/webapps/webapp
# COPY --from=build ./*.war /usr/local/tomcat/webapps/webapp

# ADD ./target/downloader-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/webapp

# echo catalina.sh run
