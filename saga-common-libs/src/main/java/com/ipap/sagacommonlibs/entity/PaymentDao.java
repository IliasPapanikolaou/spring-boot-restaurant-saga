package com.ipap.sagacommonlibs.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payment")
public class PaymentDao {
    @MongoId
    private String paymentId;
    private String orderId;
    private Integer customerId;
    private Integer sellerId;
    private String paymentMethod;
    private String paymentStatus;
    private String paymentStatusNotes;
}
