package com.vaibhav.product_service.repository;

import com.vaibhav.product_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {

}
