package com.ipap.sagapaymentservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipap.sagacommonlibs.entity.OrderDao;
import com.ipap.sagapaymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    private final Logger log = LoggerFactory.getLogger(KafkaListeners.class.getName());
    private final ObjectMapper objectMapper;

    private final PaymentService paymentService;

    public KafkaListeners(ObjectMapper objectMapper, PaymentService paymentService) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "payment-updates-topic", groupId = "payment-group-0")
    private void listener(String data) {
        log.info("data: {}", data);
        try {
            OrderDao orderDao = objectMapper.readValue(data, OrderDao.class);
            paymentService.handle(orderDao);
        } catch (JsonProcessingException e) {
            log.error("Error whole deserializing data: {}", e.getMessage());
        }
    }

}
