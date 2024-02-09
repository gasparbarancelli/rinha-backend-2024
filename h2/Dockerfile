FROM ghcr.io/graalvm/graalvm-ce:21.3.3

ENV H2_ZIP https://github.com/h2database/h2database/releases/download/version-2.2.224/h2-2023-09-17.zip
ENV H2_PORT 9092
ENV H2_CONSOLE_PORT 8082
ENV H2_DATA /var/lib/h2

RUN microdnf install -y unzip && \
    microdnf install -y wget && \
    wget ${H2_ZIP} && unzip -q h2-2023-09-17.zip -d /opt/ && \
    mkdir ${H2_DATA}

EXPOSE ${H2_PORT} ${H2_CONSOLE_PORT}

WORKDIR ${H2_DATA}

CMD java -cp /opt/h2/bin/h2*.jar org.h2.tools.Server \
    -baseDir ${H2_DATA} -ifNotExists \
    -tcp -tcpAllowOthers -tcpPort ${H2_PORT} \
    -web -webAllowOthers -webPort ${H2_CONSOLE_PORT}