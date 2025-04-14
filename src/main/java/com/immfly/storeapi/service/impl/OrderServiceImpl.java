package com.immfly.storeapi.service.impl;

import com.immfly.storeapi.dto.OrderDTO;
import com.immfly.storeapi.dto.PaymentResponse;
import com.immfly.storeapi.enums.OrderStatus;
import com.immfly.storeapi.enums.PaymentStatus;
import com.immfly.storeapi.exception.*;
import com.immfly.storeapi.mapper.OrderMapper;
import com.immfly.storeapi.model.*;
import com.immfly.storeapi.repository.OrderRepository;
import com.immfly.storeapi.repository.ProductOrderRepository;
import com.immfly.storeapi.repository.ProductRepository;
import com.immfly.storeapi.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductOrderRepository productOrderRepository;
    private final RestTemplate restTemplate;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, ProductOrderRepository productOrderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productOrderRepository = productOrderRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = OrderMapper.toEntity(orderDTO);
        order.setStatus(OrderStatus.OPEN);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentDate(null);

        Order savedOrder = orderRepository.save(order);

        BigDecimal totalPrice = linkProductsAndCalculateTotalPrice(savedOrder, orderDTO.getProductIds());

        savedOrder.setTotalPrice(totalPrice);
        orderRepository.save(savedOrder);

        return orderRepository.findById(savedOrder.getId())
                .map(OrderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found after creation with id: " + savedOrder.getId()));
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (existingOrder.getStatus() == OrderStatus.FINISHED) {
            throw new OrderAlreadyFinishedException("Cannot update a finished order with id: " + id);
        }

        existingOrder.setBuyerEmail(orderDTO.getBuyerEmail());

        productOrderRepository.deleteAllByOrder(existingOrder);

        BigDecimal totalPrice = linkProductsAndCalculateTotalPrice(existingOrder, orderDTO.getProductIds());

        existingOrder.setTotalPrice(totalPrice);

        Order updatedOrder = orderRepository.save(existingOrder);
        return OrderMapper.toDto(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public OrderDTO finishOrder(Long id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (existingOrder.getStatus() == OrderStatus.FINISHED) {
            throw new OrderAlreadyFinishedException("Order already finished with id: " + id);
        }

        String baseUrl = "http://localhost:8080/mock-payment";
        String endpoint = switch (existingOrder.getPaymentGateway()) {
            case STRIPE -> "/stripe";
            case PAYPAL -> "/paypal";
            default -> throw new UnsupportedPaymentGatewayException("Unsupported payment gateway: " + existingOrder.getPaymentGateway());
        };

        String url = baseUrl + endpoint + "?cardToken=" + existingOrder.getCardToken() + "&amount=" + existingOrder.getTotalPrice();

        try {
            PaymentResponse paymentResponse = restTemplate.postForObject(url, null, PaymentResponse.class);

            if (paymentResponse == null || paymentResponse.getStatus() == null) {
                throw new PaymentStatusNullException("Payment gateway returned null for order id: " + id);
            }

            if ("success".equalsIgnoreCase(paymentResponse.getStatus())) {
                // FALTA DESCONTAR EL STOCK!!!!!
                existingOrder.setPaymentStatus(PaymentStatus.PAID);
                existingOrder.setStatus(OrderStatus.FINISHED);
                existingOrder.setPaymentDate(LocalDateTime.now());
            } else {
                existingOrder.setPaymentStatus(PaymentStatus.FAILED);
                existingOrder.setStatus(OrderStatus.DROPPED);
            }

            orderRepository.save(existingOrder);
            return OrderMapper.toDto(existingOrder);

        } catch (RestClientException ex) {
            throw new PaymentGatewayException("Error calling payment gateway: " + ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(Long id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (existingOrder.getStatus() == OrderStatus.FINISHED) {
            throw new OrderAlreadyFinishedException("Cannot cancel a finished order with id: " + id);
        }

        existingOrder.setStatus(OrderStatus.DROPPED);

        Order cancelledOrder = orderRepository.save(existingOrder);

        return OrderMapper.toDto(cancelledOrder);
    }

    private BigDecimal linkProductsAndCalculateTotalPrice(Order orderEntity, List<Long> productIds) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        if (productIds != null && !productIds.isEmpty()) {
            for (Long productId : productIds) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

                if (product.getStock() == null || product.getStock() <= 0) {
                    throw new OutOfStockException("Product out of stock: " + product.getName());
                }

                ProductOrder productOrder = new ProductOrder();
                productOrder.setId(new ProductOrderId(orderEntity.getId(), product.getId()));
                productOrder.setOrder(orderEntity);
                productOrder.setProduct(product);

                productOrderRepository.save(productOrder);

                totalPrice = totalPrice.add(product.getPrice());
            }
        }

        return totalPrice;
    }
}
