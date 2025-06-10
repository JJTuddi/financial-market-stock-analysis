package tuddi.tpd.simulator.stock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tuddi.tpd.simulator.stock.data.StockInfo;
import tuddi.tpd.simulator.stock.data.StockValue;
import tuddi.tpd.simulator.stock.request.CreateStockSimulationRequest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StockInfoServiceTest {

    @Mock
    private StockInfoRepository stockInfoRepository;
    @Mock
    private SimulationRepository simulationRepository;
    @InjectMocks
    private StockInfoService stockInfoService;

    @Test
    void gettingTheStockInfo_shouldReturnThemFromStockInfoRepository() {
        List<StockInfo> stockInfos = List.of(
                new StockInfo("Apple Inc.", "AAPL", "apple"),
                new StockInfo("Microsoft", "MSFT", "4 squares")
        );
        Mockito.when(stockInfoRepository.getStockInfo()).thenReturn(stockInfos);

        List<StockInfo> actual = stockInfoService.getStockInfo();

        assertSame(stockInfos, actual);
        assertEquals(stockInfos, actual);
    }

    @Test
    void createSimulation() {
        CreateStockSimulationRequest request = new CreateStockSimulationRequest(100, "AAPL", "01-01-2009");
        List<StockValue> stockValues = Collections.singletonList(new StockValue(LocalDate.of(2009, 1, 2), 100, 103, 30, 115, 110));
        Mockito.when(stockInfoRepository.readStock("AAPL", LocalDate.of(2009, 1, 1)))
                .thenReturn(stockValues);

        stockInfoService.createSimulation(request);

        Mockito.verify(simulationRepository).createSimulation("AAPL", 100, stockValues);
    }

}