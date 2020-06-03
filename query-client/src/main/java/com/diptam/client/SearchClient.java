package com.diptam.client;

import com.diptam.search.ElasticSearchClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class SearchClient {

    @GetMapping(value = "/search/{keyword}", produces = MediaType.APPLICATION_JSON_VALUE)

    public String searchUsingKeyWords(@PathVariable String keyword){

        log.info(keyword);

        Optional<SearchResponse> response = Optional.empty();

        RestHighLevelClient client =  new ElasticSearchClientBuilder().getClient();

        SearchRequest searchRequest = new SearchRequest("tweets");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("text", keyword);
        searchSourceBuilder.query(matchQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        try {
            Instant start = Instant.now();
            response = Optional.ofNullable(client.search(searchRequest, RequestOptions.DEFAULT));
            Instant finish = Instant.now();

            log.info("Time taken : "+ Duration.between(start, finish).toMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.get().toString();
    }
}
