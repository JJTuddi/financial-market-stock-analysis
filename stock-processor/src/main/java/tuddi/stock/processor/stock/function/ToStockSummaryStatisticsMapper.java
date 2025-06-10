package tuddi.stock.processor.stock.function;

import org.apache.flink.api.common.functions.MapFunction;
import tuddi.stock.processor.stock.data.Stock;
import tuddi.stock.processor.stock.data.StockSummaryStatistics;

public class ToStockSummaryStatisticsMapper implements MapFunction<Stock, StockSummaryStatistics> {

    @Override
    public StockSummaryStatistics map(Stock value) {
        StockSummaryStatistics result = new StockSummaryStatistics();

        result.stockName = value.stockName;

        result.count = 1;

        result.lowSum = value.low;
        result.openSum = value.open;
        result.volumeSum = value.volume;
        result.highSum = value.high;
        result.closeSum = value.close;

        result.lowSumOfSquares = value.low * value.low;
        result.openSumOfSquares = value.open * value.open;
        result.volumeSumOfSquares = value.volume * value.volume;
        result.highSumOfSquares = value.high * value.high;
        result.closeSumOfSquares = value.close * value.close;

        // min
        result.lowMin = value.low;
        result.openMin = value.open;
        result.volumeMin = value.volume;
        result.highMin = value.high;
        result.closeMin = value.close;

        // max
        result.lowMax = value.low;
        result.openMax = value.open;
        result.volumeMax = value.volume;
        result.highMax = value.high;
        result.closeMax = value.close;

        // first
        result.firstTimestamp = value.timestamp;
        result.firstOpen = value.open;
        result.firstClose = value.close;

        // last
        result.lastTimestamp = value.timestamp;
        result.lastOpen = value.open;
        result.lastClose = value.close;

        result.timestamp = value.timestamp;

        return result;
    }

}
