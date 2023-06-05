package com.ipap.sagacommonlibs.entity;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatus {

    private String orderId;
    private OrderState orderState;
    private String message;
}
