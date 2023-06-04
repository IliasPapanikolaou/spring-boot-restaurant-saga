package com.ipap.sagaorderservice.repository;

import com.ipap.sagaorderservice.entity.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderRepository extends ReactiveCrudRepository<Order, String> {
}
