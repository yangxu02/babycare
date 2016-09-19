package com.linkx.babycare.data.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

/**
 * Created by ulyx.yang on 2016/9/15.
 */
@AutoValue
public abstract class AllDetail extends Model {
    @JsonProperty("id")
    public abstract String id();
    @JsonProperty("day")
    public abstract String day();
    @JsonProperty("buru")
    public abstract List<BuruDetail> buruDetails();
    @JsonProperty("dabian")
    public abstract List<DabianDetail> dabianDetails();
    @JsonProperty("xiaobian")
    public abstract List<SimpleDetail> xiaobianDetails();
    @JsonProperty("huangdan")
    public abstract List<SimpleDetail> huangdan();
    @JsonProperty("tizhong")
    public abstract List<SimpleDetail> tizhong();
    @JsonProperty("tiwen")
    public abstract List<SimpleDetail> tiwen();

     @JsonCreator
    public static AllDetail create(
             @JsonProperty("id") String id,
             @JsonProperty("day") String day,
             @JsonProperty("buru") List<BuruDetail> buruDetails,
             @JsonProperty("dabian") List<DabianDetail> dabianDetails,
             @JsonProperty("xiaobian") List<SimpleDetail> xiaobianDetails,
             @JsonProperty("huangdan") List<SimpleDetail> huangdan,
             @JsonProperty("tizhong") List<SimpleDetail> tizhong,
             @JsonProperty("tiwen") List<SimpleDetail> tiwen
     ) {
        return new AutoValue_AllDetail(id, day, buruDetails, dabianDetails, xiaobianDetails, huangdan, tizhong, tiwen);
    }

    @Override
    public String identity() throws MethodNotOverrideException {
        return id();
    }
}
