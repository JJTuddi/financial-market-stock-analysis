package tuddi.tpd.simulator.stock.request;

public record CreateStockSimulationRequest(
        int delayMillis,
        String stockName,
        String startDate
) {
}
