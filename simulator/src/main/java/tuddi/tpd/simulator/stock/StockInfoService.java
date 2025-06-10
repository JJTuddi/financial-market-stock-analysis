package tuddi.tpd.simulator.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tuddi.tpd.simulator.stock.data.StockInfo;
import tuddi.tpd.simulator.stock.data.StockValue;
import tuddi.tpd.simulator.stock.request.CreateStockSimulationRequest;
import tuddi.tpd.simulator.util.DateTimeUtil;

import java.time.LocalDate;
import java.util.List;

@Service
class StockInfoService {

    private static final Logger logger = LoggerFactory.getLogger(StockInfoService.class);

    private final StockInfoRepository stockInfoRepository;
    private final SimulationRepository simulationRepository;


    StockInfoService(
            StockInfoRepository stockInfoRepository,
            SimulationRepository simulationRepository
    ) {
        this.stockInfoRepository = stockInfoRepository;
        this.simulationRepository = simulationRepository;
    }

    List<StockInfo> getStockInfo() {
        return stockInfoRepository.getStockInfo();
    }

    List<Simulation> getSimulations() {
        return simulationRepository.getSimulations();
    }

    void createSimulation(CreateStockSimulationRequest request) {
        LocalDate startDate = DateTimeUtil.fromString(request.startDate());
        List<StockValue> stockValues = stockInfoRepository.readStock(request.stockName(), startDate);

        simulationRepository.createSimulation(request.stockName(), request.delayMillis(), stockValues);

        logger.trace("Created new simulation of stock: {}",  request.stockName());
    }

    void stopSimulation(String simulationId) {
        simulationRepository.stopSimulation(simulationId);
        logger.info("The simulation with id={} was stopped", simulationId);
    }

}
