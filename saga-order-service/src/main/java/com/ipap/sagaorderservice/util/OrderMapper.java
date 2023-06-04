package com.ipap.sagaorderservice.util;

import com.ipap.sagacommonlibs.CreateOrderRequest;
import com.ipap.sagacommonlibs.OrderResponse;
import com.ipap.sagacommonlibs.Product;
import com.ipap.sagaorderservice.entity.Order;
import com.ipap.sagaorderservice.entity.ProductDao;

import java.sql.Timestamp;

public class OrderMapper {

    public static Order toEntity(CreateOrderRequest request) {
        Order order = new Order();
        order.setOrderStatus("ORDER_CREATED");
        order.setCustomerId(request.getCustomerId());
        order.setSellerId(request.getSellerId());
        ProductDao productDao = new ProductDao();
        productDao.setProductId(request.getProduct().getProductId());
        productDao.setProductName(request.getProduct().getProductName());
        order.setProductDao(productDao);
        order.setDeliveryLocation(request.getDeliveryLocation());
        order.setPaymentMethod(request.getPaymentMethod().toString());
        String time = new Timestamp(System.currentTimeMillis()).toString();
        order.setCreatedTimestamp(time);
        order.setUpdatedTimestamp(time);
        return order;
    }

    public static OrderResponse toOrderResponse(Order order) {
        Product product = Product.newBuilder()
                .setProductId(order.getProductDao().getProductId())
                .setProductName(order.getProductDao().getProductName())
                .build();

        return OrderResponse.newBuilder()
                .setCustomerId(order.getCustomerId())
                .setDeliveryLocation(order.getDeliveryLocation())
                .setPaymentMethod(order.getPaymentMethod())
                .setSellerId(order.getSellerId())
                .setOrderId(order.getOrderId())
                .setProduct(product)
                .setCreatedTime(order.getCreatedTimestamp())
                .setUpdatedTime(order.getUpdatedTimestamp())
                .build();
    }
}
