package com.linkx.babycare.data.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

/**
 * Created by ulyx.yang on 2016/9/15.
 */
@AutoValue
public abstract class DabianDetail extends Model {
    @JsonProperty("id")
    public abstract String id();
    @JsonProperty("timestamp")
    public abstract long timestamp();
    @JsonProperty("color")
    public abstract int color();
    @JsonProperty("quantity")
    public abstract Quantity quantity();
    @JsonProperty("softy")
    public abstract Softy softy();

    @JsonCreator
    public static DabianDetail create(@JsonProperty("id") String id,
                                      @JsonProperty("timestamp") long timestamp,
                                      @JsonProperty("color") int color,
                                      @JsonProperty("quantity") Quantity quantity,
                                      @JsonProperty("softy") Softy softy
    ) {
        return new AutoValue_DabianDetail(id, timestamp, color,
                null == quantity ? Quantity.appropriate : quantity,
                null == softy ? Softy.appropriate : softy);
    }

    @Override
    public String identity() throws MethodNotOverrideException {
        return id();
    }
}
