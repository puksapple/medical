package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.PurchaseDto;
import com.asarfi.acquirer.medical.dto.PurchaseItemDto;
import com.asarfi.acquirer.medical.entity.*;
import com.asarfi.acquirer.medical.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SupplierRepository supplierRepository;


    @Transactional
    public PurchaseDto createPurchase(PurchaseDto purchaseDto) {

        Company company = companyRepository.findById(purchaseDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Supplier supplier = supplierRepository.findById(purchaseDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        Purchase purchase = new Purchase();
        purchase.setCompany(company);
        purchase.setSupplier(supplier);
        purchase.setSupplierName(supplier.getName());
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
            stock.setPurchase(savedPurchase);
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
        response.setSupplierId(supplier.getId());
        response.setSupplierName(supplier.getName());
        response.setInvoiceNumber(finalPurchase.getInvoiceNumber());
        response.setTotalAmount(finalPurchase.getTotalAmount());

        return response;
    }

    @Transactional

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

            if (purchase.getSupplier() != null) {
                dto.setSupplierId(purchase.getSupplier().getId());
                dto.setSupplierName(purchase.getSupplier().getName());
            } else {
                dto.setSupplierName(purchase.getSupplierName());
            }

            return dto;

        }).toList();
    }

    @Transactional

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

        if (purchase.getSupplier() != null) {
            response.setSupplierId(purchase.getSupplier().getId());
            response.setSupplierName(purchase.getSupplier().getName());
        } else {
            response.setSupplierName(purchase.getSupplierName());
        }


        return response;
    }
    @Transactional
    public PurchaseDto updatePurchase(
            Long purchaseId,
            PurchaseDto purchaseDto
    ) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        if (purchaseDto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(purchaseDto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            purchase.setSupplier(supplier);
            purchase.setSupplierName(supplier.getName());
        }

        if (purchaseDto.getInvoiceNumber() != null) {
            purchase.setInvoiceNumber(purchaseDto.getInvoiceNumber());
        }

        List<PurchaseItem> purchaseItems =
                purchaseItemRepository.findByPurchase(purchase);

        if (purchaseDto.getItems() != null) {

            if (purchaseDto.getItems().size() != purchaseItems.size()) {
                throw new RuntimeException("Purchase item count cannot be changed during update");
            }

            for (int i = 0; i < purchaseDto.getItems().size(); i++) {

                PurchaseItemDto itemDto = purchaseDto.getItems().get(i);
                PurchaseItem item = purchaseItems.get(i);

                if (itemDto.getPurchasePrice() != null) {
                    item.setPurchasePrice(itemDto.getPurchasePrice());

                    BigDecimal subtotal = itemDto.getPurchasePrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    item.setSubtotal(subtotal);
                }

                if (itemDto.getBatchNo() != null) {
                    item.setBatchNo(itemDto.getBatchNo());
                }

                if (itemDto.getExpiryDate() != null) {
                    item.setExpiryDate(itemDto.getExpiryDate());
                }

                purchaseItemRepository.save(item);
            }
        }

        BigDecimal totalAmount = purchaseItemRepository.findByPurchase(purchase)
                .stream()
                .map(PurchaseItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        purchase.setTotalAmount(totalAmount);

        Purchase finalPurchase = purchaseRepository.save(purchase);

        PurchaseDto response = new PurchaseDto();
        response.setId(finalPurchase.getId());
        response.setCompanyId(finalPurchase.getCompany().getId());

        if (finalPurchase.getSupplier() != null) {
            response.setSupplierId(finalPurchase.getSupplier().getId());
            response.setSupplierName(finalPurchase.getSupplier().getName());
        } else {
            response.setSupplierName(finalPurchase.getSupplierName());
        }

        response.setInvoiceNumber(finalPurchase.getInvoiceNumber());
        response.setTotalAmount(finalPurchase.getTotalAmount());

        return response;
    }

}