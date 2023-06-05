package com.ipap.sagacommonlibs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order")
public class OrderDao {

    @MongoId
    private String id;
    @Indexed
    private String orderId;
    @Indexed
    private Integer customerId;
    private Integer sellerId;
    private ProductDao productDao;
    private String paymentMethod;
    private String deliveryLocation;
    private String orderStatus;
    private String createdTimestamp;
    private String updatedTimestamp;
}
