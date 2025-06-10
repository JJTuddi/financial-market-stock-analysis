package tuddi.stock.processor.stock.prediction.function.map;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import tuddi.stock.processor.stock.data.Stock;
import tuddi.stock.processor.stock.prediction.Prediction;

import java.util.Random;

// Multiple parameters, also using the volume, low, high, and others
public class OnlineSgdRegressor extends RichMapFunction<Stock, Prediction> {

    private final double learningRate;

    public OnlineSgdRegressor(double learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public Prediction map(Stock stock) throws Exception {
        ValueStateDescriptor<double[]> weightsDescriptor = new ValueStateDescriptor<>(
                stock.stockName + "-onlineSgdRegressor-weights",
                double[].class
        );
        ValueState<double[]> weightsState = getRuntimeContext().getState(weightsDescriptor);
        ValueStateDescriptor<double[]> lastStockValue = new ValueStateDescriptor<>(
                stock.stockName + "-onlineSgdRegressor-lastStockValue",
                double[].class
        );
        ValueState<double[]> lastStockState = getRuntimeContext().getState(lastStockValue);

        double[] weights = weightsState.value();
        if (weights == null) {
            Random random = new Random(42);
            weights = new double[] {
                    (random.nextDouble() - 0.5) * 2, // stock.low;
                    (random.nextDouble() - 0.5) * 2, // stock.high;
                    (random.nextDouble() - 0.5) * 2, // stock.open;
                    (random.nextDouble() - 0.5) * 2, // stock.volume;
                    (random.nextDouble() - 0.5) * 2, // stock.close;
                    (random.nextDouble() - 0.5) * 2, // bias
            };
        }

        double[] X = new double[] {
                stock.low,
                stock.high,
                stock.open,
                stock.volume,
                stock.close,
                1
        };

        double error = Double.POSITIVE_INFINITY;
        double percentageError = 100.0;

        double[] lastStock = lastStockState.value();
        if (lastStock != null) {
            double predictionOnLast = 0.0;
            for (int i = 0; i < 5; ++i) {
                predictionOnLast += weights[i] * lastStock[i];
            }
            error = stock.close - predictionOnLast;
            percentageError = Math.abs(error) / stock.close;

            for (int i = 0; i < 5; i++) {
                weights[i] += learningRate * error * lastStock[i];
            }
        }


        double predictedNext = 0.0;
        for (int i = 0; i < 5; ++i) {
            predictedNext += weights[i] * X[i];
        }

        lastStockState.update(X);
        weightsState.update(weights);

        return new Prediction(
                stock.stockName,
                "sgdRegressor",
                stock.timestamp,
                stock.close,
                predictedNext,
                error,
                percentageError
        );
    }

}
