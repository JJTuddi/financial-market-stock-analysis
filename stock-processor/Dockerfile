FROM apache/flink:1.18.1-scala_2.12-java17

WORKDIR /opt/flink

COPY entrypoint.sh        ./entrypoint.sh
RUN chmod +x entrypoint.sh

COPY target/stock-processor.jar /opt/flink/usrlib/stock-processor.jar

ENTRYPOINT ["sh", "-c", "./entrypoint.sh"]
