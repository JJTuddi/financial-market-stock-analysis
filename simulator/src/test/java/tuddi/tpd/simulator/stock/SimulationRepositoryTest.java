package tuddi.tpd.simulator.stock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import tuddi.tpd.simulator.helper.IdHelper;
import tuddi.tpd.simulator.stock.data.StockValue;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static tuddi.tpd.simulator.util.ClockUtil.FIXED_CLOCK;

@ExtendWith(MockitoExtension.class)
class SimulationRepositoryTest {

    @Mock
    private IdHelper idHelper;
    @Mock
    private ExecutorService executorService;
    @Mock
    private KafkaTemplate<String, StockValue> kafkaTemplate;
    @InjectMocks
    private SimulationRepository simulationRepository;

    @Test
    void given2Simulations_itShouldCreateThem_submitToExecutorService_andPlaceInTheMap() {
        assertThat(simulationRepository.getSimulations()).isEmpty();
        Mockito.when(idHelper.getUuid())
                .thenReturn("testUuid1")
                .thenReturn("testUuid2");

        simulationRepository.createSimulation("AAPL", 100, Collections.emptyList());
        simulationRepository.createSimulation("MSFT", 100, Collections.emptyList());

        Simulation simulation1 = new Simulation("testUuid1", "AAPL", 100, Collections.emptyList(), null, FIXED_CLOCK);
        Simulation simulation2 = new Simulation("testUuid2", "MSFT", 100, Collections.emptyList(), null, FIXED_CLOCK);
        assertThat(simulationRepository.getSimulations()).isNotEmpty()
                .hasSize(2)
                .containsExactly(simulation1, simulation2);
        ArgumentCaptor<Simulation> esArgumentCaptor = ArgumentCaptor.forClass(Simulation.class);
        Mockito.verify(executorService, times(2)).submit(esArgumentCaptor.capture());
        assertThat(esArgumentCaptor.getAllValues()).isNotEmpty()
                .hasSize(2)
                .containsExactly(simulation1, simulation2);
    }

    @Test
    void stoppingSimulation_shouldRemoveFromMapAndStopIt() {
        Map<String, Simulation> map = Mockito.mock(Map.class);
        Simulation simulation = Mockito.mock(Simulation.class);
        Mockito.when(map.remove("test-id")).thenReturn(simulation);
        simulationRepository.setSimulationsMap(map);

        simulationRepository.stopSimulation("test-id");

        Mockito.verify(simulation).stop();
    }

}