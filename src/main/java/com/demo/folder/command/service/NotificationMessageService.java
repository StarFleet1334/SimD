package com.demo.folder.command.service;


import com.demo.folder.command.action.NotificationMessageAction;
import com.demo.folder.sim.SimulationParser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationMessageService {

    @Autowired
    private NotificationMessageAction notificationMessageAction;

    private String template;

    public void processNotificationMessageRequest() {
        notificationMessageAction.uploadNotificationMessage("New File has successfully been uploaded - continue decoupling");
    }

}
