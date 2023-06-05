package com.ipap.sagacommonlibs.util;

import com.ipap.sagacommonlibs.entity.OrderDao;
import com.ipap.sagacommonlibs.entity.PaymentDao;

public class PaymentMapper {

    public static PaymentDao toEntity(OrderDao order) {
        return PaymentDao.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .sellerId(order.getSellerId())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus("SUCCESS")
                .build();
    }
}
