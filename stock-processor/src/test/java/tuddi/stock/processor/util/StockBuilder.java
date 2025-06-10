package tuddi.stock.processor.util;

import tuddi.stock.processor.stock.data.*;

public final class StockBuilder {

    public static StockStatistics buildStatistic(
            int count, int period, String stockName, long timestamp,
            // mean
            double lowMean,
            double openMean,
            double volumeMean,
            double highMean,
            double closeMean,
            // std
            double lowStd,
            double openStd,
            double volumeStd,
            double highStd,
            double closeStd,
            // min
            double lowMin,
            double openMin,
            double volumeMin,
            double highMin,
            double closeMin,
            // max
            double lowMax,
            double openMax,
            double volumeMax,
            double highMax,
            double closeMax,
            // first
            double firstOpen,
            double firstClose,
            // last
            double lastOpen,
            double lastClose,
            // first and last timestamps
            long firstTimestamp,
            long lastTimestamp
    ) {
        StockStatistics result = new StockStatistics();

        result.count = count;
        result.period = period;
        result.stockName = stockName;
        result.timestamp = timestamp;

        RollingStatistics low = new RollingStatistics();
        low.rollingMean = lowMean;
        low.rollingMin = lowMin;
        low.rollingMax = lowMax;
        low.rollingStandardDeviation = lowStd;
        result.low = low;

        RollingStatistics high = new RollingStatistics();
        high.rollingMean = highMean;
        high.rollingMin = highMin;
        high.rollingMax = highMax;
        high.rollingStandardDeviation = highStd;
        result.high = high;

        RollingStatistics close = new RollingStatistics();
        close.rollingMean = closeMean;
        close.rollingMin = closeMin;
        close.rollingMax = closeMax;
        close.rollingStandardDeviation = closeStd;
        result.close = close;

        RollingStatistics volume = new RollingStatistics();
        volume.rollingMean = volumeMean;
        volume.rollingMin = volumeMin;
        volume.rollingMax = volumeMax;
        volume.rollingStandardDeviation = volumeStd;
        result.volume = volume;

        RollingStatistics open = new RollingStatistics();
        open.rollingMean = openMean;
        open.rollingMin = openMin;
        open.rollingMax = openMax;
        open.rollingStandardDeviation = openStd;
        result.open = open;

        BollingerBands lowBands = new BollingerBands();
        lowBands.middle = lowMean;
        lowBands.lower = lowMean - 2 * lowStd;
        lowBands.upper = lowMean + 2 * lowStd;
        result.lowBands = lowBands;

        BollingerBands highBands = new BollingerBands();
        highBands.middle = highMean;
        highBands.lower = highMean - 2 * highStd;
        highBands.upper = highMean + 2 * highStd;
        result.highBands = highBands;

        BollingerBands closeBands = new BollingerBands();
        closeBands.middle = closeMean;
        closeBands.lower = closeMean - 2 * closeStd;
        closeBands.upper = closeMean + 2 * closeStd;
        result.closeBands = closeBands;

        BollingerBands volumeBands = new BollingerBands();
        volumeBands.middle = volumeMean;
        volumeBands.lower = volumeMean - 2 * volumeMin;
        volumeBands.upper = volumeMean + 2 * volumeMin;
        result.volumeBands = volumeBands;

        result.openDonchianChannels = DonchianChannels.of(openMin, openMax);
        result.closeDonchianChannels = DonchianChannels.of(closeMin, closeMax);

        result.closeRollingScores = RollingScores.of(firstClose, lastClose, result.close.rollingMean, result.close.rollingStandardDeviation);
        result.openRollingScores = RollingScores.of(firstOpen, lastOpen, result.open.rollingMean, result.open.rollingStandardDeviation);

        result.firstTimestamp = firstTimestamp;
        result.lastTimestamp = lastTimestamp;

        return result;
    }

    public static StockSummaryStatistics buildSummaryStatistic(
            String stockName, int count, int period,
            double lowSum, double openSum, double volumeSum, double highSum, double closeSum,
            double lowSquaredSum, double openSquaredSum, double volumeSquaredSum, double highSquaredSum, double closeSquaredSum,
            long timestamp,
            double lowMin, double highMin, double closeMin, double openMin, double volumeMin,
            double lowMax, double highMax, double closeMax, double openMax, double volumeMax,
            long firstTimestamp, double firstOpen, double firstClose,
            long lastTimestamp, double lastOpen, double lastClose
    ) {
        StockSummaryStatistics result = new StockSummaryStatistics();

        result.stockName = stockName;

        result.count = count;

        result.period = period;

        result.lowSum = lowSum;
        result.openSum = openSum;
        result.volumeSum = volumeSum;
        result.highSum = highSum;
        result.closeSum = closeSum;

        result.lowSumOfSquares = lowSquaredSum;
        result.openSumOfSquares = openSquaredSum;
        result.volumeSumOfSquares = volumeSquaredSum;
        result.highSumOfSquares = highSquaredSum;
        result.closeSumOfSquares = closeSquaredSum;

        result.timestamp = timestamp;

        result.lowMin = lowMin;
        result.openMin = openMin;
        result.volumeMin = volumeMin;
        result.highMin = highMin;
        result.closeMin = closeMin;

        // max
        result.lowMax = lowMax;
        result.openMax = openMax;
        result.volumeMax = volumeMax;
        result.highMax = highMax;
        result.closeMax = closeMax;

        // first
        result.firstTimestamp = firstTimestamp;
        result.firstOpen = firstOpen;
        result.firstClose = firstClose;

        // last
        result.lastTimestamp = lastTimestamp;
        result.lastOpen = lastOpen;
        result.lastClose = lastClose;

        return result;
    }

}
