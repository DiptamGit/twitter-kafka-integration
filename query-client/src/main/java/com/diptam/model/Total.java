
package com.diptam.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Total {

    @SerializedName("value")
    @Expose
    private Long value;
    @SerializedName("relation")
    @Expose
    private String relation;

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return "Total{" +
                "value=" + value +
                ", relation='" + relation + '\'' +
                '}';
    }
}
