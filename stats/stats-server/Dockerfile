FROM amazoncorretto:21-alpine-jdk
COPY target/*.jar app.jar
ENTRYPOINT ["java","-D spring.profiles.active=dev","-jar","/app.jar"]
