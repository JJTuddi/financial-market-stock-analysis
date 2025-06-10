package tuddi.tpd.simulator.stock.data;

import java.time.LocalDate;

public record StockValue(LocalDate date, double low, double open, double volume, double high, double close, long timestamp) {

    public StockValue(LocalDate date, double low, double open, double volume, double high, double close) {
        this(date, low, open, volume, high, close, -1);
    }

    public StockValue withTimestamp(long timestamp) {
        return new StockValue(date, low, open, volume, high, close, timestamp);
    }

}
