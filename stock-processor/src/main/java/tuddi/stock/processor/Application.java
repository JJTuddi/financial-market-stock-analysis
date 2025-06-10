/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tuddi.stock.processor;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import tuddi.stock.processor.stock.data.Stock;
import tuddi.stock.processor.stock.data.StockStatistics;
import tuddi.stock.processor.stock.data.StockSummaryStatistics;
import tuddi.stock.processor.stock.function.*;
import tuddi.stock.processor.stock.prediction.Prediction;
import tuddi.stock.processor.stock.prediction.function.map.ExponentialSmootherRegressor;
import tuddi.stock.processor.stock.prediction.function.map.OnlineOneDimensionRegressor;
import tuddi.stock.processor.stock.prediction.function.map.OnlineSgdRegressor;
import tuddi.stock.processor.stock.sink.InfluxDBSink4Prediction;
import tuddi.stock.processor.stock.sink.InfluxDBSink4StockStatistics;
import tuddi.stock.processor.stock.sink.MySqlStockSink;

import java.time.Duration;
import java.util.Arrays;

public class Application {

    public static KafkaSource<Stock> buildKafkaSource(ParameterTool parameters) {
        String bootstrapServers = parameters.get("bootstrapServers");
        if (StringUtils.isBlank(bootstrapServers)) bootstrapServers = "localhost:9092";
        String topics = "stocks";

        return KafkaSource.<Stock>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(topics)
                .setGroupId("stocks-consumers")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(new StockDeserializationSchema())
                .build();
    }

    public static DataStream<StockSummaryStatistics> readStockStatistics(DataStream<Stock> stocks) {
        return stocks
                .map(new ToStockSummaryStatisticsMapper())
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<StockSummaryStatistics>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, ts) ->  event.timestamp)
                )
                .name("stock value reader");
    }

    public static DataStream<StockSummaryStatistics> stockSummaryStatisticsRolling1Week(DataStream<StockSummaryStatistics> stockStatistics) {
        return stockStatisticsRolling(stockStatistics, 7, "Stock statistics - rolling 1 week");
    }

    public static DataStream<StockSummaryStatistics> stockSummaryStatisticsRolling2Weeks(DataStream<StockSummaryStatistics> stockStatistics) {
        return stockStatisticsRolling(stockStatistics, 14, "Stock statistics - rolling 2 weeks");
    }

    public static DataStream<StockSummaryStatistics> stockSummaryStatisticsRolling1Month(DataStream<StockSummaryStatistics> stockStatistics) {
        return stockStatisticsRolling(stockStatistics, 30, "Stock statistics - rolling 1 month");
    }

    public static DataStream<StockStatistics> stockFinalStatistics(DataStream<StockSummaryStatistics> stockSummaryStatisticsRolling, String name) {
        return stockSummaryStatisticsRolling
                .map(new SimplifyStockStatistics())
                .name(name);
    }

    public static DataStream<Prediction> createOnlineExponentialSmootherRegressorPipeline(DataStream<Stock> stockDataStream) {
        return stockDataStream
                .keyBy(stock -> stock.stockName)
                .map(new ExponentialSmootherRegressor(0.8))
                .name("exponential smoother regressor");
    }

    public static DataStream<Prediction> createOnlineOneDimensionRegressorPipeline(DataStream<Stock> stockDataStream) {
        return stockDataStream
                .keyBy(stock -> stock.stockName)
                .map(new OnlineOneDimensionRegressor(0.3))
                .name("one dimension regressor");
    }

    public static DataStream<Prediction> createOnlineSgdRegressorPipeline(DataStream<Stock> stockDataStream) {
        return stockDataStream
                .keyBy(stock -> stock.stockName)
                .map(new OnlineSgdRegressor(0.3))
                .name("sgd online regressor");
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        String[] filteredArgs = args;
        if (args.length > 0 && "--".equals(args[0])) {
            filteredArgs = Arrays.copyOfRange(args, 1, args.length);
        }
        ParameterTool parameters = ParameterTool.fromArgs(filteredArgs);
        env.getConfig().setGlobalJobParameters(parameters);

        KafkaSource<Stock> source = buildKafkaSource(parameters);

        DataStream<Stock> stocks = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Stocks Source")
                .name("Stocks Source");

        stocks.addSink(new MySqlStockSink())
                .name("stock mysql sink");

        DataStream<StockSummaryStatistics> stockStatistics = readStockStatistics(stocks);

        DataStream<StockSummaryStatistics> stockSummaryStatisticsRolling1Week = stockSummaryStatisticsRolling1Week(stockStatistics);
        DataStream<StockSummaryStatistics> stockSummaryStatisticsRolling2Weeks = stockSummaryStatisticsRolling2Weeks(stockStatistics);
        DataStream<StockSummaryStatistics> stockSummaryStatisticsRolling1Month = stockSummaryStatisticsRolling1Month(stockStatistics);

        DataStream<StockStatistics> stockStatistics1Week = stockFinalStatistics(stockSummaryStatisticsRolling1Week, "1Week");
        DataStream<StockStatistics> stockStatistics2Week = stockFinalStatistics(stockSummaryStatisticsRolling2Weeks, "2Weeks");
        DataStream<StockStatistics> stockStatistics1Month = stockFinalStatistics(stockSummaryStatisticsRolling1Month, "1Month");

        InfluxDBSink4StockStatistics influxDbSink4StockStatistics = new InfluxDBSink4StockStatistics();

        stockStatistics1Week.addSink(influxDbSink4StockStatistics).name("stockStatistics1Week");
        stockStatistics2Week.addSink(influxDbSink4StockStatistics).name("stockStatistics2Week");
        stockStatistics1Month.addSink(influxDbSink4StockStatistics).name("stockStatistics1Month");

        // Machine learning stuff
        DataStream<Prediction> exponentialSmoothingPredictions = createOnlineExponentialSmootherRegressorPipeline(stocks);
        DataStream<Prediction> oneDimensionRegressorPredictions = createOnlineOneDimensionRegressorPipeline(stocks);
        DataStream<Prediction> sgdRegressorPredictions = createOnlineSgdRegressorPipeline(stocks);

        InfluxDBSink4Prediction influxDBSink4Prediction = new InfluxDBSink4Prediction();

        exponentialSmoothingPredictions.addSink(influxDBSink4Prediction).name("exponentialSmoothingPredictions");
        oneDimensionRegressorPredictions.addSink(influxDBSink4Prediction).name("oneDimensionRegressorPredictions");
        sgdRegressorPredictions.addSink(influxDBSink4Prediction).name("sgdRegressorPredictions");

        env.execute("Stock Analysis Tool");
    }

    private static DataStream<StockSummaryStatistics> stockStatisticsRolling(DataStream<StockSummaryStatistics> stockStatistics, int numberOfDays, String name) {
        return stockStatistics
                .keyBy(StockSummaryStatistics::getKey)
                .window(SlidingEventTimeWindows.of(Time.seconds(numberOfDays), Time.seconds(1)))
                .allowedLateness(Time.seconds(1))
                .reduce(new StocksReducer(numberOfDays), new EmitOnWindowClose(numberOfDays))
                .name(name);
    }

}
