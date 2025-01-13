package com.techie.microservices.inventory.controller;

import com.techie.microservices.inventory.dto.InventoryRequest;
import com.techie.microservices.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.techie.microservices.inventory.model.Inventory;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService){
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity){
        return this.inventoryService.isInStock(skuCode,quantity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Inventory createInventory(@RequestBody InventoryRequest inventoryRequest){
        return this.inventoryService.createInventory(inventoryRequest);
    }

    @GetMapping("/getStockCount")
    @ResponseStatus(HttpStatus.OK)
    public Integer getStockCount(@RequestParam String skuCode){
        return this.inventoryService.getStockCount(skuCode);
    }
}
