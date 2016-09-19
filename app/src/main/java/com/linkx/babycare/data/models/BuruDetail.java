package com.linkx.babycare.data.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

/**
 * Created by ulyx.yang on 2016/9/15.
 */
@AutoValue
public abstract class BuruDetail extends Model {
    @JsonProperty("id")
    public abstract String id();
    @JsonProperty("startTime")
    public abstract long startTime();
    @JsonProperty("endTime")
    public abstract long endTime();
    @JsonProperty("tag")
    public abstract String tag();

     @JsonCreator
    public static BuruDetail create(@JsonProperty("id") String id,
                                    @JsonProperty("startTime") long startTime,
                                    @JsonProperty("endTime") long endTime,
                                    @JsonProperty("tag") String tag

     ) {
        return new AutoValue_BuruDetail(id, startTime, endTime, tag);
    }

    @Override
    public String identity() throws MethodNotOverrideException {
        return id();
    }

}
