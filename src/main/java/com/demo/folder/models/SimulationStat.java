package com.demo.folder.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static com.demo.folder.models.LineType.REQUEST;
import static com.demo.folder.models.LineType.USER;

@Getter
@Setter
public class SimulationStat {

    private String name;
    private String gatlingVersion;
    private Date date;
    private Long duration;
    private Long startTime;
    private Long endTime;

    public SimulationStat() {
    }

    public SimulationStat(List<String> header) {
        setName(header);
        setDate(header);
        setStartTime(header);
        setGatlingVersion(header);
    }

    public void setName(List<String> header) {
        this.name = header.get(2);
    }

    public void setGatlingVersion(List<String> header) {
        this.gatlingVersion = header.get(5);
    }

    public void setDate(List<String> header) {
        this.date = new Date(Long.parseLong(header.get(3)));
    }

    public void setStartTime(List<String> header) {
        this.startTime = Long.parseLong(header.get(3));
    }

    public void setEndTime(List<String> header) {
        switch (header.get(0)) {
            case REQUEST:
                this.endTime = Long.parseLong(header.get(4));
                break;
            case USER:
                this.endTime = Long.parseLong(header.get(3));
                break;
        }
        this.duration = Duration.of(endTime - startTime, ChronoUnit.MILLIS).toMillis();
    }

    // Jackson constructor
    @JsonCreator
    public SimulationStat(
            @JsonProperty("name") String name,
            @JsonProperty("gatlingVersion") String gatlingVersion,
            @JsonProperty("date") Date date,
            @JsonProperty("duration") Long duration,
            @JsonProperty("startTime") Long startTime,
            @JsonProperty("endTime") Long endTime
    ) {
        this.name = name;
        this.gatlingVersion = gatlingVersion;
        this.date = date;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
