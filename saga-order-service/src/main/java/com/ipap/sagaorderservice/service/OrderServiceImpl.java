package com.ipap.sagaorderservice.service;

import com.ipap.sagacommonlibs.CreateOrderRequest;
import com.ipap.sagacommonlibs.GetOrderRequest;
import com.ipap.sagacommonlibs.OrderResponse;
import com.ipap.sagacommonlibs.OrderServiceGrpc;
import com.ipap.sagacommonlibs.entity.OrderDao;
import com.ipap.sagacommonlibs.entity.OrderStatus;
import com.ipap.sagaorderservice.repository.OrderRepository;
import com.ipap.sagacommonlibs.util.OrderMapper;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@GrpcService
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class.getName());

    private final OrderRepository orderRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderServiceImpl(OrderRepository orderRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        // Convert to Entity Pojo
        OrderDao orderDao = OrderMapper.toEntity(request);
        orderDao.setOrderId(UUID.randomUUID().toString());
        // Persist to MongoDB
        OrderDao savedOrderDao = orderRepository.save(orderDao);
        log.info("Order with orderId: {} saved to MongoDB", orderDao.getOrderId());
        // Send message to kafka topic
        kafkaTemplate.send("payment-updates-topic", savedOrderDao).completable().whenComplete(
                ((sr, ex) -> {
                    if (ex != null) {
                        log.error("Order with orderId {} failed to enqueue on 'payment-updates-topic'", orderDao.getOrderId());
                        // TODO: Implement fallback
                        return;
                    }
                    log.info("Order with orderId {} has published on 'payment-updates-topic' with details: {}",
                            orderDao.getOrderId(), sr.getRecordMetadata().toString());
                })
        );
        // Create response
        OrderResponse orderResponse = OrderMapper.toOrderResponse(savedOrderDao);
        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        // Retrieve from MongoDB
        Optional<OrderDao> orderDao = orderRepository.findById(request.getOrderId());
        if (orderDao.isEmpty()) {
            // TODO: Implement fallback
            log.error("Order not found!");
            throw new IllegalArgumentException("Order not found!");
        }
        OrderResponse orderResponse = OrderMapper.toOrderResponse(orderDao.get());
        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();
    }

    public void orderStatusUpdate(OrderStatus orderStatus) {
        orderRepository.findByOrderId(orderStatus.getOrderId()).ifPresentOrElse(order -> {
            order.setOrderStatus(orderStatus.getOrderState().name());
            order.setUpdatedTimestamp(new Timestamp(System.currentTimeMillis()).toString());
            orderRepository.save(order);
            log.info("Order status updated successfully: {}", orderStatus);
        }, () -> {
            // TODO: Implement fallback
            throw new IllegalArgumentException("Order not found in mongoDB");
        });
    }
}
