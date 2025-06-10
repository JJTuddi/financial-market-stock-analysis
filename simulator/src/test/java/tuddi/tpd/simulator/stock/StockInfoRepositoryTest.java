package tuddi.tpd.simulator.stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tuddi.tpd.simulator.stock.data.StockInfo;
import tuddi.tpd.simulator.stock.data.StockValue;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StockInfoRepositoryTest {

    @Test
    void shouldReadStocks_butTheFileDoesNotExist_shouldThrowRuntimeException() {
        ObjectMapper objectMapper = new ObjectMapper();

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> new StockInfoRepository("notExisting", objectMapper));
        assertThat(thrown).hasMessage("The file notExisting doesn't exist!");
    }

    @Test
    void shouldReadStocks_theFileExists_shouldReadTheResults() {
        ObjectMapper objectMapper = new ObjectMapper();
        StockInfoRepository repository = new StockInfoRepository("stocks.json", objectMapper);

        List<StockInfo> stockInfo = repository.getStockInfo();

        List<StockInfo> expected = List.of(
                new StockInfo("Apple Inc.", "AAPL", "https://1000logos.net/wp-content/uploads/2017/02/Apple-Logosu.png"),
                new StockInfo("Analog Devices, Inc.", "ADI", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/86/Analog_Devices_Logo.svg/2560px-Analog_Devices_Logo.svg.png"),
                new StockInfo("Advanced Micro Devices, Inc.", "AMD", "https://1000logos.net/wp-content/uploads/2020/05/AMD-Logo.png"),
                new StockInfo("Amazon.com, Inc.", "AMZN", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Amazon_logo.svg/2560px-Amazon_logo.svg.png"),
                new StockInfo("ASML Holding N.V.", "ASML", "https://upload.wikimedia.org/wikipedia/en/thumb/6/6c/ASML_Holding_N.V._logo.svg/1280px-ASML_Holding_N.V._logo.svg.png"),
                new StockInfo("Cisco Systems, Inc.", "CSCO", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Cisco_logo_blue_2016.svg/640px-Cisco_logo_blue_2016.svg.png"),
                new StockInfo("Microsoft Corporation", "MSFT", "https://logos-world.net/wp-content/uploads/2020/09/Microsoft-Logo.png"),
                new StockInfo("Netflix, Inc.", "NFLX", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Netflix_2015_logo.svg/1280px-Netflix_2015_logo.svg.png"),
                new StockInfo("NVIDIA Corporation", "NVDA", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/NVIDIA_logo.svg/1024px-NVIDIA_logo.svg.png"),
                new StockInfo("Qualcomm Incorporated", "QCOM", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Qualcomm-Logo.svg/1200px-Qualcomm-Logo.svg.png"),
                new StockInfo("Starbucks Corporation", "SBUX", "https://pngimg.com/d/starbucks_PNG7.png"),
                new StockInfo("Texas Instruments Incorporated", "TXN", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/ba/TexasInstruments-Logo.svg/1200px-TexasInstruments-Logo.svg.png")
        );
        Assertions.assertEquals(expected, stockInfo);
    }

    @Test
    void requestingTheStockInfoOfApple_shouldReadTheCsvFileAndParseIt() {
        ObjectMapper objectMapper = new ObjectMapper();
        StockInfoRepository repository = new StockInfoRepository("stocks.json", objectMapper);
        List<StockValue> expected = List.of(
                new StockValue(LocalDate.of(2022, 12, 7), 140.0, 142.19000244140625, 69721100, 143.3699951171875, 140.94000244140625),
                new StockValue(LocalDate.of(2022, 12, 8), 141.10000610351562, 142.36000061035156, 62128300, 143.52000427246094, 142.64999389648438),
                new StockValue(LocalDate.of(2022, 12, 9), 140.89999389648438, 142.33999633789062, 76069500, 145.57000732421875, 142.16000366210938),
                new StockValue(LocalDate.of(2022, 12, 12), 141.07000732421875, 142.6999969482422, 21904917, 143.0, 142.32000732421875)
        );

        List<StockValue> actual = repository.readStock("AAPL", LocalDate.of(2022, 12, 7));

        assertThat(actual).hasSameSizeAs(expected).isEqualTo(expected);
    }

}