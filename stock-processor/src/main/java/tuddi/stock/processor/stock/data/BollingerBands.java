package tuddi.stock.processor.stock.data;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class BollingerBands {

    private static final double ERR = 1.0;

    public double lower;
    public double middle;
    public double upper;

    public static BollingerBands of(double rollingMean, double rollingStandardDeviation) {
        BollingerBands result = new BollingerBands();

        result.middle = rollingMean;
        result.lower = rollingMean - 2 * rollingStandardDeviation;
        result.upper = rollingMean + 2 * rollingStandardDeviation;

        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BollingerBands{");
        sb.append("lower=").append(lower);
        sb.append(", middle=").append(middle);
        sb.append(", upper=").append(upper);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof BollingerBands that)) return false;

        return Math.abs(lower - that.lower) < ERR &&
               Math.abs(middle - that.middle) < ERR &&
               Math.abs(upper - that.upper) < ERR;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(lower)
                .append(middle)
                .append(upper)
                .toHashCode();
    }
}
