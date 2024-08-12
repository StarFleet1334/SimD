package com.demo.folder.command.action;

import com.demo.folder.broker.message.NotificationMessage;
import com.demo.folder.broker.producer.NotificationMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationMessageAction.class);

    @Autowired
    private NotificationMessageProducer notificationMessageProducer;

    public void uploadNotificationMessage(String content) {
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setMessage(content);
        LOGGER.info("Notification created successfully");
        notificationMessageProducer.publish(notificationMessage);
    }

}
