
package com.diptam.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Shards {

    @SerializedName("total")
    @Expose
    private Long total;
    @SerializedName("successful")
    @Expose
    private Long successful;
    @SerializedName("skipped")
    @Expose
    private Long skipped;
    @SerializedName("failed")
    @Expose
    private Long failed;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSuccessful() {
        return successful;
    }

    public void setSuccessful(Long successful) {
        this.successful = successful;
    }

    public Long getSkipped() {
        return skipped;
    }

    public void setSkipped(Long skipped) {
        this.skipped = skipped;
    }

    public Long getFailed() {
        return failed;
    }

    public void setFailed(Long failed) {
        this.failed = failed;
    }

    @Override
    public String toString() {
        return "Shards{" +
                "total=" + total +
                ", successful=" + successful +
                ", skipped=" + skipped +
                ", failed=" + failed +
                '}';
    }
}
