package com.techie.microservices.inventory.service;

import com.techie.microservices.inventory.dto.InventoryRequest;
import com.techie.microservices.inventory.model.Inventory;
import com.techie.microservices.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository){
        this.inventoryRepository = inventoryRepository;
    }

    public boolean isInStock(String skuCode,Integer quantity){
        return this.inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(skuCode,quantity);
    }

    public Inventory createInventory(InventoryRequest inventoryRequest){
        Inventory inventory = inventoryRepository.findBySkuCode(inventoryRequest.skuCode())
                .orElse(new Inventory());

        // Update or set properties
        inventory.setSkuCode(inventoryRequest.skuCode());
        inventory.setQuantity(inventoryRequest.quantity());

        // Save the inventory
        System.out.println(inventory.getId() == null ? "Created Inventory" : "Updated Inventory");
        return inventoryRepository.save(inventory);
    }

    public Integer getStockCount(String skuCode){
        return inventoryRepository.findBySkuCode(skuCode)
                .map(Inventory::getQuantity)
                .orElseThrow(() -> new RuntimeException("Inventory not found for SKU code: " + skuCode));
    }
}
