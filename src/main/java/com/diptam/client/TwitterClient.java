package com.diptam.client;

import com.diptam.kafka.Producer;
import com.diptam.util.ApplicationProperties;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/*
* Github : https://github.com/twitter/hbc
* */
@Slf4j
public class TwitterClient {

    public static Client createTwitterClient(BlockingQueue<String> msgQueue){

        Hosts twitterHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint twitterEndpoint = new StatusesFilterEndpoint();
        List<String> terms = Lists.newArrayList("#AssassinsCreedValhalla");
        twitterEndpoint.trackTerms(terms);

        ClientBuilder builder = new ClientBuilder()
                .name("Twitter-Client-01")
                .hosts(twitterHosts)
                .authentication(getAuthentication())
                .endpoint(twitterEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        Client twitterClient = builder.build();

        return twitterClient;
    }

    private static Authentication getAuthentication(){
        ApplicationProperties properties = ApplicationProperties.loadProperties();
        log.info("App Properties : "+properties);
        return new OAuth1(properties.getApiKey(), properties.getApiSecretKey(),properties.getAccessToken(), properties.getAccessTokenSecret());
    }

    public static void main(String[] args) {
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);
        Client twitterClient = createTwitterClient(msgQueue);
        Optional<String> msg = Optional.empty();
        KafkaProducer<String, String> twitterProducer = Producer.createKafkaProducer();
        try {
            twitterClient.connect();

            while (!twitterClient.isDone()) {
                log.info("Polling Message -->");
               msg = Optional.ofNullable(msgQueue.poll(2, TimeUnit.SECONDS));
               if(msg.isPresent()){
                   log.info("Sending Message : "+msg.get());
                   Producer.sendMessage(twitterProducer, Producer.createProducerRecord(msg.get()));
               }
            }
        } catch (InterruptedException e) {
            log.error("Error Occurred : ",e);
            twitterClient.stop();
        } finally {
            twitterProducer.close();
            twitterClient.stop();
        }
    }
}
