package com.ipap.sagacommonlibs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "product")
public class ProductDao {
    @MongoId
    private String id;
    private Integer productId;
    private String productName;
}
