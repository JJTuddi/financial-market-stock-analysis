package tuddi.stock.processor.stock.data;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DonchianChannels {

    private static final double ERR = 1;

    public double lower;
    public double upper;

    public static DonchianChannels of(double rollingMin, double rollingMax) {
        DonchianChannels result = new DonchianChannels();

        result.lower = rollingMin;
        result.upper = rollingMax;

        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DonchianChannels{");
        sb.append("lower=").append(lower);
        sb.append(", upper=").append(upper);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DonchianChannels that)) return false;

        return Math.abs(lower - that.lower) < ERR &&
               Math.abs(upper - that.upper) < ERR;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(lower)
                .append(upper)
                .toHashCode();
    }
}
