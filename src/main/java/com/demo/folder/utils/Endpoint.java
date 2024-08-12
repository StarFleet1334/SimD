package com.demo.folder.utils;

public enum Endpoint {
    SIMULATION_STATS("/api/files/simulationStats"),
    SINGLE_REQUEST_STATS("/api/files/singleRequestStats"),
    GENERAL_REQUEST_STATS("/api/files/generalRequestStats"),
    TIME_STATS("/api/files/timeStats");

    private final String url;

    Endpoint(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
