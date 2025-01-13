package com.techie.microservices.order.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface InventoryClient {

    Logger log = LoggerFactory.getLogger(InventoryClient.class);

    @GetExchange("/api/inventory")
    @CircuitBreaker(name="inventory",fallbackMethod = "fallbackMethod")
    @Retry(name="inventory")
    public boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);

    default boolean fallbackMethod(String skuCode,Integer quantity,Throwable throwable){
        log.info("Can't get inventory for skuCode - " + skuCode + " " + throwable.getMessage());
        return false;
    }
}
