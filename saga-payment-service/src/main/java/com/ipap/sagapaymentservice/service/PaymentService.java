package com.ipap.sagapaymentservice.service;

import com.ipap.sagacommonlibs.entity.OrderDao;
import com.ipap.sagacommonlibs.entity.OrderState;
import com.ipap.sagacommonlibs.entity.OrderStatus;
import com.ipap.sagacommonlibs.entity.PaymentDao;
import com.ipap.sagacommonlibs.util.PaymentMapper;
import com.ipap.sagapaymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentService.class.getName());

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentService(PaymentRepository paymentRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void handle(OrderDao order) {
        PaymentDao paymentDao = PaymentMapper.toEntity(order);
        // Persist to MongoDB
        paymentRepository.save(paymentDao);
        // Publish paymentDto to "restaurant-updates-topic"
        publishToRestaurantUpdatesTopic(paymentDao);
        // Publish to "order-updates-topic"
        publishToOrderUpdatesTopic(order, paymentDao);
    }

    private void publishToRestaurantUpdatesTopic(PaymentDao paymentDao) {
        if (!Objects.equals(paymentDao.getPaymentStatus(), "SUCCESS")) {
            throw new IllegalArgumentException("Payment cannot be completed!");
        }
        log.info("Publishing order with orderId: {}", paymentDao.getOrderId());
        // Send to restaurant-updates-topic
        kafkaTemplate.send("restaurant-updates-topic", paymentDao).completable().whenComplete(
                (sr, ex) -> {
                    if (ex != null) {
                        log.error("Payment with orderId {} failed to enqueue on 'payment-updates-topic'", paymentDao.getOrderId());
                        // TODO: Implement fallback
                        return;
                    }
                    log.info("Payment with orderId {} has published on 'payment-updates-topic' with details: {}",
                            paymentDao.getOrderId(), sr.getRecordMetadata().toString());
                }
        );
    }

    private void publishToOrderUpdatesTopic(OrderDao order, PaymentDao paymentDao) {
        // Send order status message to Order-Service
        OrderStatus orderStatus = OrderStatus.builder()
                .orderId(order.getOrderId())
                .orderState(OrderState.ORDER_PAID)
                .message("Successfully paid by " + paymentDao.getPaymentMethod())
                .build();
        // Send to topic order-updates
        kafkaTemplate.send("order-updates-topic", orderStatus).completable().whenComplete(
                ((sr, ex) -> {
                    if (ex != null) {
                        log.error("Order with orderId {} failed to enqueue on 'order-updates-topic'", orderStatus.getOrderId());
                        // TODO: Implement fallback
                        return;
                    }
                    log.info("Order with orderId {} has published on 'order-updates-topic' with details: {}",
                            orderStatus.getOrderId(), sr.getRecordMetadata().toString());
                })
        );
    }
}
