package com.ipap.sagaorderservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipap.sagacommonlibs.entity.OrderStatus;
import com.ipap.sagaorderservice.service.OrderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    private final Logger log = LoggerFactory.getLogger(KafkaListeners.class.getName());

    private final OrderServiceImpl orderServiceImpl;
    private final ObjectMapper objectMapper;

    public KafkaListeners(OrderServiceImpl orderServiceImpl, ObjectMapper objectMapper) {
        this.orderServiceImpl = orderServiceImpl;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-updates-topic", groupId = "group-0")
    private void listener(String data) {
        log.info("data: {}", data);
        try {
            OrderStatus orderStatus = objectMapper.readValue(data, OrderStatus.class);
            orderServiceImpl.orderStatusUpdate(orderStatus);
        } catch (JsonProcessingException e) {
            log.error("Error whole deserializing data: {}", e.getMessage());
        }
    }
}
