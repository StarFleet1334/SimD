package com.demo.folder.broker.message;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotificationMessage {
    private String message;

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
