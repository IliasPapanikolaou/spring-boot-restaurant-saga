package com.ipap.sagacommonlibs.util;

import com.ipap.sagacommonlibs.CreateOrderRequest;
import com.ipap.sagacommonlibs.OrderResponse;
import com.ipap.sagacommonlibs.Product;
import com.ipap.sagacommonlibs.entity.OrderDao;
import com.ipap.sagacommonlibs.entity.ProductDao;

import java.sql.Timestamp;

public class OrderMapper {

    public static OrderDao toEntity(CreateOrderRequest request) {
        OrderDao orderDao = new OrderDao();
        orderDao.setOrderStatus("ORDER_CREATED");
        orderDao.setCustomerId(request.getCustomerId());
        orderDao.setSellerId(request.getSellerId());
        ProductDao productDao = new ProductDao();
        productDao.setProductId(request.getProduct().getProductId());
        productDao.setProductName(request.getProduct().getProductName());
        orderDao.setProductDao(productDao);
        orderDao.setDeliveryLocation(request.getDeliveryLocation());
        orderDao.setPaymentMethod(request.getPaymentMethod().toString());
        String time = new Timestamp(System.currentTimeMillis()).toString();
        orderDao.setCreatedTimestamp(time);
        orderDao.setUpdatedTimestamp(time);
        return orderDao;
    }

    public static OrderResponse toOrderResponse(OrderDao orderDao) {
        Product product = Product.newBuilder()
                .setProductId(orderDao.getProductDao().getProductId())
                .setProductName(orderDao.getProductDao().getProductName())
                .build();

        return OrderResponse.newBuilder()
                .setCustomerId(orderDao.getCustomerId())
                .setDeliveryLocation(orderDao.getDeliveryLocation())
                .setPaymentMethod(orderDao.getPaymentMethod())
                .setSellerId(orderDao.getSellerId())
                .setOrderId(orderDao.getOrderId())
                .setProduct(product)
                .setCreatedTime(orderDao.getCreatedTimestamp())
                .setUpdatedTime(orderDao.getUpdatedTimestamp())
                .build();
    }
}
