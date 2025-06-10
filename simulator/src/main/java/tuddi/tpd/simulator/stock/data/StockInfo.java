package tuddi.tpd.simulator.stock.data;

public record StockInfo(String companyName, String stockName, String image) {
    public String displayName() {
        return "(%s) %s".formatted(stockName, companyName);
    }
}
