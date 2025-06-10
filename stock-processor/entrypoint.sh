#!/usr/bin/env sh
set -eux

echo "▶︎ Launching Flink job..."

exec bin/flink run \
  -m jobmanager:8081 \
  -c tuddi.stock.processor.Application \
  usrlib/stock-processor.jar \
  -- \
  --bootstrapServers "$KAFKA_BOOTSTRAP_SERVER" \
  --sink.influx.host "$INFLUX_DB_SINK_HOST" \
  --sink.influx.token "$INFLUX_DB_SINK_TOKEN" \
  --sink.influx.bucketName "$INFLUX_DB_STOCK_ANALYSIS_BUCKET_NAME" \
  --sink.influx.organization "UPB" \
  --sink.influx.predictions.bucketName "$INFLUX_DB_STOCK_PREDICTIONS_BUCKET_NAME" \
  --mysql.stockDb.username "$MYSQL_STOCK_DB_USERNAME" \
  --mysql.stockDb.password "$MYSQL_STOCK_DB_PASSWORD" \
  --mysql.stockDb.url "$MYSQL_STOCK_DB_URL"