package tuddi.stock.processor.util;

import tuddi.stock.processor.stock.data.Stock;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongSupplier;

public final class TestUtil {

    public static class OneSecondApartTimeSupplier implements LongSupplier {

        public static OneSecondApartTimeSupplier getInstance() {
            return new OneSecondApartTimeSupplier();
        }

        private final AtomicInteger atomicInteger = new AtomicInteger(0);

        @Override
        public long getAsLong() {
            return atomicInteger.incrementAndGet() * 1000L;
        }

    }

    public static List<Stock> readStockFromFile(String stockName, int lines, LongSupplier timestampSupplier) {
        try {
            Path path = Path.of("src/test/resources/nasdaq", stockName.toUpperCase() + ".csv");
            return Files.readAllLines(path, StandardCharsets.UTF_8).stream()
                    .skip(1)
                    .limit(lines)
                    .map(TestUtil::stringCsvLineToStock)
                    .peek(stock -> stock.stockName = stockName)
                    .peek(stock -> stock.timestamp = timestampSupplier.getAsLong())
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Stock stringCsvLineToStock(String line) {
        String[] split = line.split(",");
        Stock result = new Stock();

        result.low = Double.parseDouble(split[1]);
        result.open = Double.parseDouble(split[2]);
        result.volume = Double.parseDouble(split[3]);
        result.high = Double.parseDouble(split[4]);
        result.close = Double.parseDouble(split[5]);

        return result;
    }


}
