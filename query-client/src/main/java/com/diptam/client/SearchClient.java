package com.diptam.client;

import com.diptam.model.ElasticSearchResponse;
import com.diptam.search.ElasticSearchClientBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@RestController
@Slf4j
public class SearchClient {

    private static final String INDEX = "tweets";

    @GetMapping(value = "/search/{keyword}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ElasticSearchResponse searchUsingKeyWords(@PathVariable String keyword){

        Optional<SearchResponse> response = Optional.empty();

        RestHighLevelClient client =  new ElasticSearchClientBuilder().getClient();

        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("text", keyword);
        searchSourceBuilder.query(matchQueryBuilder);
        return getElasticSearchResponse(response, client, searchRequest, searchSourceBuilder);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ElasticSearchResponse searchAll(){

        Optional<SearchResponse> response = Optional.empty();

        RestHighLevelClient client =  new ElasticSearchClientBuilder().getClient();

        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        return getElasticSearchResponse(response, client, searchRequest, searchSourceBuilder);
    }

    private ElasticSearchResponse getElasticSearchResponse(Optional<SearchResponse> response, RestHighLevelClient client, SearchRequest searchRequest, SearchSourceBuilder searchSourceBuilder) {
        searchRequest.source(searchSourceBuilder);

        try {
            Instant start = Instant.now();
            response = Optional.ofNullable(client.search(searchRequest, RequestOptions.DEFAULT));
            Instant finish = Instant.now();

            log.info("Time taken : "+ Duration.between(start, finish).toMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new GsonBuilder().create();
        ElasticSearchResponse elasticSearchResponse = gson.fromJson(response.get().toString(), ElasticSearchResponse.class);

        return elasticSearchResponse;
    }
}
