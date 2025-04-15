package com.immfly.storeapi.service;

import com.immfly.storeapi.dto.FinishOrderRequest;
import com.immfly.storeapi.dto.OrderDTO;
import com.immfly.storeapi.dto.PaymentResponse;
import com.immfly.storeapi.enums.OrderStatus;
import com.immfly.storeapi.enums.PaymentGateway;
import com.immfly.storeapi.enums.PaymentStatus;
import com.immfly.storeapi.exception.*;
import com.immfly.storeapi.model.Order;
import com.immfly.storeapi.model.Product;
import com.immfly.storeapi.model.ProductOrder;
import com.immfly.storeapi.model.ProductOrderId;
import com.immfly.storeapi.repository.OrderRepository;
import com.immfly.storeapi.repository.ProductOrderRepository;
import com.immfly.storeapi.repository.ProductRepository;
import com.immfly.storeapi.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    private OrderServiceImpl orderService;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private ProductOrderRepository productOrderRepository;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        productRepository = mock(ProductRepository.class);
        productOrderRepository = mock(ProductOrderRepository.class);
        restTemplate = mock(RestTemplate.class);
        orderService = new OrderServiceImpl(orderRepository, productRepository, productOrderRepository, restTemplate);
    }

    @Test
    void getOrderById_ExistingId_ReturnsOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setBuyerEmail("victor@gmail.com");
        order.setSeatLetter('A');
        order.setSeatNumber(5);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_NotFound_ThrowsException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(99L));
        verify(orderRepository).findById(99L);
    }

    @Test
    void getAllOrders_ReturnsListOfOrders() {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setBuyerEmail("user1@gmail.com");

        Order order2 = new Order();
        order2.setId(2L);
        order2.setBuyerEmail("user2@gmail.com");

        List<Order> mockOrders = List.of(order1, order2);

        when(orderRepository.findAll()).thenReturn(mockOrders);

        List<OrderDTO> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        assertEquals("user1@gmail.com", result.get(0).getBuyerEmail());
        assertEquals("user2@gmail.com", result.get(1).getBuyerEmail());
        verify(orderRepository).findAll();
    }

    @Test
    void deleteOrder_ValidId_DeletesOrderAndRelations() {
        Long id = 1L;
        Order order = new Order();
        order.setId(id);
        order.setStatus(OrderStatus.OPEN);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        doNothing().when(productOrderRepository).deleteAllByOrder(order);
        doNothing().when(orderRepository).delete(order);

        orderService.deleteOrder(id);

        verify(orderRepository).findById(id);
        verify(productOrderRepository).deleteAllByOrder(order);
        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_FinishedOrder_ThrowsException() {
        Long id = 1L;
        Order order = new Order();
        order.setId(id);
        order.setStatus(OrderStatus.FINISHED);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        assertThrows(OrderNotDeletableException.class, () -> orderService.deleteOrder(id));

        verify(orderRepository).findById(id);
        verify(orderRepository, never()).delete(order);
        verify(productOrderRepository, never()).deleteAllByOrder(order);
    }

    @Test
    void createOrder_WithNoProducts_SetsDefaultsCorrectly() {
        Order orderToSave = new Order();
        orderToSave.setBuyerEmail("victor@gmail.com");
        orderToSave.setSeatLetter('B');
        orderToSave.setSeatNumber(10);
        orderToSave.setTotalPrice(BigDecimal.ZERO);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setStatus(OrderStatus.OPEN);
        savedOrder.setPaymentStatus(PaymentStatus.PENDING);
        savedOrder.setBuyerEmail("victor@gmail.com");
        savedOrder.setTotalPrice(BigDecimal.ZERO);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        OrderDTO dto = new OrderDTO();
        dto.setBuyerEmail("victor@gmail.com");
        dto.setSeatLetter('B');
        dto.setSeatNumber(10);

        OrderDTO result = orderService.createOrder(dto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.OPEN);
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void createOrderWithProducts() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerEmail("victor@gmail.com");
        orderDTO.setSeatLetter('B');
        orderDTO.setSeatNumber(15);
        orderDTO.setProductIds(List.of(1L, 2L));

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product A");
        product1.setPrice(BigDecimal.valueOf(10));
        product1.setStock(5);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product B");
        product2.setPrice(BigDecimal.valueOf(5));
        product2.setStock(3);

        Order savedOrder = new Order();
        savedOrder.setId(99L);
        savedOrder.setBuyerEmail(orderDTO.getBuyerEmail());
        savedOrder.setSeatLetter(orderDTO.getSeatLetter());
        savedOrder.setSeatNumber(orderDTO.getSeatNumber());
        savedOrder.setStatus(OrderStatus.OPEN);
        savedOrder.setPaymentStatus(PaymentStatus.PENDING);
        savedOrder.setTotalPrice(BigDecimal.ZERO);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderRepository.findById(99L)).thenReturn(Optional.of(savedOrder));

        OrderDTO result = orderService.createOrder(orderDTO);

        assertNotNull(result);
        assertEquals(orderDTO.getBuyerEmail(), result.getBuyerEmail());
        assertEquals(OrderStatus.OPEN, result.getStatus());
        assertEquals(PaymentStatus.PENDING, result.getPaymentStatus());
        assertEquals(BigDecimal.valueOf(15), result.getTotalPrice());

        verify(productOrderRepository, times(2)).save(any(ProductOrder.class));
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void updateOrderWithNewProducts() {
        Long orderId = 1L;

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Ipad");
        product1.setPrice(BigDecimal.valueOf(20));
        product1.setStock(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Laptop");
        product2.setPrice(BigDecimal.valueOf(30));
        product2.setStock(5);

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setBuyerEmail("old@gmail.com");
        existingOrder.setSeatLetter('C');
        existingOrder.setSeatNumber(22);
        existingOrder.setStatus(OrderStatus.OPEN);
        existingOrder.setPaymentStatus(PaymentStatus.PENDING);
        existingOrder.setTotalPrice(BigDecimal.valueOf(0));

        OrderDTO updateDTO = new OrderDTO();
        updateDTO.setBuyerEmail("updated@gmail.com");
        updateDTO.setProductIds(List.of(1L, 2L));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        OrderDTO result = orderService.updateOrder(orderId, updateDTO);

        assertNotNull(result);
        assertEquals("updated@gmail.com", result.getBuyerEmail());
        assertEquals(BigDecimal.valueOf(50), result.getTotalPrice());

        verify(productOrderRepository).deleteAllByOrder(existingOrder);
        verify(productOrderRepository, times(2)).save(any(ProductOrder.class));
        verify(orderRepository).save(existingOrder);
    }

    @Test
    void updateOrder_ProductNotFound_ShouldThrowException() {
        Long orderId = 1L;
        Long invalidProductId = 99L;

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setBuyerEmail("victor@gmail.com");
        existingOrder.setStatus(OrderStatus.OPEN);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerEmail("victor@gmail.com");
        orderDTO.setProductIds(List.of(invalidProductId));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(productRepository.findById(invalidProductId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(orderId, orderDTO));

        verify(productOrderRepository).deleteAllByOrder(existingOrder);
        verify(productRepository).findById(invalidProductId);
    }

    @Test
    void updateOrder_OrderAlreadyFinished_ShouldThrowException() {
        Long orderId = 1L;

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(OrderStatus.FINISHED);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerEmail("victor@gmail.com");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        assertThrows(OrderNotUpdatableException.class, () -> orderService.updateOrder(orderId, orderDTO));

        verify(productOrderRepository, never()).deleteAllByOrder(any());
        verify(productRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrder_OrderNotFound_ShouldThrowException() {
        Long invalidOrderId = 99L;
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerEmail("victor@gmail.com");

        when(orderRepository.findById(invalidOrderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(invalidOrderId, orderDTO));

        verify(orderRepository).findById(invalidOrderId);
        verify(productOrderRepository, never()).deleteAllByOrder(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrder_ProductOutOfStock_ShouldThrowException() {
        Long orderId = 1L;

        Product inStockProduct = new Product();
        inStockProduct.setId(1L);
        inStockProduct.setName("Product OK");
        inStockProduct.setPrice(BigDecimal.valueOf(10));
        inStockProduct.setStock(5);

        Product outOfStockProduct = new Product();
        outOfStockProduct.setId(2L);
        outOfStockProduct.setName("Out of stock");
        outOfStockProduct.setPrice(BigDecimal.valueOf(15));
        outOfStockProduct.setStock(0);

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setBuyerEmail("victor@gmail.com");
        existingOrder.setStatus(OrderStatus.OPEN);

        OrderDTO updateDTO = new OrderDTO();
        updateDTO.setBuyerEmail("new@email.com");
        updateDTO.setProductIds(List.of(1L, 2L));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(inStockProduct));
        when(productRepository.findById(2L)).thenReturn(Optional.of(outOfStockProduct));

        assertThrows(OutOfStockException.class, () -> orderService.updateOrder(orderId, updateDTO));

        verify(productOrderRepository).deleteAllByOrder(existingOrder);
        verify(productOrderRepository, times(1)).save(any(ProductOrder.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_SuccessfullyCancelsOrderWithOpenStatus() {
        Long orderId = 1L;

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(OrderStatus.OPEN);

        Order cancelledOrder = new Order();
        cancelledOrder.setId(orderId);
        cancelledOrder.setStatus(OrderStatus.DROPPED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(cancelledOrder);

        OrderDTO result = orderService.cancelOrder(orderId);

        assertNotNull(result);
        assertEquals(OrderStatus.DROPPED, result.getStatus());
        verify(orderRepository).save(existingOrder);
    }

    @Test
    void cancelOrder_ThrowsExceptionIfOrderIsFinished() {
        Long orderId = 1L;

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(OrderStatus.FINISHED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        assertThrows(OrderNotUpdatableException.class, () -> orderService.cancelOrder(orderId));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_ThrowsExceptionIfOrderIsAlreadyDropped() {
        Long orderId = 1L;

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(OrderStatus.DROPPED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        assertThrows(OrderNotUpdatableException.class, () -> orderService.cancelOrder(orderId));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void finishOrder_Success() {
        Long orderId = 1L;
        String cardToken = "tok_123456";

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.TEN);
        product.setStock(5);

        Order order = new Order();
        order.setId(orderId);
        order.setTotalPrice(BigDecimal.TEN);
        order.setStatus(OrderStatus.OPEN);
        order.setBuyerEmail("victor@gmail.com");
        order.setProductOrders(List.of());

        ProductOrder productOrder = new ProductOrder();
        productOrder.setId(new ProductOrderId(orderId, product.getId()));
        productOrder.setProduct(product);
        productOrder.setOrder(order);

        order.setProductOrders(List.of(productOrder));

        FinishOrderRequest request = new FinishOrderRequest();
        request.setCardToken(cardToken);
        request.setPaymentGateway(PaymentGateway.STRIPE);

        PaymentResponse paymentResponse = new PaymentResponse("success", "txn_123", "Payment approved");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(restTemplate.postForObject(anyString(), isNull(), eq(PaymentResponse.class)))
                .thenReturn(paymentResponse);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO result = orderService.finishOrder(orderId, request);

        assertNotNull(result);
        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        assertEquals(OrderStatus.FINISHED, result.getStatus());
        assertEquals(4, product.getStock());

        verify(productRepository).save(product);
        verify(orderRepository).save(order);
    }

    @Test
    void finishOrder_AlreadyFinishedOrder_ShouldThrowException() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.FINISHED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        FinishOrderRequest request = new FinishOrderRequest("tok_123", PaymentGateway.STRIPE);

        assertThrows(OrderNotUpdatableException.class, () -> orderService.finishOrder(orderId, request));

        verify(orderRepository, never()).save(any());
    }


    @Test
    void finishOrder_PaymentResponseNull_ShouldThrowException() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.OPEN);
        order.setTotalPrice(BigDecimal.valueOf(100));
        order.setBuyerEmail("victor@gmail.com");

        FinishOrderRequest request = new FinishOrderRequest("tok_123", PaymentGateway.STRIPE);
        order.setCardToken(request.getCardToken());
        order.setPaymentGateway(request.getPaymentGateway());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(restTemplate.postForObject(anyString(), isNull(), eq(PaymentResponse.class)))
                .thenReturn(null);

        assertThrows(PaymentStatusNullException.class, () -> orderService.finishOrder(orderId, request));
    }

    @Test
    void finishOrder_ProductOutOfStock_ShouldThrowException() {
        Long orderId = 1L;

        Product product = new Product();
        product.setId(1L);
        product.setName("Item");
        product.setStock(0);

        ProductOrder productOrder = new ProductOrder();
        productOrder.setProduct(product);

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.OPEN);
        order.setTotalPrice(BigDecimal.valueOf(10));
        order.setProductOrders(List.of(productOrder));
        order.setBuyerEmail("victor@gmail.com");

        FinishOrderRequest request = new FinishOrderRequest("tok_123", PaymentGateway.STRIPE);
        order.setCardToken(request.getCardToken());
        order.setPaymentGateway(request.getPaymentGateway());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(restTemplate.postForObject(anyString(), isNull(), eq(PaymentResponse.class)))
                .thenReturn(new PaymentResponse("success", "tx123", "ok"));

        assertThrows(OutOfStockException.class, () -> orderService.finishOrder(orderId, request));
    }
}
