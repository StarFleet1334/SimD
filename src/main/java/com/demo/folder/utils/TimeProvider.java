package com.demo.folder.utils;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeProvider {

    private LocalDateTime currentTime;
    @Getter
    @Setter
    private String template;

    public TimeProvider() {
        this.template = "yyyy-MM-dd HH:mm:ss";
    }

    public LocalDateTime getCurrentTime() {
        this.currentTime = LocalDateTime.now();
        return this.currentTime;
    }

    public String getFormattedCurrentTime(String pattern) {
        this.currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return this.currentTime.format(formatter);
    }

}

