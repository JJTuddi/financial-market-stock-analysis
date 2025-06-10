package tuddi.stock.processor.stock.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import tuddi.stock.processor.stock.data.RawStock;
import tuddi.stock.processor.stock.data.Stock;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StockDeserializationSchema implements KafkaRecordDeserializationSchema<Stock> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> consumerRecord, Collector<Stock> collector) throws IOException {
        byte[] stockNameByteArr = consumerRecord.key();
        byte[] rawStockByteArr = consumerRecord.value();

        String stockName = new String(stockNameByteArr, StandardCharsets.UTF_8);
        RawStock rawStock = objectMapper.readValue(rawStockByteArr, RawStock.class);

        Stock result = new Stock();

        result.stockName = stockName;
        result.low = rawStock.low;
        result.open = rawStock.open;
        result.volume = rawStock.volume;
        result.high = rawStock.high;
        result.close = rawStock.close;
        result.timestamp = rawStock.timestamp;

        collector.collect(result);
    }

    @Override
    public TypeInformation<Stock> getProducedType() {
        return TypeInformation.of(Stock.class);
    }
}
