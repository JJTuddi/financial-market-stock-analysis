package tuddi.stock.processor.stock.sink;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import tuddi.stock.processor.stock.data.*;

public class StockStatisticToPointMapper {

    public static Point map(StockStatistics stockStatistics) {
        return Point.measurement("stock-statistic")
                .time(stockStatistics.timestamp, WritePrecision.MS)
                .addTag("stockName", stockStatistics.stockName)
                .addField("firstTimestamp", stockStatistics.firstTimestamp)
                .addField("lastTimestamp", stockStatistics.lastTimestamp)
                .addField("count", stockStatistics.count)
                .addField("period", stockStatistics.period)
                // close bands
                .addField("closeBands_lower", stockStatistics.closeBands.lower)
                .addField("closeBands_middle", stockStatistics.closeBands.middle)
                .addField("closeBands_upper", stockStatistics.closeBands.upper)
                // low bands
                .addField("lowBands_lower", stockStatistics.lowBands.lower)
                .addField("lowBands_middle", stockStatistics.lowBands.middle)
                .addField("lowBands_upper", stockStatistics.lowBands.upper)
                // high bands
                .addField("highBands_lower", stockStatistics.highBands.lower)
                .addField("highBands_middle", stockStatistics.highBands.middle)
                .addField("highBands_upper", stockStatistics.highBands.upper)
                // volume bands
                .addField("volumeBands_lower", stockStatistics.volumeBands.lower)
                .addField("volumeBands_middle", stockStatistics.volumeBands.middle)
                .addField("volumeBands_upper", stockStatistics.volumeBands.upper)
                // close rolling statistics
                .addField("closeRollingStatistics_mean", stockStatistics.close.rollingMean)
                .addField("closeRollingStatistics_standardDeviation", stockStatistics.close.rollingStandardDeviation)
                .addField("closeRollingStatistics_min", stockStatistics.close.rollingMin)
                .addField("closeRollingStatistics_max", stockStatistics.close.rollingMax)
                // low rolling statistics
                .addField("lowRollingStatistics_mean", stockStatistics.low.rollingMean)
                .addField("lowRollingStatistics_standardDeviation", stockStatistics.low.rollingStandardDeviation)
                .addField("lowRollingStatistics_min", stockStatistics.low.rollingMin)
                .addField("lowRollingStatistics_max", stockStatistics.low.rollingMax)
                // high rolling statistics
                .addField("highRollingStatistics_mean", stockStatistics.high.rollingMean)
                .addField("highRollingStatistics_standardDeviation", stockStatistics.high.rollingStandardDeviation)
                .addField("highRollingStatistics_min", stockStatistics.high.rollingMin)
                .addField("highRollingStatistics_max", stockStatistics.high.rollingMax)
                // open rolling statistics
                .addField("openRollingStatistics_mean", stockStatistics.open.rollingMean)
                .addField("openRollingStatistics_standardDeviation", stockStatistics.open.rollingStandardDeviation)
                .addField("openRollingStatistics_min", stockStatistics.open.rollingMin)
                .addField("openRollingStatistics_max", stockStatistics.open.rollingMax)
                // volume rolling statistics
                .addField("volumeRollingStatistics_mean", stockStatistics.volume.rollingMean)
                .addField("volumeRollingStatistics_standardDeviation", stockStatistics.volume.rollingStandardDeviation)
                .addField("volumeRollingStatistics_min", stockStatistics.volume.rollingMin)
                .addField("volumeRollingStatistics_max", stockStatistics.volume.rollingMax)
                // open donchian channels
                .addField("openDonchianChannels_lower", stockStatistics.openDonchianChannels.lower)
                .addField("openDonchianChannels_upper", stockStatistics.openDonchianChannels.upper)
                // close donchian channels
                .addField("closeDonchianChannels_lower", stockStatistics.closeDonchianChannels.lower)
                .addField("closeDonchianChannels_upper", stockStatistics.closeDonchianChannels.upper)
                // open rolling scores
                .addField("openRollingScores_rollingReturn", stockStatistics.openRollingScores.rollingReturn)
                .addField("openRollingScores_rollingLogReturn", stockStatistics.openRollingScores.rollingLogReturn)
                .addField("openRollingScores_zScore", stockStatistics.openRollingScores.zScore)
                .addField("openRollingScores_rollingMomentum", stockStatistics.openRollingScores.rollingMomentum)
                // close rolling scores
                .addField("closeRollingScores_rollingReturn", stockStatistics.closeRollingScores.rollingReturn)
                .addField("closeRollingScores_rollingLogReturn", stockStatistics.closeRollingScores.rollingLogReturn)
                .addField("closeRollingScores_zScore", stockStatistics.closeRollingScores.zScore)
                .addField("closeRollingScores_rollingMomentum", stockStatistics.closeRollingScores.rollingMomentum);
    }

}
