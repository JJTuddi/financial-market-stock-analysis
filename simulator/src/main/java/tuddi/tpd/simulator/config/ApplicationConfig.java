package tuddi.tpd.simulator.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import tuddi.tpd.simulator.stock.data.StockValue;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
class ApplicationConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    ExecutorService simulationExecutorService(
            @Value("${simulation.executorService.numberOfThreads:4}") int numberOfThreads
    ) {
        return Executors.newFixedThreadPool(numberOfThreads);
    }

    @Bean
    ProducerFactory<String, StockValue> stockValueKafkaProducerFactory(
            @Value("${kafka.stock.bootstrapServers:localhost:9092}") String bootstrapServers
    ) {
        Map<String, Object> configProps = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    KafkaTemplate<String, StockValue> stockValueKafkaTemplate(
            @Value("${kafka.stock.defaultTopic:stocks}") String defaultTopic,
            ProducerFactory<String, StockValue> stockValueKafkaProducerFactory
    ) {
        KafkaTemplate<String, StockValue> result = new KafkaTemplate<>(stockValueKafkaProducerFactory);

        result.setDefaultTopic(defaultTopic);

        return result;
    }

}
