package tuddi.tpd.simulator.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import tuddi.tpd.simulator.stock.data.StockValue;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class Simulation implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Simulation.class);

    private final String id;
    private final String stockName;
    private final int delayMillis;
    private final List<StockValue> stocks;
    private final KafkaTemplate<String, StockValue> kafkaTemplate;
    private int progres;
    private State state;
    private Clock clock;

    private final AtomicBoolean run = new AtomicBoolean(true);

    Simulation(
            String id,
            String stockName,
            int delayMillis,
            List<StockValue> stocks,
            KafkaTemplate<String, StockValue> kafkaTemplate,
            Clock clock
    ) {
        this.id = id;
        this.stockName = stockName;
        this.delayMillis = delayMillis;
        this.stocks = stocks;
        this.kafkaTemplate = kafkaTemplate;
        state = State.CREATED;
        this.clock = clock;
    }

    @Override
    public void run() {
        try {
            state = State.RUNNING;
            for (StockValue stockValue : stocks) {
                if (!run.get()) {
                    state = State.STOPPED;
                    logger.debug("The simulation with id={} was stopped!", id);
                    return;
                }
                if (!safeSleep()) {
                    state = State.INTERRUPTED;
                    logger.debug("The simulation with id={} was interrupted!", id);
                    return;
                }

                kafkaTemplate.sendDefault(stockName, stockValue.withTimestamp(clock.millis()));
                progres++;
            }
            state = State.DONE;
        } catch (Throwable throwable) {
            state = State.FAILED;
        }
    }

    public void stop() {
        run.set(false);
    }

    public String getId() {
        return id;
    }

    public int getProgress() {
        return progres;
    }

    public int getTotal() {
        return stocks.size();
    }

    public String getState() {
        return state.toString();
    }

    public int getDelayMillis() {
        return delayMillis;
    }

    private boolean safeSleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(delayMillis);
            return true;
        } catch (InterruptedException interruptedException) {
            return false;
        }
    }

    private enum State {
        CREATED, RUNNING, INTERRUPTED, DONE, STOPPED, FAILED
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Simulation other)) {
            return false;
        }
        return Objects.equals(id, other.id) && Objects.equals(stockName, other.stockName) &&
               delayMillis == other.delayMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stockName, delayMillis);
    }

}
