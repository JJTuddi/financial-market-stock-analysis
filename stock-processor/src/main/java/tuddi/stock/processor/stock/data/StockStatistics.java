package tuddi.stock.processor.stock.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class StockStatistics {

    public int count;
    public int period;
    public String stockName;
    public long timestamp;

    // close - general trend tracking of trade
    public BollingerBands closeBands;
    // low - bottom bound, good for mean reversion strategies
    public BollingerBands lowBands;
    // high - upper bound of volatility or momentum
    public BollingerBands highBands;
    // volume - for volatility bands on activity, not price
    public BollingerBands volumeBands;

    public RollingStatistics close;
    public RollingStatistics low;
    public RollingStatistics high;
    public RollingStatistics open;
    public RollingStatistics volume;

    public DonchianChannels openDonchianChannels;
    public DonchianChannels closeDonchianChannels;

    public RollingScores openRollingScores;
    public RollingScores closeRollingScores;

    public long firstTimestamp;
    public long lastTimestamp;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StockStatistics{");
        sb.append("count=").append(count);
        sb.append(", period='").append(period);
        sb.append(", stockName='").append(stockName).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", closeBands=").append(closeBands);
        sb.append(", lowBands=").append(lowBands);
        sb.append(", highBands=").append(highBands);
        sb.append(", volumeBands=").append(volumeBands);
        sb.append(", close=").append(close);
        sb.append(", low=").append(low);
        sb.append(", high=").append(high);
        sb.append(", open=").append(open);
        sb.append(", volume=").append(volume);
        sb.append(", openDonchianChannels=").append(openDonchianChannels);
        sb.append(", closeDonchianChannels=").append(closeDonchianChannels);
        sb.append(", openRollingScores=").append(openRollingScores);
        sb.append(", closeRollingScores=").append(closeRollingScores);
        // timestamps
        sb.append(", firstTimestamp=").append(firstTimestamp);
        sb.append(", lastTimestamp=").append(lastTimestamp);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof StockStatistics that)) return false;

        return new EqualsBuilder()
                .append(count, that.count)
                .append(period, that.period)
                .append(stockName, that.stockName)
                .append(timestamp, that.timestamp)
//                // Bollinger Bands
                .append(closeBands, that.closeBands)
                .append(lowBands, that.lowBands)
                .append(highBands, that.highBands)
//                .append(volumeBands, that.volumeBands) // the introduced error is too big...
                // donchianChannels
                .append(openDonchianChannels, that.openDonchianChannels)
                .append(closeDonchianChannels, that.closeDonchianChannels)
//                // rolling statistics
                .append(close, that.close)
                .append(low, that.low)
                .append(high, that.high)
                .append(open, that.open)
//                .append(volume, that.volume) // the introduced err is too big...
                // rolling
                .append(closeRollingScores, that.closeRollingScores)
                .append(openRollingScores, that.openRollingScores)
                // timestamps
                .append(firstTimestamp, that.firstTimestamp)
                .append(lastTimestamp, that.lastTimestamp)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(count)
                .append(period)
                .append(stockName)
                .append(timestamp)
                // Bollinger Bands
                .append(closeBands)
                .append(lowBands)
                .append(highBands)
                .append(volumeBands)
                // donchianChannels
                .append(openDonchianChannels)
                .append(closeDonchianChannels)
                // rolling statistics
                .append(close)
                .append(low)
                .append(high)
                .append(close)
                .append(open)
                .append(volume)
                // rolling
                .append(closeRollingScores)
                .append(openRollingScores)
                // timestamps
                .append(firstTimestamp)
                .append(lastTimestamp)
                .toHashCode();
    }
}
