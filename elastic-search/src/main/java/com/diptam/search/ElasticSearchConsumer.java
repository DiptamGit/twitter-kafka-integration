package com.diptam.search;

import com.diptam.kafka.Consumer;
import com.diptam.model.Tweet;
import com.diptam.util.ApplicationProperties;
import com.diptam.util.SearchUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;

@Slf4j
public class ElasticSearchConsumer {

    private final HashMap<String, String> properties = ApplicationProperties.getProperties();
    private final static String ELASTIC_INDEX = "tweets";

    public RestHighLevelClient createElasticClient(){

        RestClientBuilder builder = RestClient.builder(new HttpHost(properties.get("elasticHost"),443,"https"))
                                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                                        @Override
                                        public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                                            return httpAsyncClientBuilder.setDefaultCredentialsProvider(getCredentialProvider());
                                        }
                                    });
        RestHighLevelClient client = new RestHighLevelClient(builder);
       return client;
    }

    private CredentialsProvider getCredentialProvider(){
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(properties.get("elasticUsername"), properties.get("elasticPassword")));
        return credentialsProvider;
    }

    private void insertRecordsToElasticSearch() throws IOException {
        ElasticSearchConsumer elasticSearchConsumer = new ElasticSearchConsumer();
        final RestHighLevelClient elasticClient = elasticSearchConsumer.createElasticClient();
        KafkaConsumer<String, String> consumer = Consumer.getConsumer();
        Gson gson = new GsonBuilder().create();

        try {
            //Kafka Consumer
            while (true){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
                records.forEach(record -> {
                    log.info("Record found at consumer : "+record.value()+" with offset"+record.offset());
                    try {
                        //Elastic Search Request
                        IndexRequest request = new IndexRequest(ELASTIC_INDEX).source(SearchUtil.formatTweetAsJson(record.value()), XContentType.JSON);
                        IndexResponse response = elasticClient.index(request, RequestOptions.DEFAULT);
                        log.info("Response from ElasticSearch : "+response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            elasticClient.close();
            consumer.close();
        }
    }

    public static void main(String[] args) {
        ElasticSearchConsumer elasticSearchConsumer = new ElasticSearchConsumer();
        try {
            elasticSearchConsumer.insertRecordsToElasticSearch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
