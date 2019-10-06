FROM adoptopenjdk/openjdk11

MAINTAINER Neeraj Jain "https://hub.docker.com/neerajjain"

RUN mkdir -p /tmp/not-started &&\
    mkdir -p /tmp/started &&\
    mkdir -p /tmp/finished

ADD positionsimulator-0.0.1-SNAPSHOT.jar positionsimulator.jar

CMD ["java", "-jar", "positionsimulator.jar"]
