package com.diptam.search;

import com.diptam.util.ApplicationProperties;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.HashMap;

public class ElasticSearchClientBuilder {

    private RestHighLevelClient client;

    private final HashMap<String, String> properties = ApplicationProperties.getProperties();
    private final static String ELASTIC_INDEX = "tweets";

    public ElasticSearchClientBuilder() {
        this.client = createElasticClient();
    }

    private RestHighLevelClient createElasticClient(){

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

    public RestHighLevelClient getClient() {
        return client;
    }
}
