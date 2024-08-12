package com.demo.folder.broker.producer;

import com.demo.folder.broker.message.NotificationMessage;
import com.demo.folder.utils.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationMessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationMessageProducer.class);

    @Autowired
    private KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    private final TimeProvider timeProvider = new TimeProvider();

    public void publish(NotificationMessage notificationMessage) {
        kafkaTemplate.send("t-notification-message",notificationMessage);
        LOGGER.info("Notification message published to topic at time: {}", timeProvider.getFormattedCurrentTime(timeProvider.getTemplate()));
    }


}
