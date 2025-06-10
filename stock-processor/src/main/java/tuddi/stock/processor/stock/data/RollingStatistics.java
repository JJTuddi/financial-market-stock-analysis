package tuddi.stock.processor.stock.data;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import tuddi.stock.processor.util.MathUtil;

public class RollingStatistics {

    private static final double ERR = 1.0;

    public double rollingMean;
    public double rollingStandardDeviation;
    public double rollingMax;
    public double rollingMin;

    public static RollingStatistics of(double rollingSum, double rollingSumOfSquares, double rollingMin, double rollingMax, int count) {
        RollingStatistics result = new RollingStatistics();

        result.rollingMean = rollingSum / count;
        result.rollingStandardDeviation = MathUtil.computeStandardDeviation(rollingSum, rollingSumOfSquares, count);
        result.rollingMin = rollingMin;
        result.rollingMax = rollingMax;

        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RollingStatistics{");
        sb.append("rollingMean=").append(rollingMean);
        sb.append(", rollingStandardDeviation=").append(rollingStandardDeviation);
        sb.append(", rollingMax=").append(rollingMax);
        sb.append(", rollingMin=").append(rollingMin);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof RollingStatistics that)) return false;

        return Math.abs(rollingMean - that.rollingMean) < ERR &&
               Math.abs(rollingStandardDeviation - that.rollingStandardDeviation) < ERR &&
               Math.abs(rollingMax - that.rollingMax) < ERR &&
               Math.abs(rollingMin - that.rollingMin) < ERR;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(rollingMean)
                .append(rollingStandardDeviation)
                .append(rollingMax)
                .append(rollingMin)
                .toHashCode();
    }
}
