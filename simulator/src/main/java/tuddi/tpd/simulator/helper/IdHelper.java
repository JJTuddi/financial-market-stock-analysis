package tuddi.tpd.simulator.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class IdHelper {

    private final Random random;

    IdHelper(@Value("${idHelper.random.seed:42}") int randomSeed) {
        random = new Random(randomSeed);
    }

    public int getNextInt() {
        return random.nextInt();
    }

    public int getNextInt(int bound) {
        return random.nextInt(bound);
    }

    public int getNextInt(int origin, int bound) {
        return random.nextInt(origin, bound);
    }

    public String getUuid() {
        return UUID.randomUUID().toString();
    }



}
