package tuddi.tpd.simulator.stock;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tuddi.tpd.simulator.stock.request.CreateStockSimulationRequest;

@Controller
@RequestMapping("/")
public class StockController {

    private final StockInfoService stockInfoService;

    StockController(StockInfoService stockInfoService) {
        this.stockInfoService = stockInfoService;
    }

    @GetMapping
    public String getStockView(Model model) {
        model.addAttribute("stockInfo", stockInfoService.getStockInfo());
        model.addAttribute("simulations", stockInfoService.getSimulations());
        return "simulator";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String createSimulation(@ModelAttribute("simulationForm")CreateStockSimulationRequest simulationForm) {
        stockInfoService.createSimulation(simulationForm);
        return "redirect:/";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String stopSimulationAndDelete(@PathVariable("id") String simulationId) {
        stockInfoService.stopSimulation(simulationId);
        return "redirect:/";
    }

}
