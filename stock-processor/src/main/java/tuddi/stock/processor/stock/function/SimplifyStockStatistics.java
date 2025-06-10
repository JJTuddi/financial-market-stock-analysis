package tuddi.stock.processor.stock.function;

import org.apache.flink.api.common.functions.MapFunction;
import tuddi.stock.processor.stock.data.*;

public class SimplifyStockStatistics implements MapFunction<StockSummaryStatistics, StockStatistics> {

    @Override
    public StockStatistics map(StockSummaryStatistics value) throws Exception {
        StockStatistics result = new StockStatistics();

        result.count = value.count;
        result.period = value.period;
        result.stockName = value.stockName;
        result.timestamp = value.timestamp;

        result.low = RollingStatistics.of(value.lowSum, value.lowSumOfSquares, value.lowMin, value.lowMax, value.count);
        result.high = RollingStatistics.of(value.highSum, value.highSumOfSquares, value.highMin, value.highMax, value.count);
        result.close = RollingStatistics.of(value.closeSum, value.closeSumOfSquares, value.closeMin, value.closeMax, value.count);
        result.volume = RollingStatistics.of(value.volumeSum, value.volumeSumOfSquares, value.volumeMin, value.volumeMax, value.count);
        result.open = RollingStatistics.of(value.openSum, value.openSumOfSquares, value.openMin, value.openMax, value.count);

        result.lowBands = BollingerBands.of(result.low.rollingMean, result.low.rollingStandardDeviation);
        result.highBands = BollingerBands.of(result.high.rollingMean, result.high.rollingStandardDeviation);
        result.closeBands = BollingerBands.of(result.close.rollingMean, result.close.rollingStandardDeviation);
        result.volumeBands = BollingerBands.of(result.volume.rollingMean, result.volume.rollingStandardDeviation);

        result.openDonchianChannels = DonchianChannels.of(value.closeMin, value.closeMax);
        result.closeDonchianChannels = DonchianChannels.of(value.closeMin, value.closeMax);

        result.closeRollingScores = RollingScores.of(value.firstClose, value.lastClose, result.close.rollingMean, result.close.rollingStandardDeviation);
        result.openRollingScores = RollingScores.of(value.firstOpen, value.lastOpen, result.open.rollingMean, result.open.rollingStandardDeviation);

        result.firstTimestamp = value.firstTimestamp;
        result.lastTimestamp = value.lastTimestamp;

        return result;
    }

}
