package com.diptam.search;

import com.diptam.kafka.Consumer;
import com.diptam.model.Tweet;
import com.diptam.util.ApplicationProperties;
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

        try {
            //Kafka Consumer
            while (true){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
                records.forEach(record -> {
                    log.info("Record found at consumer : "+record.value()+" with offset"+record.offset());
                    try {
                        //Elastic Search Request
                        IndexRequest request = new IndexRequest(ELASTIC_INDEX).source(record.value().toString(), XContentType.JSON);
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
        Gson gson = new GsonBuilder().create();
        String json = "{\n" +
                "\t\"created_at\": \"Tue May 12 16:46:27 +0000 2020\",\n" +
                "\t\"id\": 1260249995745259500,\n" +
                "\t\"id_str\": \"1260249995745259520\",\n" +
                "\t\"text\": \"@Frostitute4ever @Marvel @StateFarm Self made royalty, Jean Grey is a Goddess chosen by the Universe\",\n" +
                "\t\"display_text_range\": [\n" +
                "\t\t36,\n" +
                "\t\t100\n" +
                "\t],\n" +
                "\t\"source\": \"<a href=\\\"https://mobile.twitter.com\\\" rel=\\\"nofollow\\\">Twitter Web App</a>\",\n" +
                "\t\"truncated\": false,\n" +
                "\t\"in_reply_to_status_id\": 1260232883287064600,\n" +
                "\t\"in_reply_to_status_id_str\": \"1260232883287064577\",\n" +
                "\t\"in_reply_to_user_id\": 722833943158894600,\n" +
                "\t\"in_reply_to_user_id_str\": \"722833943158894593\",\n" +
                "\t\"in_reply_to_screen_name\": \"Frostitute4ever\",\n" +
                "\t\"user\": {\n" +
                "\t\t\"id\": 1258217943709540400,\n" +
                "\t\t\"id_str\": \"1258217943709540352\",\n" +
                "\t\t\"name\": \"for all humanity\",\n" +
                "\t\t\"screen_name\": \"forallhumanity1\",\n" +
                "\t\t\"location\": null,\n" +
                "\t\t\"url\": null,\n" +
                "\t\t\"description\": \"Original X-Men fan\",\n" +
                "\t\t\"translator_type\": \"none\",\n" +
                "\t\t\"protected\": false,\n" +
                "\t\t\"verified\": false,\n" +
                "\t\t\"followers_count\": 1,\n" +
                "\t\t\"friends_count\": 1,\n" +
                "\t\t\"listed_count\": 0,\n" +
                "\t\t\"favourites_count\": 32,\n" +
                "\t\t\"statuses_count\": 10,\n" +
                "\t\t\"created_at\": \"Thu May 07 02:12:06 +0000 2020\",\n" +
                "\t\t\"utc_offset\": null,\n" +
                "\t\t\"time_zone\": null,\n" +
                "\t\t\"geo_enabled\": false,\n" +
                "\t\t\"lang\": null,\n" +
                "\t\t\"contributors_enabled\": false,\n" +
                "\t\t\"is_translator\": false,\n" +
                "\t\t\"profile_background_color\": \"F5F8FA\",\n" +
                "\t\t\"profile_background_image_url\": \"\",\n" +
                "\t\t\"profile_background_image_url_https\": \"\",\n" +
                "\t\t\"profile_background_tile\": false,\n" +
                "\t\t\"profile_link_color\": \"1DA1F2\",\n" +
                "\t\t\"profile_sidebar_border_color\": \"C0DEED\",\n" +
                "\t\t\"profile_sidebar_fill_color\": \"DDEEF6\",\n" +
                "\t\t\"profile_text_color\": \"333333\",\n" +
                "\t\t\"profile_use_background_image\": true,\n" +
                "\t\t\"profile_image_url\": \"http://pbs.twimg.com/profile_images/1258465041440858112/-OyyMjMy_normal.jpg\",\n" +
                "\t\t\"profile_image_url_https\": \"https://pbs.twimg.com/profile_images/1258465041440858112/-OyyMjMy_normal.jpg\",\n" +
                "\t\t\"default_profile\": true,\n" +
                "\t\t\"default_profile_image\": false,\n" +
                "\t\t\"following\": null,\n" +
                "\t\t\"follow_request_sent\": null,\n" +
                "\t\t\"notifications\": null\n" +
                "\t},\n" +
                "\t\"geo\": null,\n" +
                "\t\"coordinates\": null,\n" +
                "\t\"place\": null,\n" +
                "\t\"contributors\": null,\n" +
                "\t\"is_quote_status\": false,\n" +
                "\t\"quote_count\": 0,\n" +
                "\t\"reply_count\": 0,\n" +
                "\t\"retweet_count\": 0,\n" +
                "\t\"favorite_count\": 0,\n" +
                "\t\"entities\": {\n" +
                "\t\t\"hashtags\": [],\n" +
                "\t\t\"urls\": [],\n" +
                "\t\t\"user_mentions\": [\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"screen_name\": \"Frostitute4ever\",\n" +
                "\t\t\t\t\"name\": \"Isah Alonso\uD83D\uDC8E\",\n" +
                "\t\t\t\t\"id\": 722833943158894600,\n" +
                "\t\t\t\t\"id_str\": \"722833943158894593\",\n" +
                "\t\t\t\t\"indices\": [\n" +
                "\t\t\t\t\t0,\n" +
                "\t\t\t\t\t16\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"screen_name\": \"Marvel\",\n" +
                "\t\t\t\t\"name\": \"Marvel Entertainment\",\n" +
                "\t\t\t\t\"id\": 15687962,\n" +
                "\t\t\t\t\"id_str\": \"15687962\",\n" +
                "\t\t\t\t\"indices\": [\n" +
                "\t\t\t\t\t17,\n" +
                "\t\t\t\t\t24\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"screen_name\": \"StateFarm\",\n" +
                "\t\t\t\t\"name\": \"State Farm\",\n" +
                "\t\t\t\t\"id\": 15091022,\n" +
                "\t\t\t\t\"id_str\": \"15091022\",\n" +
                "\t\t\t\t\"indices\": [\n" +
                "\t\t\t\t\t25,\n" +
                "\t\t\t\t\t35\n" +
                "\t\t\t\t]\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\t\"symbols\": []\n" +
                "\t},\n" +
                "\t\"favorited\": false,\n" +
                "\t\"retweeted\": false,\n" +
                "\t\"filter_level\": \"low\",\n" +
                "\t\"lang\": \"en\",\n" +
                "\t\"timestamp_ms\": \"1589301987955\"\n" +
                "}";
        Tweet tweet = gson.fromJson(json, Tweet.class);
        String out = gson.toJson(tweet.getUser());
        log.info(out);

        ElasticSearchConsumer elasticSearchConsumer = new ElasticSearchConsumer();
        final RestHighLevelClient elasticClient = elasticSearchConsumer.createElasticClient();
        IndexRequest request = new IndexRequest(ELASTIC_INDEX).source(out, XContentType.JSON);
        try {
            IndexResponse response = elasticClient.index(request, RequestOptions.DEFAULT);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
