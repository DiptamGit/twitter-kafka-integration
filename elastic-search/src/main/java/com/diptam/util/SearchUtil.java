package com.diptam.util;

import com.diptam.model.Tweet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SearchUtil {

    public static String formatTweetAsJson(String json){
        Gson gson = new GsonBuilder().create();
        Tweet tweet = gson.fromJson(json, Tweet.class);
        return gson.toJson(tweet);
    }
}
