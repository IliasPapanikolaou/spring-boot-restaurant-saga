package com.ipap.sagapaymentservice.repository;

import com.ipap.sagacommonlibs.entity.PaymentDao;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<PaymentDao, String> {

}
