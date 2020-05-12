package com.diptam.search;

import com.diptam.util.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
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

    public static void main(String[] args) throws IOException {
        ElasticSearchConsumer elasticSearchConsumer = new ElasticSearchConsumer();
        final RestHighLevelClient elasticClient = elasticSearchConsumer.createElasticClient();
        String jsonString = "{" +
                "\"user\":\"Diptam\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        try {
            IndexRequest request = new IndexRequest(ELASTIC_INDEX).source(jsonString, XContentType.JSON);
            IndexResponse response = elasticClient.index(request, RequestOptions.DEFAULT);
            log.info(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            elasticClient.close();
        }
    }
}
