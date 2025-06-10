package tuddi.stock.processor.stock.prediction.function.map;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import tuddi.stock.processor.stock.data.Stock;
import tuddi.stock.processor.stock.prediction.Prediction;

// Simple regressor, more or less a simple perceptron xD
public class OnlineOneDimensionRegressor extends RichMapFunction<Stock, Prediction> {

    private final double learningRate;

    public OnlineOneDimensionRegressor(double learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public Prediction map(Stock stock) throws Exception {
        ValueStateDescriptor<Double> lastCloseDescriptor = new ValueStateDescriptor<>(
                stock.stockName + "-oneDimensionRegressor-lastClose",
                Double.class
        );
        ValueState<Double> lastCloseState = getRuntimeContext().getState(lastCloseDescriptor);

        ValueStateDescriptor<double[]> weightsDescriptor = new ValueStateDescriptor<>(
                stock.stockName + "-oneDimensionRegressor-weights",
                double[].class
        );
        ValueState<double[]> weightsState = getRuntimeContext().getState(weightsDescriptor);

        double close = stock.close;
        double error = Double.POSITIVE_INFINITY;
        double percentageError = 100.0;

        double[] weights = weightsState.value();
        if (weights == null) {
            weights = new double[]{1.0, 0.0};
        }

        double w = weights[0];
        double b = weights[1];

        Double lastClose = lastCloseState.value();
        if (lastClose != null) {
            double predictionOnLast = w * lastClose + b;
            error = close - predictionOnLast;
            percentageError = Math.abs(error) / close;

            w += learningRate * error * lastClose;
            b += learningRate * error;
        }

        double predictedNext = w * close + b;

        weightsState.update(new double[]{w, b});
        lastCloseState.update(stock.close);

        return new Prediction(
                stock.stockName,
                "oneDimensionRegressor",
                stock.timestamp,
                stock.close,
                predictedNext,
                error,
                percentageError
        );
    }

}
