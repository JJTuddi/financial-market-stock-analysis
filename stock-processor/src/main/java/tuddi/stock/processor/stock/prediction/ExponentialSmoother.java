package tuddi.stock.processor.stock.prediction;

public class ExponentialSmoother {

    private final double alpha;

    private boolean firstRun = false;
    private double result;

    public ExponentialSmoother(double alpha) {
        this.alpha = alpha;
    }

    public double updateAndPredict(double value) {

        if (firstRun) {
            firstRun = false;
            result = value;
        } else {
            result = alpha * value + (1.0 - alpha) * result;
        }

        return result;
    }

}
