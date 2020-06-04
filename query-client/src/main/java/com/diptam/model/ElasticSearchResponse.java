
package com.diptam.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ElasticSearchResponse {

    @SerializedName("took")
    @Expose
    private Long took;
    @SerializedName("timed_out")
    @Expose
    private Boolean timedOut;
    @SerializedName("_shards")
    @Expose
    private Shards shards;
    @SerializedName("hits")
    @Expose
    private Hits hits;

    public Long getTook() {
        return took;
    }

    public void setTook(Long took) {
        this.took = took;
    }

    public Boolean getTimedOut() {
        return timedOut;
    }

    public void setTimedOut(Boolean timedOut) {
        this.timedOut = timedOut;
    }

    public Shards getShards() {
        return shards;
    }

    public void setShards(Shards shards) {
        this.shards = shards;
    }

    public Hits getHits() {
        return hits;
    }

    public void setHits(Hits hits) {
        this.hits = hits;
    }

    @Override
    public String toString() {
        return "ElasticSearchResponse{" +
                "took=" + took +
                ", timedOut=" + timedOut +
                ", shards=" + shards +
                ", hits=" + hits +
                '}';
    }
}
