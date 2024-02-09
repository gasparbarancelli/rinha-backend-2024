FROM eclipse-temurin:21.0.2_13-jdk-jammy

ENV H2_ZIP https://github.com/h2database/h2database/releases/download/version-2.2.224/h2-2023-09-17.zip
ENV H2_PORT 9092
ENV H2_CONSOLE_PORT 8082
ENV H2_DATA /var/lib/h2

RUN apt-get update && apt-get upgrade -y && apt-get install -y unzip && \
    wget ${H2_ZIP} && unzip -q h2-2023-09-17.zip -d /opt/ && \
    mkdir ${H2_DATA}

EXPOSE ${H2_PORT} ${H2_CONSOLE_PORT}

WORKDIR ${H2_DATA}

CMD java -XX:CompileThreshold=1 -XX:MaxRAMPercentage=75 -XX:ActiveProcessorCount=2 -XX:+UseParallelGC -cp /opt/h2/bin/h2*.jar org.h2.tools.Server \
    -baseDir ${H2_DATA} -ifNotExists \
    -tcp -tcpAllowOthers -tcpPort ${H2_PORT} \
    -web -webAllowOthers -webPort ${H2_CONSOLE_PORT}