package com.immfly.storeapi.repository;

import com.immfly.storeapi.model.Order;
import com.immfly.storeapi.model.ProductOrder;
import com.immfly.storeapi.model.ProductOrderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, ProductOrderId> {
    void deleteAllByOrder(Order order);
}
