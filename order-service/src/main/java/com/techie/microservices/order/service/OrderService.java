package com.techie.microservices.order.service;

import com.techie.microservices.order.client.InventoryClient;
import com.techie.microservices.order.dto.OrderRequest;
import com.techie.microservices.order.event.OrderPlacedEvent;
import com.techie.microservices.order.model.Order;
import com.techie.microservices.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String,OrderPlacedEvent> orderPlacedEventKafkaTemplate;

    @Autowired
    public OrderService(OrderRepository orderRepository,InventoryClient inventoryClient, KafkaTemplate<String,OrderPlacedEvent> orderPlacedEventKafkaTemplate){
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.orderPlacedEventKafkaTemplate = orderPlacedEventKafkaTemplate;
    }

    public void placeOrder(OrderRequest orderRequest){
        if(inventoryClient.isInStock(orderRequest.skuCode(),orderRequest.quantity())) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setQuantity(orderRequest.quantity());
            order.setSkuCode(orderRequest.skuCode());
            this.orderRepository.save(order);
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
            orderPlacedEvent.setOrderNumber(order.getOrderNumber());
            orderPlacedEvent.setEmail(orderRequest.userDetails().email());
            orderPlacedEvent.setFirstName(orderRequest.userDetails().firstName());
            orderPlacedEvent.setLastName(orderRequest.userDetails().lastName());
            System.out.println(orderPlacedEvent+" "+ order.getOrderNumber()+" "+ orderRequest.userDetails().email()+" "+ orderRequest.userDetails().firstName()+" "+ orderRequest.userDetails().lastName());
            orderPlacedEventKafkaTemplate.send("order-placed",orderPlacedEvent);
        }
        else{
            throw new RuntimeException("Product with skuCode - " + orderRequest.skuCode() + " is out of stock");
        }
    }
}
