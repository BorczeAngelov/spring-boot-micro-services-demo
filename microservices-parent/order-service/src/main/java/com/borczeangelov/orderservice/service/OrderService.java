package com.borczeangelov.orderservice.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.borczeangelov.orderservice.dto.OrderLineItemsDto;
import com.borczeangelov.orderservice.dto.OrderRequest;
import com.borczeangelov.orderservice.model.Order;
import com.borczeangelov.orderservice.model.OrderLineItems;
import com.borczeangelov.orderservice.repository.OrderRepositroy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepositroy orderRepositroy;

    public void placceOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        var orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(items -> mapToDto(items))
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        orderRepositroy.save(order);
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
