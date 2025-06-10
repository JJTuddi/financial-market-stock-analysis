package tuddi.stock.processor.stock.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class StockSummaryStatistics {

    public String stockName;

    public int count;
    public int period;

    // sum
    public double lowSum;
    public double openSum;
    public double volumeSum;
    public double highSum;
    public double closeSum;

    // squares
    public double lowSumOfSquares;
    public double openSumOfSquares;
    public double volumeSumOfSquares;
    public double highSumOfSquares;
    public double closeSumOfSquares;

    // min
    public double lowMin;
    public double openMin;
    public double volumeMin;
    public double highMin;
    public double closeMin;

    // max
    public double lowMax;
    public double openMax;
    public double volumeMax;
    public double highMax;
    public double closeMax;

    // first
    public long firstTimestamp;
    public double firstOpen;
    public double firstClose;

    // last
    public long lastTimestamp;
    public double lastOpen;
    public double lastClose;

    // ts
    public long timestamp;

    public String getKey() {
        return stockName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StockStatistics{");
        sb.append("stockName='").append(stockName).append('\'');
        sb.append(", count=").append(count);
        sb.append(", period=").append(period);
        // sum
        sb.append(", lowSum=").append(lowSum);
        sb.append(", openSum=").append(openSum);
        sb.append(", volumeSum=").append(volumeSum);
        sb.append(", highSum=").append(highSum);
        sb.append(", closeSum=").append(closeSum);
        // squared
        sb.append(", lowSumOfSquares=").append(lowSumOfSquares);
        sb.append(", openSumOfSquares=").append(openSumOfSquares);
        sb.append(", volumeSumOfSquares=").append(volumeSumOfSquares);
        sb.append(", highSumOfSquares=").append(highSumOfSquares);
        sb.append(", closeSumOfSquares=").append(closeSumOfSquares);
        // ts
        sb.append(", timestamp=").append(timestamp);
        // min
        sb.append(", lowMin=").append(lowMin);
        sb.append(", highMin=").append(highMin);
        sb.append(", openMin=").append(openMin);
        sb.append(", closeMin=").append(closeMin);
        sb.append(", volumeMin=").append(volumeMin);
        // max
        sb.append(", lowMax=").append(lowMax);
        sb.append(", highMax=").append(highMax);
        sb.append(", openMax=").append(openMax);
        sb.append(", closeMax=").append(closeMax);
        sb.append(", volumeMax=").append(volumeMax);
        // first
        sb.append(", firstTimestamp=").append(firstTimestamp);
        sb.append(", firstOpen=").append(firstOpen);
        sb.append(", firstClose=").append(firstClose);
        // last
        sb.append(", lastTimestamp=").append(lastTimestamp);
        sb.append(", lastOpen=").append(lastOpen);
        sb.append(", lastClose=").append(lastClose);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof StockSummaryStatistics that)) return false;

        return new EqualsBuilder()
                .append(count, that.count)
                .append(stockName, that.stockName)
                .append(period, that.period)
                // sum
                .append(lowSum, that.lowSum)
                .append(openSum, that.openSum)
                .append(volumeSum, that.volumeSum)
                .append(highSum, that.highSum)
                .append(closeSum, that.closeSum)
                // squares
                .append(lowSumOfSquares, that.lowSumOfSquares)
                .append(openSumOfSquares, that.openSumOfSquares)
                .append(volumeSumOfSquares, that.volumeSumOfSquares)
                .append(highSumOfSquares, that.highSumOfSquares)
                .append(closeSumOfSquares, that.closeSumOfSquares)
                // min
                .append(lowMin, that.lowMin)
                .append(highMin, that.highMin)
                .append(openMin, that.openMin)
                .append(closeMin, that.closeMin)
                .append(volumeMin, that.volumeMin)
                // max
                .append(lowMax, that.lowMax)
                .append(highMax, that.highMax)
                .append(openMax, that.openMax)
                .append(closeMax, that.closeMax)
                .append(volumeMax, that.volumeMax)
                // first
                .append(firstTimestamp, that.firstTimestamp)
                .append(firstOpen, that.firstOpen)
                .append(firstClose, that.firstClose)
                // last
                .append(lastTimestamp, that.lastTimestamp)
                .append(lastOpen, that.lastOpen)
                .append(lastClose, that.lastClose)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(count)
                .append(stockName)
                .append(period)
                // sum
                .append(count)
                .append(lowSum)
                .append(openSum)
                .append(volumeSum)
                .append(highSum)
                .append(closeSum)
                // squares
                .append(lowSumOfSquares)
                .append(openSumOfSquares)
                .append(volumeSumOfSquares)
                .append(highSumOfSquares)
                .append(closeSumOfSquares)
                // min
                .append(lowMin)
                .append(highMin)
                .append(openMin)
                .append(closeMin)
                .append(volumeMin)
                // max
                .append(lowMax)
                .append(highMax)
                .append(openMax)
                .append(closeMax)
                .append(volumeMax)
                // first
                .append(firstTimestamp)
                .append(firstOpen)
                .append(firstClose)
                // last
                .append(lastTimestamp)
                .append(lastOpen)
                .append(lastClose)
                .toHashCode();
    }

}
