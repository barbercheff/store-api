package com.immfly.storeapi.service;

import com.immfly.storeapi.dto.OrderDTO;
import com.immfly.storeapi.enums.OrderStatus;
import com.immfly.storeapi.enums.PaymentStatus;
import com.immfly.storeapi.exception.ResourceNotFoundException;
import com.immfly.storeapi.model.Order;
import com.immfly.storeapi.model.Product;
import com.immfly.storeapi.model.ProductOrder;
import com.immfly.storeapi.repository.OrderRepository;
import com.immfly.storeapi.repository.ProductOrderRepository;
import com.immfly.storeapi.repository.ProductRepository;
import com.immfly.storeapi.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOrderRepository productOrderRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getOrderById_existingId_returnsOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setBuyerEmail("test@example.com");
        order.setSeatLetter('A');
        order.setSeatNumber(5);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_notFound_throwsException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(99L));
        verify(orderRepository).findById(99L);
    }

    @Test
    void deleteOrder_validId_deletesSuccessfully() {
        Long id = 1L;
        doNothing().when(orderRepository).deleteById(id);

        orderService.deleteOrder(id);

        verify(orderRepository).deleteById(id);
    }

    @Test
    void createOrder_withNoProducts_setsDefaultsCorrectly() {
        Order orderToSave = new Order();
        orderToSave.setBuyerEmail("test@example.com");
        orderToSave.setSeatLetter('B');
        orderToSave.setSeatNumber(10);
        orderToSave.setTotalPrice(BigDecimal.ZERO);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setStatus(OrderStatus.OPEN);
        savedOrder.setPaymentStatus(PaymentStatus.PENDING);
        savedOrder.setBuyerEmail("test@example.com");
        savedOrder.setTotalPrice(BigDecimal.ZERO);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        OrderDTO dto = new OrderDTO();
        dto.setBuyerEmail("test@example.com");
        dto.setSeatLetter('B');
        dto.setSeatNumber(10);

        OrderDTO result = orderService.createOrder(dto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.OPEN);
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void testCreateOrderWithProducts() {
        // Arrange
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerEmail("test@example.com");
        orderDTO.setSeatLetter('B');
        orderDTO.setSeatNumber(15);
        orderDTO.setProductIds(List.of(1L, 2L));

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Producto A");
        product1.setPrice(BigDecimal.valueOf(10));
        product1.setStock(5);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Producto B");
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

    // Más tests se podrían añadir para casos con productos, fallos de stock, finishOrder, etc.
    /*
    Luego puedes expandir con tests más específicos:

    Actualizar producto

    Cancelar orden

    Falta de stock.

    finishOrder con cada resultado posible del gateway.

    Validaciones lanzando tus excepciones personalizadas.
     */
}
