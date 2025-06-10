package tuddi.stock.processor.util;

public final class MathUtil {

    public static double computeStandardDeviation(double[] nums) {
        double mean = 0;
        for (double num: nums) {
            mean += num;
        }
        mean /= nums.length;
        double var = 0;
        for (double num: nums) {
            var += Math.pow(num - mean, 2);
        }
        var /= nums.length;

        return Math.sqrt(var);
    }

    public static double computeStandardDeviation(double sum, double sumOfSquares, int count) {
        double mean = sum / count;
        double varianceOfVelocity = (sumOfSquares / count) - (mean * mean);

        return Math.sqrt(varianceOfVelocity);
    }

    private MathUtil() {
        throw new IllegalAccessError("This class should not be instantiated!");
    }

}
