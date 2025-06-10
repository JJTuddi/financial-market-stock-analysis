package tuddi.stock.processor.stock.prediction;

public class Prediction {

    public String stockName;
    public String algorithm;
    public long timestamp;

    public double close;
    public double predicted;

    public double error;
    public double percentageError;

    public Prediction(
            String stockName,
            String algorithm,
            long timestamp,
            double close,
            double predicted,
            double error,
            double percentageError
    ) {
        this.stockName = stockName;
        this.algorithm = algorithm;
        this.timestamp = timestamp;

        this.close = close;
        this.predicted = predicted;

        this.error = error;
        this.percentageError = percentageError;
    }

    public Prediction() {

    }

    @Override
    public String toString() {
        return new StringBuilder("Prediction{")
                .append("stockName=").append('\'').append(stockName).append('\'').append(',')
                .append(",algorithm=").append('\'').append(algorithm).append('\'')
                .append(",timestamp=").append(timestamp)
                .append(",close=").append(close)
                .append(",predicted=").append(predicted)
                .append(",error=").append(error)
                .append(",percentageError=").append(percentageError)
                .append('}')
                .toString();
    }

}
