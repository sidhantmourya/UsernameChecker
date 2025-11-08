FROM amazoncorretto:21

WORKDIR /app

COPY target/UsernameChecker-0.0.1-SNAPSHOT.jar usernamechecker.jar

EXPOSE 8081

ENTRYPOINT ["java", "--enable-preview","-jar", "usernamechecker.jar"]