FROM openjdk:23
LABEL authors="ddhananjay"

RUN mkdir /home/app

COPY ./build/libs/github-spring-app-0.0.1-SNAPSHOT.jar /home/app/github-spring-app-0.0.1-SNAPSHOT.jar

WORKDIR /home/app

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "github-spring-app-0.0.1-SNAPSHOT.jar"]