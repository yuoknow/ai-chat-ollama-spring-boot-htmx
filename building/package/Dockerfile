FROM ibm-semeru-runtimes:open-21.0.1_12-jre-jammy

ARG NAME=app
ARG ID=1001
RUN addgroup --gid $ID $NAME && adduser --disabled-password --uid $ID --ingroup $NAME --home /home/$NAME $NAME
USER $NAME

COPY *.jar app.jar
CMD ["java", "-jar", "app.jar"]
