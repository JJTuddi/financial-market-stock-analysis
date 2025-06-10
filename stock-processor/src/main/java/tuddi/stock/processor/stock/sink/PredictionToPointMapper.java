package tuddi.stock.processor.stock.sink;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import tuddi.stock.processor.stock.prediction.Prediction;

public class PredictionToPointMapper {

    public static Point map(Prediction prediction) {
        return Point.measurement("stock-prediction")
                .time(prediction.timestamp, WritePrecision.MS)
                .addTag("stockName", prediction.stockName)
                .addTag("algorithm", prediction.algorithm)
                // Close and predicted
                .addField("close", prediction.close)
                .addField("predicted", prediction.predicted)
                // Error
                .addField("error", prediction.error)
                .addField("percentageError", prediction.percentageError);
    }

}
