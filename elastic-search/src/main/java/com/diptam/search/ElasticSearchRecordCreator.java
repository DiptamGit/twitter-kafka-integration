package com.diptam.search;

import com.diptam.kafka.Consumer;
import com.diptam.util.SearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.time.Duration;

@Slf4j
public class ElasticSearchRecordCreator {

    private final static String ELASTIC_INDEX = "tweets";

    private RestHighLevelClient client;
    private KafkaConsumer<String, String> consumer;

    public ElasticSearchRecordCreator() {
        this.client = new ElasticSearchClientBuilder().getClient();
        this.consumer = new Consumer().getConsumer();
    }

    public void insert() throws IOException {

        try {
            //Kafka Consumer
            while (true){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
                records.forEach(record -> {
                    log.info("Record found at consumer : "+record.value()+" with offset"+record.offset());
                    try {
                        //Elastic Search Request
                        IndexRequest request = new IndexRequest(ELASTIC_INDEX).source(SearchUtil.formatTweetAsJson(record.value()), XContentType.JSON);
                        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
                        log.info("Response from ElasticSearch : "+response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            client.close();
            consumer.close();
        }
    }

}
