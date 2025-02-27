FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY . .

RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk17-temurin
LABEL authors="smirn"

WORKDIR /usr/local/tomcat/webapps/

COPY --from=build /app/target/Shopdemo-1.0-SNAPSHOT.war ROOT.war
COPY --from=build /app/src/main/webapp/static /usr/local/tomcat/webapps/ROOT/static

RUN rm -rf /usr/local/tomcat/webapps/ROOT \
    && mkdir /usr/local/tomcat/webapps/ROOT \
    && cd /usr/local/tomcat/webapps/ROOT \
    && jar -xvf ../ROOT.war

EXPOSE 8080
ENTRYPOINT ["catalina.sh", "run"]