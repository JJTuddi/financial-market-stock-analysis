package tuddi.tpd.simulator.stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tuddi.tpd.simulator.helper.IdHelper;
import tuddi.tpd.simulator.stock.data.StockInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static tuddi.tpd.simulator.util.ClockUtil.FIXED_CLOCK;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class StockSimulationIntegrationTest {

    private static final List<StockInfo> STOCK_INFOS = List.of(
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
    @MockitoBean
    private IdHelper idHelper;
    @MockitoBean
    private ExecutorService simulationExecutorService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SimulationRepository simulationRepository;

    @Test
    void creatingASimulation_shouldStartRunItAndAfterQueryingItShouldReturnTheCreatedSimulation() throws Exception {
        Mockito.when(idHelper.getUuid()).thenReturn("testId");
        Simulation expectedSimulation = new Simulation("testId", "AAPL", 1000, null, null, FIXED_CLOCK);
        MockHttpServletRequestBuilder createRequest = post("/simulator")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("delayMillis", "1000")
                .param("stockName", "AAPL")
                .param("startDate", "10-02-2022");

        mockMvc.perform(createRequest)
                .andExpect(status().isCreated())
                .andExpect(view().name("redirect:/simulator"));

        Mockito.verify(simulationExecutorService).submit(expectedSimulation);

        MockHttpServletRequestBuilder getRequest = get("/simulator");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML_VALUE))
                .andExpect(view().name("simulator"))
                .andExpect(model().attribute("stockInfo", STOCK_INFOS))
                .andExpect(model().attribute("simulations", Collections.singletonList(expectedSimulation)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void stoppingTheSimulation_shouldRemoveItFromMapAndCallStopMethodOnIt() throws Exception {
        Map<String, Simulation> map = Mockito.mock(Map.class);
        Simulation simulation = Mockito.mock(Simulation.class);
        Mockito.when(map.remove("test-id")).thenReturn(simulation);
        simulationRepository.setSimulationsMap(map);

        MockHttpServletRequestBuilder request = delete("/simulator/{id}", "test-id");
        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(view().name("redirect:/simulator"));

        Mockito.verify(simulation).stop();
    }

}
