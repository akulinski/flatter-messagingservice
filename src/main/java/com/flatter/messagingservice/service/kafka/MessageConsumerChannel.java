package com.flatter.messagingservice.service.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MessageConsumerChannel {

    String CHANNEL = "input";

    @Input("input")
    SubscribableChannel subscribableChannel();

}
