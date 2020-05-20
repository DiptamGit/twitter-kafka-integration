package com.diptam;

import com.diptam.search.ElasticSearchRecordCreator;

import java.io.IOException;

public class Test {

    public static void main(String[] args) {
        ElasticSearchRecordCreator elasticSearchRecordCreator = new ElasticSearchRecordCreator();
        try {
            elasticSearchRecordCreator.insert();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
