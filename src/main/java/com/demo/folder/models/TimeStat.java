package com.demo.folder.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TimeStat {

    private List<UserStat> stats = new ArrayList<>();
    private Map<Long, UserStat> timestampStats = new LinkedHashMap<>();
    private Integer activeUsers = 0;
    private Integer maxUsers = 0;

    public TimeStat() {
    }

    @JsonCreator
    public TimeStat(
            @JsonProperty("stats") List<UserStat> stats,
            @JsonProperty("activeUsers") Integer activeUsers,
            @JsonProperty("maxUsers") Integer maxUsers
    ) {
        this.stats = stats;
        this.activeUsers = activeUsers;
        this.maxUsers = maxUsers;
    }

    @Getter
    @Setter
    public static class UserStat {
        private Integer numberOfUsers;
        private Integer requestsPerSec;
        private Integer responsesPerSec;
        private Integer successResponse;
        private Integer failedResponse;
        private List<Long> responseTimes = new ArrayList<>();
        private Percentiles percentiles;
        private Long timestamp;

        // Default constructor
        public UserStat() {
        }

        @JsonCreator
        public UserStat(
                @JsonProperty("numberOfUsers") Integer numberOfUsers,
                @JsonProperty("requestsPerSec") Integer requestsPerSec,
                @JsonProperty("responsesPerSec") Integer responsesPerSec,
                @JsonProperty("successResponse") Integer successResponse,
                @JsonProperty("failedResponse") Integer failedResponse,
                @JsonProperty("responseTimes") List<Long> responseTimes,
                @JsonProperty("percentiles") Percentiles percentiles,
                @JsonProperty("timestamp") Long timestamp
        ) {
            this.numberOfUsers = numberOfUsers;
            this.requestsPerSec = requestsPerSec;
            this.responsesPerSec = responsesPerSec;
            this.successResponse = successResponse;
            this.failedResponse = failedResponse;
            this.responseTimes = responseTimes;
            this.percentiles = percentiles;
            this.timestamp = timestamp;
        }

        public UserStat(Long timestamp) {
            this.numberOfUsers = 0;
            this.requestsPerSec = 0;
            this.responsesPerSec = 0;
            this.successResponse = 0;
            this.failedResponse = 0;
            this.timestamp = timestamp;
            this.percentiles = new Percentiles();
        }

        public void addUser() {
            numberOfUsers++;
        }

        public void addRequest() {
            requestsPerSec++;
        }

        public void addSuccessResponse() {
            successResponse++;
            responsesPerSec++;
        }

        public void addFailedResponse() {
            failedResponse++;
            responsesPerSec++;
        }

        public void calculatePercentiles() {
            percentiles.calculate(responseTimes);
        }

        public void setResponseTimes(List<SingleRequestStat> allRequests) {
            allRequests.stream()
                    .filter(request -> (request.getStart() / 1000) == (timestamp))
                    .forEach(request -> responseTimes.add(request.getResponseTime()));
        }
    }

    public void updateRequest(List<String> line) {
        Long timestamp = Long.parseLong(line.get(3)) / 1000;
        Long response = Long.parseLong(line.get(3)) / 1000;
        timestampStats.computeIfAbsent(timestamp, UserStat::new);
        timestampStats.computeIfAbsent(response, UserStat::new);
        timestampStats.get(timestamp).addRequest();
        if (line.get(5).equals("OK")) {
            timestampStats.get(response).addSuccessResponse();
        } else {
            timestampStats.get(response).addFailedResponse();
        }
    }

    public void updateUser(List<String> line) {
        Long timestamp = Long.parseLong(line.get(3)) / 1000;
        timestampStats.computeIfAbsent(timestamp, UserStat::new);
        if (line.get(2).equals("START")) {
            timestampStats.get(timestamp).addUser();
            activeUsers++;
            if (maxUsers < activeUsers) {
                maxUsers = activeUsers;
            }
        } else {
            activeUsers--;
        }
    }

    public void convert() {
        stats = new ArrayList<>(timestampStats.values());
    }

    public void addResponseTimes(List<SingleRequestStat> allRequests) {
        stats.forEach(stat -> stat.setResponseTimes(allRequests));
    }

    public void calculatePercentiles() {
        stats.forEach(UserStat::calculatePercentiles);
    }
}
