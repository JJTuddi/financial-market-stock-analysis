package tuddi.stock.processor.stock.prediction.function.map;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import tuddi.stock.processor.stock.data.Stock;
import tuddi.stock.processor.stock.prediction.Prediction;

// Just a simple exponential smoother, it has some nice results (but not on stock xD)
public class ExponentialSmootherRegressor extends RichMapFunction<Stock, Prediction> {

    private final double alpha;

    public ExponentialSmootherRegressor(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public Prediction map(Stock stock) throws Exception {
        ValueStateDescriptor<Double> predictionDescriptor = new ValueStateDescriptor<>(
                stock.stockName + "-exponentialSmoother-prediction",
                Double.class
        );
        ValueState<Double> predictionState = getRuntimeContext().getState(predictionDescriptor);

        double error = Double.POSITIVE_INFINITY;
        double percentageError = 100.0;

        double prediction = stock.close;
        Double predictionWrap = predictionState.value();
        if (predictionWrap != null) {
            prediction = predictionWrap;

            error = stock.close - prediction;
            percentageError = Math.abs(error) / stock.close;
        }

        prediction = alpha * stock.close + (1.0 - alpha) * prediction;

        predictionState.update(prediction);

        return new Prediction(
                stock.stockName,
                "exponentialSmoother",
                stock.timestamp,
                stock.close,
                prediction,
                error,
                percentageError
        );
    }

}
