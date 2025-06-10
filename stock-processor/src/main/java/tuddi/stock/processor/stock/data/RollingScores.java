package tuddi.stock.processor.stock.data;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RollingScores {

    private static final double ERR = 1.0;

    public double rollingReturn;
    public double rollingLogReturn;
    public double zScore;
    public double rollingMomentum;

    public static RollingScores of(double firstValue, double lastValue, double rollingMean, double rollingStandardDeviation) {
        RollingScores result = new RollingScores();

        result.rollingReturn = lastValue / firstValue - 1.0;
        result.rollingLogReturn = Math.log10(lastValue) - Math.log10(firstValue);
        result.zScore = (lastValue - rollingMean) / rollingStandardDeviation;
        result.rollingMomentum = result.rollingReturn * 100;

        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RollingOpenStatistics{");
        sb.append("rollingReturn=").append(rollingReturn);
        sb.append(", rollingLogReturn=").append(rollingLogReturn);
        sb.append(", zScore=").append(zScore);
        sb.append(", rollingMomentum=").append(rollingMomentum);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof RollingScores that)) return false;

        return Math.abs(rollingReturn - that.rollingReturn) < ERR &&
               Math.abs(rollingLogReturn - that.rollingLogReturn) < ERR &&
               Math.abs(zScore - that.zScore) < ERR &&
               Math.abs(rollingMomentum - that.rollingMomentum) < ERR;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(rollingReturn)
                .append(rollingLogReturn)
                .append(zScore)
                .append(rollingMomentum)
                .toHashCode();
    }
}
