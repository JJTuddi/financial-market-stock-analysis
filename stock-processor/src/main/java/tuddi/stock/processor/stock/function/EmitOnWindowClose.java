package tuddi.stock.processor.stock.function;

import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuddi.stock.processor.stock.data.StockSummaryStatistics;

public class EmitOnWindowClose extends ProcessWindowFunction<StockSummaryStatistics, StockSummaryStatistics, String, TimeWindow> {

    private static Logger logger = LoggerFactory.getLogger(EmitOnWindowClose.class);

    private final int minCount;

    public EmitOnWindowClose(int minCount) {
        this.minCount = minCount;
    }

    @Override
    public void process(String key, Context context, Iterable<StockSummaryStatistics> elements, Collector<StockSummaryStatistics> out) {
        for (StockSummaryStatistics stat : elements) {
            if (stat.count < minCount) {
                logger.debug("The number of stocks in statistics is too low, going to drop it: [{}]", stat);
            } else {
                logger.debug("Emitting from window [{}-{}]", context.window().getStart(), context.window().getEnd());
                out.collect(stat);
            }
        }
    }

}