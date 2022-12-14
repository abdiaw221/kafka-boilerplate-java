package io.confluent.examples.clients.cloud.order_services.service.producer;

import io.confluent.examples.clients.cloud.model.DataRecord;
import io.confluent.examples.clients.cloud.order_services.base_domain.domain.Order;
import io.confluent.examples.clients.cloud.order_services.base_domain.enums.OrderStatus;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.errors.TopicExistsException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

public class OrderProducerService {

    private static Random RAND = new Random();

    private Producer<String, Object> producer;
    private Properties props;

    private AtomicLong id = new AtomicLong();

    private static final long leftLimit = 1L;

    private static final long rightLimit = 100L;

    public OrderProducerService(final String configFile) throws IOException {
        final Properties properties = loadConfig(configFile);
        this.props = properties;
    }

    public static void initializeTopic(final String topic, final Properties cloudConfig) {
        // Create topic in Confluent Cloud
        final NewTopic newTopic = new NewTopic(topic, Optional.empty(), Optional.empty());
        try (final AdminClient adminClient = AdminClient.create(cloudConfig)) {
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (final InterruptedException | ExecutionException e) {
            // Ignore if TopicExistsException, which may be valid if topic exists
            if (!(e.getCause() instanceof TopicExistsException)) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Properties loadConfig(final String configFile) throws IOException {
        final Properties properties = getFileConfig(configFile);
        // Add additional properties.
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonSerializer");
        return properties;
    }

    static Properties getFileConfig(final String configFile) throws IOException {
        if (!Files.exists(Paths.get(configFile))) {
            throw new IOException(configFile + " not found.");
        }
        final Properties cfg = new Properties();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            cfg.load(inputStream);
        }
        return cfg;
    }

    void produceObject(final String topic, final String key, final Object record) {
        this.producer.send(new ProducerRecord<>(topic, key, record), (m, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n", m.topic(), m.partition(), m.offset());
            }
        });

    }

    public void generateOrder() {
        initializeTopic("orders", this.props);
        this.producer = new KafkaProducer<>(this.props);
        for (int i = 0; i < 10000; i++) {
            int x = RAND.nextInt(5) + 1;
            Order o = new Order();
            o.setId(id.incrementAndGet());
            o.setCustomerId(randomLong() + 1);
            o.setProductId(randomLong() + 1);
            o.setStatus(OrderStatus.NEW);
            o.setPrice(100 * x);
            o.setProductCount(x);
            o.setSource(null);
            produceObject("orders", o.getId().toString(), o);
        }
        this.producer.flush();
        System.out.printf("Message were produced to topic %s%n", "orders");
        this.producer.close();
    }

    public void produceData(final String topic) {
        initializeTopic(topic, this.props);
        // Produce sample data
        this.producer = new KafkaProducer<>(this.props);
        final Long numMessages = 10L;
        for (Long i = 0L; i < numMessages; i++) {
            DataRecord record = new DataRecord(i, i);
            final String key = "alice";
            System.out.printf("Producing record: %s\t%s%n", key, record);
            produceObject(topic, key, record);
        }
        this.producer.flush();
        System.out.printf("Message were produced to topic %s%n", topic);
        this.producer.close();
    }

    static long randomLong() {
        return leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
    }

}
