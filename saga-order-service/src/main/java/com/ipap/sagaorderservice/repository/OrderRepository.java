package com.ipap.sagaorderservice.repository;

import com.ipap.sagacommonlibs.entity.OrderDao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderRepository extends MongoRepository<OrderDao, String> {
    Optional<OrderDao> findByOrderId(String orderId);
}
