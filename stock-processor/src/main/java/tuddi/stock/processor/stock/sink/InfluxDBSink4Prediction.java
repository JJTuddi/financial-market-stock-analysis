package tuddi.stock.processor.stock.sink;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.write.Point;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import tuddi.stock.processor.stock.prediction.Prediction;

import java.util.Map;

public class InfluxDBSink4Prediction extends RichSinkFunction<Prediction> {

    private transient InfluxDBClient influxDBClient;
    private transient String bucketName;
    private transient String organization;

    @Override
    public void open(Configuration parameters) throws Exception {
        Map<String, String> params = getRuntimeContext().getExecutionConfig().getGlobalJobParameters().toMap();

        String url = params.get("sink.influx.host");
        if (StringUtils.isBlank(url)) throw new RuntimeException("The influx host is missing");
        String token = params.get("sink.influx.token");
        if (StringUtils.isBlank(token)) throw new RuntimeException("The influx token is missing");
        bucketName = params.get("sink.influx.predictions.bucketName");
        if (StringUtils.isBlank(bucketName)) throw new RuntimeException("The bucket name is missing!");
        organization = params.get("sink.influx.organization");
        if (StringUtils.isBlank(organization)) throw new RuntimeException("The organization is missing!");

        influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray());
    }

    @Override
    public void invoke(Prediction value, Context context) {
        Point point = PredictionToPointMapper.map(value);
        influxDBClient.getWriteApiBlocking().writePoint(bucketName, "stock-processor", point);
    }

    @Override
    public void close() throws Exception {
        if (influxDBClient != null) influxDBClient.close();
    }
}
