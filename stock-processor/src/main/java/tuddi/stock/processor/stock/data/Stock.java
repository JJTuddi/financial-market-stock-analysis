package tuddi.stock.processor.stock.data;

public class Stock {

    public String stockName;
    public double low;
    public double open;
    public double volume;
    public double high;
    public double close;

    public long timestamp;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Stock{");
        sb.append("stockName='").append(stockName).append('\'');
        sb.append(", low=").append(low);
        sb.append(", open=").append(open);
        sb.append(", volume=").append(volume);
        sb.append(", high=").append(high);
        sb.append(", close=").append(close);
        sb.append(", timestamp=").append(timestamp);
        sb.append('}');
        return sb.toString();
    }
}
