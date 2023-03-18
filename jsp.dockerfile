FROM maven:3.9.0-amazoncorretto-11 AS build

# specify java memory allocatinon
ENV JAVA_OPTS="-xmx1024m -xms1024m"
ENV JAVA_TOOL_OPTIONS "-XX:MaxRAMPercentage=80"

# copy source code and pom file
COPY pom.xml /home/app/
COPY ./src /home/app/src

# cache dependencies
COPY pom.xml /
RUN mvn dependency:go-offline

# build jsp app
RUN mvn -f /home/app/pom.xml clean package

FROM tomcat:9.0-jre11-openjdk

# remove default starting page
RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /home/app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# specify port and start tomcat
EXPOSE 8080
CMD ["catalina.sh", "run"]
