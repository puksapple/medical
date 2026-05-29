package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.PurchaseDto;
import com.asarfi.acquirer.medical.dto.PurchaseItemDto;
import com.asarfi.acquirer.medical.entity.*;
import com.asarfi.acquirer.medical.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final CompanyRepository companyRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineStockRepository medicineStockRepository;

    public PurchaseDto createPurchase(PurchaseDto purchaseDto) {

        Company company = companyRepository.findById(purchaseDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Purchase purchase = new Purchase();
        purchase.setCompany(company);
        purchase.setSupplierName(purchaseDto.getSupplierName());
        purchase.setInvoiceNumber(purchaseDto.getInvoiceNumber());
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setTotalAmount(BigDecimal.ZERO);

        Purchase savedPurchase = purchaseRepository.save(purchase);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PurchaseItemDto itemDto : purchaseDto.getItems()) {

            Medicine medicine = medicineRepository.findById(itemDto.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found"));

            BigDecimal subtotal = itemDto.getPurchasePrice()
                    .multiply(BigDecimal.valueOf(itemDto.getQuantity()));

            PurchaseItem purchaseItem = new PurchaseItem();
            purchaseItem.setPurchase(savedPurchase);
            purchaseItem.setMedicine(medicine);
            purchaseItem.setQuantity(itemDto.getQuantity());
            purchaseItem.setPurchasePrice(itemDto.getPurchasePrice());
            purchaseItem.setSubtotal(subtotal);
            purchaseItem.setBatchNo(itemDto.getBatchNo());
            purchaseItem.setExpiryDate(itemDto.getExpiryDate());

            purchaseItemRepository.save(purchaseItem);

            MedicineStock stock = new MedicineStock();
            stock.setCompany(company);
            stock.setMedicine(medicine);
            stock.setQuantity(itemDto.getQuantity());
            stock.setBatchNo(itemDto.getBatchNo());
            stock.setExpiryDate(itemDto.getExpiryDate());
            stock.setCreatedAt(LocalDateTime.now());

            medicineStockRepository.save(stock);

            totalAmount = totalAmount.add(subtotal);
        }

        savedPurchase.setTotalAmount(totalAmount);

        Purchase finalPurchase = purchaseRepository.save(savedPurchase);

        PurchaseDto response = new PurchaseDto();
        response.setId(finalPurchase.getId());
        response.setCompanyId(company.getId());
        response.setSupplierName(finalPurchase.getSupplierName());
        response.setInvoiceNumber(finalPurchase.getInvoiceNumber());
        response.setTotalAmount(finalPurchase.getTotalAmount());

        return response;
    }


    public List<PurchaseDto> getPurchasesByCompany(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<Purchase> purchases =
                purchaseRepository.findByCompanyOrderByPurchaseDateDesc(company);

        return purchases.stream().map(purchase -> {

            PurchaseDto dto = new PurchaseDto();

            dto.setId(purchase.getId());
            dto.setCompanyId(company.getId());
            dto.setSupplierName(purchase.getSupplierName());
            dto.setInvoiceNumber(purchase.getInvoiceNumber());
            dto.setTotalAmount(purchase.getTotalAmount());

            return dto;

        }).toList();
    }


    public PurchaseDto getPurchaseById(Long purchaseId) {

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        List<PurchaseItem> purchaseItems =
                purchaseItemRepository.findByPurchase(purchase);

        List<PurchaseItemDto> itemDtos = purchaseItems.stream().map(item -> {

            PurchaseItemDto itemDto = new PurchaseItemDto();

            itemDto.setMedicineId(item.getMedicine().getId());
            itemDto.setMedicineName(item.getMedicine().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPurchasePrice(item.getPurchasePrice());
            itemDto.setSubtotal(item.getSubtotal());
            itemDto.setBatchNo(item.getBatchNo());
            itemDto.setExpiryDate(item.getExpiryDate());

            return itemDto;

        }).toList();

        PurchaseDto response = new PurchaseDto();

        response.setId(purchase.getId());
        response.setCompanyId(purchase.getCompany().getId());
        response.setSupplierName(purchase.getSupplierName());
        response.setInvoiceNumber(purchase.getInvoiceNumber());
        response.setTotalAmount(purchase.getTotalAmount());
        response.setItems(itemDtos);

        return response;
    }

    public PurchaseDto updatePurchase(
            Long purchaseId,
            PurchaseDto purchaseDto
    ) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        purchase.setSupplierName(purchaseDto.getSupplierName());
        purchase.setInvoiceNumber(purchaseDto.getInvoiceNumber());

        Purchase updatedPurchase = purchaseRepository.save(purchase);

        List<PurchaseItem> purchaseItems =
                purchaseItemRepository.findByPurchase(updatedPurchase);

        if (purchaseDto.getItems() != null) {

            for (int i = 0; i < purchaseDto.getItems().size(); i++) {

                PurchaseItemDto itemDto = purchaseDto.getItems().get(i);

                PurchaseItem item = purchaseItems.get(i);

                item.setPurchasePrice(itemDto.getPurchasePrice());
                item.setBatchNo(itemDto.getBatchNo());
                item.setExpiryDate(itemDto.getExpiryDate());

                BigDecimal subtotal = itemDto.getPurchasePrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                item.setSubtotal(subtotal);

                purchaseItemRepository.save(item);
            }
        }

        BigDecimal totalAmount = purchaseItems.stream()
                .map(PurchaseItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        updatedPurchase.setTotalAmount(totalAmount);

        Purchase finalPurchase = purchaseRepository.save(updatedPurchase);

        PurchaseDto response = new PurchaseDto();
        response.setId(finalPurchase.getId());
        response.setCompanyId(finalPurchase.getCompany().getId());
        response.setSupplierName(finalPurchase.getSupplierName());
        response.setInvoiceNumber(finalPurchase.getInvoiceNumber());
        response.setTotalAmount(finalPurchase.getTotalAmount());

        return response;
    }

}