package tuddi.stock.processor;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.util.TestStreamEnvironment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tuddi.stock.processor.stock.data.Stock;
import tuddi.stock.processor.stock.data.StockStatistics;
import tuddi.stock.processor.stock.data.StockSummaryStatistics;
import tuddi.stock.processor.util.StockBuilder;
import tuddi.stock.processor.util.TestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationRollingTest {

    @Nested
    class OneWeekRollingStatistics {
        @Test
        void givenAListOfStockValues_shouldComputeTheirRolling() throws Exception {
            List<Stock> stockList = TestUtil.readStockFromFile("AAPL", 7, TestUtil.OneSecondApartTimeSupplier.getInstance());

            StreamExecutionEnvironment env = TestStreamEnvironment.getExecutionEnvironment();
            DataStream<Stock> stocks = env
                    .fromCollection(stockList)
                    .assignTimestampsAndWatermarks(
                            WatermarkStrategy
                                    .<Stock>forMonotonousTimestamps()
                                    .withTimestampAssigner((event, timestamp) -> event.timestamp)
                    );

            DataStream<StockSummaryStatistics> statistics = Application.readStockStatistics(stocks);
            DataStream<StockSummaryStatistics> stockStatisticsDataStream = Application.stockSummaryStatisticsRolling1Week(statistics);

            List<StockSummaryStatistics> actual = new ArrayList<>();
            stockStatisticsDataStream.executeAndCollect().forEachRemaining(actual::add);

            List<StockSummaryStatistics> expected = List.of(
                    StockBuilder.buildSummaryStatistic("AAPL", 7, 7,
                            1065.3700103759766, 1076.8399810791016, 465347400.0, 1088.6699981689453, 1081.1699981689453,
                            162156.48389106453, 165679.1151871339, 3.160345382336e+16, 169332.34935323498, 167016.9807452883,
                            7000,
                            150.3699951171875, 153.08999633789062, 151.60000610351562, 151.1199951171875, 53623900,
                            154.41000366210938, 157.63999938964844, 157.35000610351562, 156.97999572753906, 81378700,
                            1000, 151.1199951171875, 153.0399932861328,
                            7000, 156.97999572753906, 157.35000610351562
                    )
            );
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void givenAListOfStockValues_shouldComputeTheStatisticsIncludingTheBollingerBandsAndDonchianChain() throws Exception {
            List<Stock> stockList = TestUtil.readStockFromFile("AAPL", 7, TestUtil.OneSecondApartTimeSupplier.getInstance());

            StreamExecutionEnvironment env = TestStreamEnvironment.getExecutionEnvironment();
            DataStream<Stock> stocks = env
                    .fromCollection(stockList)
                    .assignTimestampsAndWatermarks(
                            WatermarkStrategy
                                    .<Stock>forMonotonousTimestamps()
                                    .withTimestampAssigner((event, timestamp) -> event.timestamp)
                    );

            DataStream<StockSummaryStatistics> statistics = Application.readStockStatistics(stocks);
            DataStream<StockSummaryStatistics> stockStatisticsDataStream = Application.stockSummaryStatisticsRolling1Week(statistics);
            DataStream<StockStatistics> finalStockStatistics = Application.stockFinalStatistics(stockStatisticsDataStream, "1Week");

            List<StockStatistics> actual = new ArrayList<>();
            finalStockStatistics.executeAndCollect().forEachRemaining(actual::add);

            int count = 7;
            // mean
            double lowMean = 152.195716;
            double openMean = 153.834283;
            double volumeMean = 6.647820e+07;
            double highMean = 155.524285;
            double closeMean = 154.452857;
            // std
            double lowStd = 1.398368;
            double openStd = 2.008680;
            double volumeStd = 1.055143e+07;
            double highStd = 1.718807;
            double closeStd = 2.128605;
            // min
            double lowMin = 150.369995;
            double openMin = 151.119995;
            double volumeMin = 5.362390e+07;
            double highMin = 153.089996;
            double closeMin = 151.600006;
            // max
            double lowMax = 154.410004;
            double openMax = 156.979996;
            double volumeMax = 8.137870e+07;
            double highMax = 157.639999;
            double closeMax = 157.350006;
            // first
            double firstOpen = 151.119995;
            double firstClose = 153.039993;
            // last
            double lastOpen = 156.979996;
            double lastClose = 157.350006;
            List<StockStatistics> expected = List.of(
                    StockBuilder.buildStatistic(count, 7, "AAPL", 7000,
                        /*mean*/ lowMean, openMean, volumeMean, highMean, closeMean,
                        /*std*/ lowStd, openStd, volumeStd, highStd, closeStd,
                        /*min*/ lowMin, openMin, volumeMin, highMin, closeMin,
                        /*max*/ lowMax, openMax, volumeMax, highMax, closeMax,
                        /*first*/ firstOpen, firstClose,
                        /*last*/lastOpen, lastClose,
                        /*first and last timestamps*/ 1000, 7000
                    )
            );
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class OneMonthRollingStatistics {
        @Test
        void givenAListOfStockValuesForOneWeek_theNumberOfValuesIsSmallerThanTheWindowMinimumSize_shouldReturnEmptyList() throws Exception {
            List<Stock> stockList = TestUtil.readStockFromFile("AAPL", 7, TestUtil.OneSecondApartTimeSupplier.getInstance());

            StreamExecutionEnvironment env = TestStreamEnvironment.getExecutionEnvironment();
            DataStream<Stock> stocks = env
                    .fromCollection(stockList)
                    .assignTimestampsAndWatermarks(
                            WatermarkStrategy
                                    .<Stock>forMonotonousTimestamps()
                                    .withTimestampAssigner((event, timestamp) -> event.timestamp)
                    );

            DataStream<StockSummaryStatistics> statistics = Application.readStockStatistics(stocks);
            DataStream<StockSummaryStatistics> stockStatisticsDataStream = Application.stockSummaryStatisticsRolling1Month(statistics);

            List<StockSummaryStatistics> actual = new ArrayList<>();
            stockStatisticsDataStream.executeAndCollect().forEachRemaining(actual::add);

            assertThat(actual).isEmpty();
        }
    }

}