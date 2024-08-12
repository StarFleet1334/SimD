package com.demo.folder.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.List;

@Getter
@Setter
public class SingleRequestStat {

    private String name;
    private boolean result = false;
    private String error;
    private Long responseTime;
    private Long start;
    private Long end;

    public SingleRequestStat() {
    }

    @JsonCreator
    public SingleRequestStat(
            @JsonProperty("name") String name,
            @JsonProperty("result") boolean result,
            @JsonProperty("error") String error,
            @JsonProperty("responseTime") Long responseTime,
            @JsonProperty("start") Long start,
            @JsonProperty("end") Long end
    ) {
        this.name = name;
        this.result = result;
        this.error = error;
        this.responseTime = responseTime;
        this.start = start;
        this.end = end;
    }

    public SingleRequestStat(List<String> line) {
        setName(line.get(2));
        setStart(Long.parseLong(line.get(3)));
        setEnd(Long.parseLong(line.get(4)));
        setResponseTime(Duration.ofMillis(end - start).toMillis());
        if (line.get(5).equals("OK")) {
            setResult(true);
        }
        if (!result) {
            setError(line.get(6));
        }
    }

    public boolean getResult() {
        return result;
    }
}
