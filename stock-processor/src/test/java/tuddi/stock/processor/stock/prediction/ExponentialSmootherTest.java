package tuddi.stock.processor.stock.prediction;

import org.junit.jupiter.api.Test;
import tuddi.stock.processor.util.PlotUtil;
import tuddi.stock.processor.util.TestUtil;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

class ExponentialSmootherTest {

    // Min from here is alpha 0.8
    // Looks like the error is around 3% for 0.8, which is ok
    @Test
    void givenTheAppleStock_theMoreItRuns_lowerItShouldGetTheError() throws Exception {
        int n = 280;
        double[] stockCloseValues = TestUtil.readStockFromFile("AAPL-big", n, () -> 0).stream()
                .mapToDouble(stock -> stock.close)
                .toArray();

        Map<String, double[][]> dataset = new HashMap<>();
        for (double alpha : new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9}) {
//        for (double alpha : new double[]{0.7, 0.8, 0.9}) {
            ExponentialSmoother exponentialSmoother = new ExponentialSmoother(alpha);

            double[] errors = new double[n];
            double[] x = new double[n];
            for (int i = 1; i < n; ++i) {
                x[i] = i;
                double result = exponentialSmoother.updateAndPredict(stockCloseValues[i - 1]);
                errors[i] = 100.0 * Math.abs(result - stockCloseValues[i]) / stockCloseValues[i]; // percentual
            }

            dataset.put("alpha=" + alpha, new double[][]{x, errors});
        }

        SwingUtilities.invokeAndWait(() -> {
            PlotUtil.plot("Exponential smoothing", dataset, "moment", "error");
        });
        Thread.sleep(50000);
    }

}