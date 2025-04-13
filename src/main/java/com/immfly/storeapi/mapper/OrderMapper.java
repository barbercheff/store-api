package com.immfly.storeapi.mapper;

import com.immfly.storeapi.dto.OrderDTO;
import com.immfly.storeapi.model.Order;

public class OrderMapper {
    public static OrderDTO toDto(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setBuyerEmail(order.getBuyerEmail());
        dto.setCardToken(order.getCardToken());
        dto.setPaymentDate(order.getPaymentDate());
        dto.setPaymentGateway(order.getPaymentGateway());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setStatus(order.getStatus());
        dto.setSeatLetter(order.getSeatLetter());
        dto.setSeatNumber(order.getSeatNumber());
        dto.setTotalPrice(order.getTotalPrice());

        return dto;
    }

    public static Order toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }

        Order order = new Order();
        order.setId(dto.getId());
        order.setBuyerEmail(dto.getBuyerEmail());
        order.setCardToken(dto.getCardToken());
        order.setPaymentDate(dto.getPaymentDate());
        order.setPaymentGateway(dto.getPaymentGateway());
        order.setPaymentStatus(dto.getPaymentStatus());
        order.setStatus(dto.getStatus());
        order.setSeatLetter(dto.getSeatLetter());
        order.setSeatNumber(dto.getSeatNumber());
        order.setTotalPrice(dto.getTotalPrice());

        return order;
    }
}
