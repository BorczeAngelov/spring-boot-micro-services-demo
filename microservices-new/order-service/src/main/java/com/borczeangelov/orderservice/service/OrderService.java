package com.borczeangelov.orderservice.service;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.borczeangelov.orderservice.dto.InventoryResponse;
import com.borczeangelov.orderservice.dto.OrderLineItemsDto;
import com.borczeangelov.orderservice.dto.OrderRequest;
import com.borczeangelov.orderservice.model.Order;
import com.borczeangelov.orderservice.model.OrderLineItems;
import com.borczeangelov.orderservice.repository.OrderRepositroy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepositroy orderRepository;
    private final WebClient webClient;

    public void placceOrder(OrderRequest orderRequest) {
        var order = CreateOrder(orderRequest);

        var areAllProductsInStock = AreProductsInStock(order);

        if (areAllProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

    private Order CreateOrder(OrderRequest orderRequest) {
        var order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        var orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(items -> mapToDto(items))
                .toList();

        order.setOrderLineItemsList(orderLineItems);
        return order;
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }    

    /*
     * Make a HTTP Get request to InventoryService and
     * check if all products are in stock
     */
    private boolean AreProductsInStock(Order order) {
        var skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        var inventoryResponsArray = webClient.get()
                .uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build()) //?skuCode=iphone-13&skuCode=iphone13-red
                .retrieve()
                .bodyToMono(InventoryResponse[].class) //parse the object to InventoryResponse[]
                .block();

        var allProductsInStock = Arrays.stream(inventoryResponsArray)
                .allMatch(InventoryResponse::isInStock);

        return allProductsInStock;
    }
}
