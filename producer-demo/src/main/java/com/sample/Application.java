package com.sample;

import com.sample.schema.demo.Employee;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;

import java.util.Properties;
import java.util.stream.IntStream;

public class Application {

    private final static String TOPIC = "new-employees";

    private static Producer<Long, Employee> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "AvroProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());

        // Configure the KafkaAvroSerializer.
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());

        // Schema Registry location.
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        return new KafkaProducer<>(props);
    }

    public static void main(String args[]) {
        Producer<Long, Employee> producer = createProducer();

        Employee bob = Employee.newBuilder()
                .setAge(35)
                .setFirstName("Bob")
                .setLastName("Jones")
                .setPhoneNumber("30115551234")
                .build();

        IntStream.range(1, 100).forEach(index -> {
            producer.send(new ProducerRecord<>(TOPIC, 1L * index, bob));

        });

        producer.flush();
        producer.close();
    }
}
