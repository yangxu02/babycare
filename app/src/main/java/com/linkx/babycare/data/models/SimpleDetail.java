package com.linkx.babycare.data.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

/**
 * Created by ulyx.yang on 2016/9/15.
 */
@AutoValue
public abstract class SimpleDetail extends Model {
    @JsonProperty("id")
    public abstract String id();
    @JsonProperty("timestamp")
    public abstract long timestamp();
    @JsonProperty("type")
    public abstract String type();
    @JsonProperty("value")
    public abstract String value();

     @JsonCreator
    public static SimpleDetail create(@JsonProperty("id") String id,
                                      @JsonProperty("timestamp") long timestamp,
                                      @JsonProperty("type") String type,
                                      @JsonProperty("value") String value
     ) {
        return new AutoValue_SimpleDetail(id, timestamp, type, value);
    }

    @Override
    public String identity() throws MethodNotOverrideException {
        return id();
    }
}
