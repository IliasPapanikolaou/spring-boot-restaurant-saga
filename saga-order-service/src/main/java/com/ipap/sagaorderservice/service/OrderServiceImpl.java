package com.ipap.sagaorderservice.service;

import com.ipap.sagacommonlibs.CreateOrderRequest;
import com.ipap.sagacommonlibs.GetOrderRequest;
import com.ipap.sagacommonlibs.OrderResponse;
import com.ipap.sagacommonlibs.OrderServiceGrpc;
import com.ipap.sagaorderservice.entity.Order;
import com.ipap.sagaorderservice.repository.OrderRepository;
import com.ipap.sagaorderservice.util.OrderMapper;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

@GrpcService
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class.getName());

    private final OrderRepository orderRepository;

    private final KafkaTemplate<String, Order> kafkaTemplate;

    public OrderServiceImpl(OrderRepository orderRepository, KafkaTemplate<String, Order> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        // Convert to Entity Pojo
        Order order = OrderMapper.toEntity(request);
        order.setOrderId(UUID.randomUUID().toString());
        // Persist to MongoDB TODO: Convert to non-blocking
        Order savedOrder = orderRepository.save(order).block();
        // Send message to kafka topic
        if (savedOrder == null) {
            // TODO: Implement fallback
            log.error("Something went wrong when saving to MongoDB!");
            throw new RuntimeException("Something went wrong when saving to MongoDB!");
        }
        // TODO: Handle completable future
        kafkaTemplate.send("payment-updates-topic", savedOrder);
        // Create response
        OrderResponse orderResponse = OrderMapper.toOrderResponse(savedOrder);
        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        // Retrieve from MongoDB TODO: Convert to non-blocking
        Order order = orderRepository.findById(request.getOrderId()).block();
        if (order == null) {
            // TODO: Implement fallback
            log.error("Order not found!");
            throw new IllegalArgumentException("Order not found!");
        }
        OrderResponse orderResponse = OrderMapper.toOrderResponse(order);
        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();
    }
}
