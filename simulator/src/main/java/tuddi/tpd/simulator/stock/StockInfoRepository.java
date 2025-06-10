package tuddi.tpd.simulator.stock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;
import tuddi.tpd.simulator.stock.data.StockInfo;
import tuddi.tpd.simulator.stock.data.StockValue;
import tuddi.tpd.simulator.util.DateTimeUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Repository
class StockInfoRepository {

    private final Logger logger = LoggerFactory.getLogger(StockInfoRepository.class);

    private final List<StockInfo> stock;

    StockInfoRepository(
            @Value("${stocksInfoPath:stocks.json}") String stocksInfoClassPath,
            ObjectMapper objectMapper
    ) {
        stock = readStockInfo(stocksInfoClassPath, objectMapper);
    }

    public List<StockInfo> getStockInfo() {
        return stock;
    }

    public List<StockValue> readStock(String stockName, @NotNull LocalDate startDate) {
        String resourcePath = "nasdaq/" + stockName + ".csv";
        ClassPathResource resource = new ClassPathResource(resourcePath);

        if (!resource.exists()) {
            throw new RuntimeException("Stock CSV not found on classpath: " + resourcePath);
        }

        try (
                InputStream is = resource.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                CSVReader reader = new CSVReader(isr)
        ) {
            return reader.readAll().stream()
                    .skip(1)  // skip header
                    .dropWhile(line -> startDate.isAfter(DateTimeUtil.fromString(line[0])))
                    .map(this::mapCsvRow)
                    .toList();
        }
        catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + resourcePath, e);
        } catch (CsvException csvException) {
            throw new RuntimeException("Couldn't read the csv do to: " + csvException.getMessage(), csvException);
        }
    }


    private StockValue mapCsvRow(String[] row) {
//        Date,Low,Open,Volume,High,Close,Adjusted Close
        return new StockValue(
                DateTimeUtil.fromString(row[0]),
                Double.parseDouble(row[1]),
                Double.parseDouble(row[2]),
                Double.parseDouble(row[3]),
                Double.parseDouble(row[4]),
                Double.parseDouble(row[5])
        );
    }

    private List<StockInfo> readStockInfo(String classpathLocation, ObjectMapper mapper) {
        logger.info("Loading stock info from classpath: {}", classpathLocation);
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        if (!resource.exists()) {
            throw new IllegalStateException("Missing resource: " + classpathLocation);
        }

        try (InputStream in = resource.getInputStream()) {
            List<StockInfo> list = mapper.readValue(
                    in,
                    new TypeReference<List<StockInfo>>() {}
            );
            return Collections.unmodifiableList(list);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + classpathLocation, e);
        }
    }

}
