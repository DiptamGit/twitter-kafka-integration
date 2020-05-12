package com.diptam.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@Slf4j
public class Producer {

    final static String BOOTSTRAP_SERVER = "localhost:9092";
    final static String TOPIC = "tweets";

    public static KafkaProducer<String, String> createKafkaProducer() {
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(createKafkaProducerProperties());
        return producer;
    }

    public static ProducerRecord createProducerRecord(String message, int key) {
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "key-"+key,message);
        return record;
    }

    public static ProducerRecord createProducerRecord(String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, message);
        return record;
    }

    public static void sendMessage(KafkaProducer<String, String> producer, final ProducerRecord record) {
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if(e != null){
                    log.error("Last Offset record : "+recordMetadata.offset(), e);
                }else{
                    log.info("**Record Meta Data**"+"\n"
                            +"Topic : "+recordMetadata.topic()+"\n"
                            +"Partition : "+recordMetadata.partition()+"\n"
                            +"Offset : "+recordMetadata.offset()+"\n"
                            +"Timestamp : "+recordMetadata.timestamp());
                }
            }
        });
    }

    private static Properties createKafkaProducerProperties() {
        //https://kafka.apache.org/documentation/#producerconfigs
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, Boolean.TRUE.toString());

        return properties;
    }
}
