package tuddi.tpd.simulator.stock;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;
import tuddi.tpd.simulator.helper.IdHelper;
import tuddi.tpd.simulator.stock.data.StockValue;

import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Repository
class SimulationRepository {

    private final Clock clock;
    private final IdHelper idHelper;
    private final ExecutorService simulationExecutorService;
    private final KafkaTemplate<String, StockValue> stockValueKafkaTemplate;

    SimulationRepository(
            Clock clock,
            IdHelper idHelper,
            ExecutorService simulationExecutorService,
            KafkaTemplate<String, StockValue> stockValueKafkaTemplate
    ) {
        this.clock = clock;
        this.idHelper = idHelper;
        this.simulationExecutorService = simulationExecutorService;
        this.stockValueKafkaTemplate = stockValueKafkaTemplate;
    }

    private Map<String, Simulation> simulations = new LinkedHashMap<>();

    List<Simulation> getSimulations() {
        return simulations.values().stream().toList();
    }

    void createSimulation(String stockName, int delayMillis, List<StockValue> stockValues) {
        String id = idHelper.getUuid();
        Simulation simulation = new Simulation(id, stockName, delayMillis, stockValues, stockValueKafkaTemplate, clock);

        simulationExecutorService.submit(simulation);

        simulations.put(id, simulation);
    }

    void stopSimulation(String simulationId) {
        Simulation toRemove = simulations.remove(simulationId);
        if (toRemove != null) toRemove.stop();
    }

    void setSimulationsMap(Map<String, Simulation> simulations) {
        this.simulations = simulations;
    }

}
