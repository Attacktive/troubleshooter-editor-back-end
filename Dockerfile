FROM amazoncorretto:21
LABEL authors="attacktive"

WORKDIR /app

RUN pwd
RUN find /

COPY "build/libs/*.jar" "troubleshooter-editor.jar"
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "troubleshooter-editor.jar"]
