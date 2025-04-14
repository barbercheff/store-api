package com.immfly.storeapi.mapper;

import com.immfly.storeapi.dto.OrderDTO;
import com.immfly.storeapi.model.Order;
import com.immfly.storeapi.model.Product;
import com.immfly.storeapi.model.ProductOrder;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDTO toDto(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setBuyerEmail(order.getBuyerEmail());
        dto.setPaymentDate(order.getPaymentDate());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setStatus(order.getStatus());
        dto.setSeatLetter(order.getSeatLetter());
        dto.setSeatNumber(order.getSeatNumber());
        dto.setTotalPrice(order.getTotalPrice());

        if (order.getProductOrders() != null && !order.getProductOrders().isEmpty()) {
            dto.setProductIds(
                    order.getProductOrders().stream()
                            .map(productOrder -> productOrder.getProduct().getId())
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public static Order toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }

        Order order = new Order();
        order.setId(dto.getId());
        order.setBuyerEmail(dto.getBuyerEmail());
        order.setPaymentDate(dto.getPaymentDate());
        order.setPaymentStatus(dto.getPaymentStatus());
        order.setStatus(dto.getStatus());
        order.setSeatLetter(dto.getSeatLetter());
        order.setSeatNumber(dto.getSeatNumber());
        order.setTotalPrice(dto.getTotalPrice());

        return order;
    }
}
