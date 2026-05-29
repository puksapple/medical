package com.asarfi.acquirer.medical.controller;

import com.asarfi.acquirer.medical.dto.PurchaseDto;
import com.asarfi.acquirer.medical.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public PurchaseDto createPurchase(
            @RequestBody PurchaseDto purchaseDto
    ) {
        return purchaseService.createPurchase(purchaseDto);
    }

    @GetMapping("/company/{companyId}")
    public List<PurchaseDto> getPurchasesByCompany(
            @PathVariable Long companyId
    ) {
        return purchaseService.getPurchasesByCompany(companyId);
    }

    @GetMapping("/{purchaseId}")
    public PurchaseDto getPurchaseById(
            @PathVariable Long purchaseId
    ) {
        return purchaseService.getPurchaseById(purchaseId);
    }

    @PutMapping("/{purchaseId}")
    public PurchaseDto updatePurchase(
            @PathVariable Long purchaseId,
            @RequestBody PurchaseDto purchaseDto
    ) {
        return purchaseService.updatePurchase(purchaseId, purchaseDto);
    }
}