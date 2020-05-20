package com.diptam.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

@Slf4j
public class Consumer {

    private KafkaConsumer<String, String> consumer;

    final static String BOOTSTRAP_SERVER = "localhost:9092";
    final static String TOPIC = "tweets";
    final static String GROUP_ID = "kafka-consumer-tweets-elasticsearch";
    final static String OFFSET_TYPE = "earliest"; //latest - earliest - none

    public Consumer() {
        this.consumer = createConsumer();
    }

    private static KafkaConsumer<String, String> createConsumer() {

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(createKafkaConsumerProperties());
        consumer.subscribe(Collections.singletonList(TOPIC));

        return consumer;
    }

    private static Properties createKafkaConsumerProperties() {
        //https://kafka.apache.org/documentation/#producerconfigs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFSET_TYPE);

        return properties;
    }

    public KafkaConsumer<String, String> getConsumer() {
        return consumer;
    }
}
