package tuddi.stock.processor.stock.function;

import org.apache.flink.api.common.functions.ReduceFunction;
import tuddi.stock.processor.stock.data.StockSummaryStatistics;

public class StocksReducer implements ReduceFunction<StockSummaryStatistics> {

    private final int period;

    public StocksReducer(int period) {
        this.period = period;
    }

    @Override
    public StockSummaryStatistics reduce(StockSummaryStatistics value1, StockSummaryStatistics value2) throws Exception {
        assert value1 != null : "Value 1 should be different from null";
        assert value2 != null : "Value 2 should be different form null";
        assert value1.stockName.equals(value2.stockName) : "Expected the stockName to be the same";
        
        StockSummaryStatistics result = new StockSummaryStatistics();

        result.stockName = value1.stockName;

        result.count = value1.count + value2.count;

        result.period = period;

        result.lowSum = value1.lowSum + value2.lowSum;
        result.openSum = value1.openSum + value2.openSum;
        result.volumeSum = value1.volumeSum + value2.volumeSum;
        result.highSum = value1.highSum + value2.highSum;
        result.closeSum = value1.closeSum + value2.closeSum;

        result.lowSumOfSquares = value1.lowSumOfSquares + value2.lowSumOfSquares;
        result.openSumOfSquares = value1.openSumOfSquares + value2.openSumOfSquares;
        result.volumeSumOfSquares = value1.volumeSumOfSquares + value2.volumeSumOfSquares;
        result.highSumOfSquares = value1.highSumOfSquares + value2.highSumOfSquares;
        result.closeSumOfSquares = value1.closeSumOfSquares + value2.closeSumOfSquares;

        result.lowMin = Math.min(value1.lowMin, value2.lowMin);
        result.highMin = Math.min(value1.highMin, value2.highMin);
        result.openMin = Math.min(value1.openMin, value2.openMin);
        result.closeMin = Math.min(value1.closeMin, value2.closeMin);
        result.volumeMin = Math.min(value1.volumeMin, value2.volumeMin);

        result.lowMax = Math.max(value1.lowMax, value2.lowMax);
        result.highMax = Math.max(value1.highMax, value2.highMax);
        result.openMax = Math.max(value1.openMax, value2.openMax);
        result.closeMax = Math.max(value1.closeMax, value2.closeMax);
        result.volumeMax = Math.max(value1.volumeMax, value2.volumeMax);

        // taking the minimum timestamp for the first
        if (value1.firstTimestamp < value2.firstTimestamp) {
            result.firstTimestamp = value1.firstTimestamp;
            result.firstClose = value1.firstClose;
            result.firstOpen = value1.firstOpen;
        } else {
            result.firstTimestamp = value2.firstTimestamp;
            result.firstClose = value2.firstClose;
            result.firstOpen = value2.firstOpen;
        }

        // taking the maximum timestamp for the last
        if (value1.lastTimestamp > value2.lastTimestamp) {
            result.lastTimestamp = value1.lastTimestamp;
            result.lastClose = value1.lastClose;
            result.lastOpen = value1.lastOpen;
        } else {
            result.lastTimestamp = value2.lastTimestamp;
            result.lastClose = value2.lastClose;
            result.lastOpen = value2.lastOpen;
        }

        result.timestamp = Math.max(value1.timestamp, value2.timestamp);

        return result;
    }

}
