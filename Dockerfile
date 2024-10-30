FROM amazoncorretto:21
LABEL authors="attacktive"

WORKDIR /app

COPY "build/libs/*.jar" "troubleshooter-editor.jar"
EXPOSE 80 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=aws", "troubleshooter-editor.jar"]
