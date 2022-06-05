package com.borczeangelov.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.borczeangelov.orderservice.model.Order;

public interface OrderRepositroy extends JpaRepository<Order, Long> {

}
