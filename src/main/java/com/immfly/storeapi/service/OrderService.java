package com.immfly.storeapi.service;

import com.immfly.storeapi.dto.FinishOrderRequest;
import com.immfly.storeapi.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    OrderDTO getOrderById(Long id);
    OrderDTO createOrder(OrderDTO order);
    List<OrderDTO> getAllOrders();
    OrderDTO updateOrder(Long id, OrderDTO order);
    void deleteOrder(Long id);
    OrderDTO finishOrder(Long id, FinishOrderRequest request);
    OrderDTO cancelOrder(Long id);
}
