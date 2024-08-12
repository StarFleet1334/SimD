package com.demo.folder.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class GeneralRequestStat {

    private String name;
    private Integer totalCount = 0;
    private Integer successCount = 0;
    private Integer failedCount = 0;
    private Double failedPercent;
    private Double minResponseTime;
    private Double maxResponseTime;
    private Double meanResponseTime;
    private Double standardDeviation;
    private Double percentile50;
    private Double percentile75;
    private Double percentile95;
    private Double percentile99;
    private List<Long> responseTimes = new ArrayList<>();
    private List<ErrorStat> errors = new ArrayList<>();

    public GeneralRequestStat() {
    }

    @JsonCreator
    public GeneralRequestStat(
            @JsonProperty("name") String name,
            @JsonProperty("totalCount") Integer totalCount,
            @JsonProperty("successCount") Integer successCount,
            @JsonProperty("failedCount") Integer failedCount,
            @JsonProperty("failedPercent") Double failedPercent,
            @JsonProperty("minResponseTime") Double minResponseTime,
            @JsonProperty("maxResponseTime") Double maxResponseTime,
            @JsonProperty("meanResponseTime") Double meanResponseTime,
            @JsonProperty("standardDeviation") Double standardDeviation,
            @JsonProperty("percentile50") Double percentile50,
            @JsonProperty("percentile75") Double percentile75,
            @JsonProperty("percentile95") Double percentile95,
            @JsonProperty("percentile99") Double percentile99,
            @JsonProperty("responseTimes") List<Long> responseTimes,
            @JsonProperty("errors") List<ErrorStat> errors
    ) {
        this.name = name;
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failedCount = failedCount;
        this.failedPercent = failedPercent;
        this.minResponseTime = minResponseTime;
        this.maxResponseTime = maxResponseTime;
        this.meanResponseTime = meanResponseTime;
        this.standardDeviation = standardDeviation;
        this.percentile50 = percentile50;
        this.percentile75 = percentile75;
        this.percentile95 = percentile95;
        this.percentile99 = percentile99;
        this.responseTimes = responseTimes;
        this.errors = errors;
    }

    public GeneralRequestStat(String name) {
        this.name = name;
    }

    public void addSuccess() {
        totalCount++;
        successCount++;
    }

    public void addFailed() {
        totalCount++;
        failedCount++;
    }

    public void addResponseTime(Long responseTime) {
        responseTimes.add(responseTime);
    }

    public void addError(String error) {
        ErrorStat newErrorStat = new ErrorStat(error, 1);
        if (errors.contains(newErrorStat)) {
            errors = errors.stream()
                    .map(errorStat -> {
                        if (errorStat.equals(newErrorStat)) {
                            errorStat.setCount(errorStat.getCount() + 1);
                        }
                        return errorStat;
                    })
                    .collect(Collectors.toList());
        } else {
            errors.add(newErrorStat);
        }
    }

    public void countResult(SingleRequestStat request) {
        if (request.getResult()) {
            addSuccess();
        } else {
            addFailed();
            addError(request.getError());
        }
        addResponseTime(request.getResponseTime());
    }

    public void calculateMetrics() {
        failedPercent = (double) failedCount / totalCount * 100;
        double[] responses = responseTimes.stream().mapToDouble(Long::doubleValue).toArray();
        minResponseTime = StatUtils.min(responses);
        maxResponseTime = StatUtils.max(responses);
        meanResponseTime = StatUtils.mean(responses);
        StandardDeviation stdDev = new StandardDeviation();
        standardDeviation = stdDev.evaluate(responses, meanResponseTime);
        percentile50 = StatUtils.percentile(responses, 50);
        percentile75 = StatUtils.percentile(responses, 75);
        percentile95 = StatUtils.percentile(responses, 95);
        percentile99 = StatUtils.percentile(responses, 99);
    }
}
