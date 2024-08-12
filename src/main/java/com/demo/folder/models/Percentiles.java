package com.demo.folder.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Percentiles {

    private Double percentile25;
    private Double percentile50;
    private Double percentile75;
    private Double percentile80;
    private Double percentile85;
    private Double percentile90;
    private Double percentile95;
    private Double percentile99;
    private Integer min;
    private Integer max;

    public Percentiles() {}

    @JsonCreator
    public Percentiles(
            @JsonProperty("percentile25") Double percentile25,
            @JsonProperty("percentile50") Double percentile50,
            @JsonProperty("percentile75") Double percentile75,
            @JsonProperty("percentile80") Double percentile80,
            @JsonProperty("percentile85") Double percentile85,
            @JsonProperty("percentile90") Double percentile90,
            @JsonProperty("percentile95") Double percentile95,
            @JsonProperty("percentile99") Double percentile99,
            @JsonProperty("min") Integer min,
            @JsonProperty("max") Integer max
    ) {
        this.percentile25 = percentile25;
        this.percentile50 = percentile50;
        this.percentile75 = percentile75;
        this.percentile80 = percentile80;
        this.percentile85 = percentile85;
        this.percentile90 = percentile90;
        this.percentile95 = percentile95;
        this.percentile99 = percentile99;
        this.min = min;
        this.max = max;
    }

    public void calculate(List<Long> responseTimes) {
        if (!responseTimes.isEmpty()) {
            percentile25 = getPercentile(responseTimes, 25);
            percentile50 = getPercentile(responseTimes, 50);
            percentile75 = getPercentile(responseTimes, 75);
            percentile80 = getPercentile(responseTimes, 80);
            percentile85 = getPercentile(responseTimes, 85);
            percentile90 = getPercentile(responseTimes, 90);
            percentile95 = getPercentile(responseTimes, 95);
            percentile99 = getPercentile(responseTimes, 99);
            min = Collections.min(responseTimes).intValue();
            max = Collections.max(responseTimes).intValue();
        } else {
            percentile25 = 0d;
            percentile50 = 0d;
            percentile75 = 0d;
            percentile80 = 0d;
            percentile85 = 0d;
            percentile90 = 0d;
            percentile95 = 0d;
            percentile99 = 0d;
            min = 0;
            max = 0;
        }
    }

    public Double getPercentile(List<Long> responses, double percentile) {
        List<Long> sortedList = responses.stream()
                .sorted()
                .collect(Collectors.toList());
        double rank = (percentile / 100) * (sortedList.size() - 1);
        int intRank = (int) rank;
        if (rank == intRank) {
            return (double) sortedList.get(intRank);
        } else {
            return sortedList.get(intRank) + (rank - intRank) * (sortedList.get(intRank + 1) - sortedList.get(intRank));
        }
    }
}
