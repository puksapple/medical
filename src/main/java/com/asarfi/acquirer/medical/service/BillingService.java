package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.BillDto;
import com.asarfi.acquirer.medical.dto.BillItemDto;
import com.asarfi.acquirer.medical.entity.*;
import com.asarfi.acquirer.medical.entity.enums.MedicineUnit;
import com.asarfi.acquirer.medical.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final CompanyRepository companyRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineStockRepository medicineStockRepository;
    private final CustomerRepository customerRepository;
    private final BillItemStockRepository billItemStockRepository;


    @Transactional
    public BillDto createBill(BillDto billDto) {

        Company company = companyRepository.findById(billDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Customer customer = null;

        if (billDto.getCustomerId() != null) {
            customer = customerRepository.findById(billDto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
        }

        Bill bill = new Bill();
        bill.setCompany(company);
        bill.setPaymentMethod(billDto.getPaymentMethod());
        bill.setBillNumber("BILL-" + UUID.randomUUID().toString().substring(0, 8));
        bill.setCreatedAt(LocalDateTime.now());
        bill.setTotalAmount(BigDecimal.ZERO);
        bill.setReturnAmount(BigDecimal.ZERO);
        bill.setNetAmount(BigDecimal.ZERO);

        if (customer != null) {
            bill.setCustomer(customer);
            bill.setCustomerName(customer.getName());
        } else {
            bill.setCustomerName(billDto.getCustomerName());
        }

        Bill savedBill = billRepository.save(bill);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (BillItemDto itemDto : billDto.getItems()) {

            Medicine medicine = medicineRepository.findById(itemDto.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found"));

            Integer enteredQuantity = itemDto.getQuantity();

            if (enteredQuantity == null || enteredQuantity <= 0) {
                throw new RuntimeException("Sale quantity must be greater than zero");
            }

            MedicineUnit saleUnit = itemDto.getSaleUnit() != null
                    ? itemDto.getSaleUnit()
                    : medicine.getBaseUnit();

            if (saleUnit == null) {
                throw new RuntimeException("Sale unit is required");
            }

            int unitsPerPack = medicine.getUnitsPerPack() != null
                    && medicine.getUnitsPerPack() > 0
                    ? medicine.getUnitsPerPack()
                    : 1;

            int stockQuantity;

            if (saleUnit == medicine.getPackUnit()) {
                stockQuantity = enteredQuantity * unitsPerPack;
            } else if (saleUnit == medicine.getBaseUnit()) {
                stockQuantity = enteredQuantity;
            } else {
                throw new RuntimeException("Invalid sale unit for medicine " + medicine.getName());
            }

            List<MedicineStock> stocks =
                    medicineStockRepository.findByCompanyAndMedicineOrderByExpiryDateAsc(
                            company,
                            medicine
                    );

            int availableQuantity = stocks.stream()
                    .mapToInt(MedicineStock::getQuantity)
                    .sum();

            if (availableQuantity < stockQuantity) {
                throw new RuntimeException("Not enough stock for " + medicine.getName());
            }

            BigDecimal price = medicine.getPrice();

            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(stockQuantity)
            );

            BillItem billItem = new BillItem();
            billItem.setBill(savedBill);
            billItem.setMedicine(medicine);

            billItem.setQuantity(enteredQuantity);
            billItem.setSaleUnit(saleUnit);
            billItem.setStockQuantity(stockQuantity);

            billItem.setPrice(price);
            billItem.setSubtotal(subtotal);

            BillItem savedBillItem = billItemRepository.save(billItem);

            int remainingQuantity = stockQuantity;

            for (MedicineStock stock : stocks) {

                if (remainingQuantity <= 0) {
                    break;
                }

                int quantityTaken;

                if (stock.getQuantity() >= remainingQuantity) {
                    quantityTaken = remainingQuantity;
                } else {
                    quantityTaken = stock.getQuantity();
                }

                stock.setQuantity(stock.getQuantity() - quantityTaken);
                medicineStockRepository.save(stock);

                BillItemStock billItemStock = new BillItemStock();
                billItemStock.setBillItem(savedBillItem);
                billItemStock.setMedicineStock(stock);
                billItemStock.setQuantityUsed(quantityTaken);

                billItemStockRepository.save(billItemStock);

                remainingQuantity = remainingQuantity - quantityTaken;
            }

            totalAmount = totalAmount.add(subtotal);
        }

        BigDecimal discount = billDto.getDiscount() != null
                ? billDto.getDiscount()
                : BigDecimal.ZERO;

        BigDecimal netAmount = totalAmount.subtract(discount);

        savedBill.setDiscount(discount);
        savedBill.setTotalAmount(netAmount);
        savedBill.setReturnAmount(BigDecimal.ZERO);
        savedBill.setNetAmount(netAmount);

        Bill finalBill = billRepository.save(savedBill);

        BillDto response = new BillDto();
        response.setId(finalBill.getId());
        response.setCompanyId(company.getId());
        response.setBillNumber(finalBill.getBillNumber());
        response.setCustomerName(finalBill.getCustomerName());
        response.setPaymentMethod(finalBill.getPaymentMethod());
        response.setDiscount(finalBill.getDiscount());
        response.setTotalAmount(finalBill.getTotalAmount());
        response.setReturnAmount(finalBill.getReturnAmount());
        response.setNetAmount(finalBill.getNetAmount());

        if (finalBill.getCustomer() != null) {
            response.setCustomerId(finalBill.getCustomer().getId());
            response.setCustomerName(finalBill.getCustomer().getName());
        } else {
            response.setCustomerName(finalBill.getCustomerName());
        }

        return response;
    }

    @Transactional
    public BillDto getBillById(Long billId) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        List<BillItem> billItems = billItemRepository.findByBill(bill);

        List<BillItemDto> itemDtos = billItems.stream().map(item -> {

            BillItemDto itemDto = new BillItemDto();

            itemDto.setId(item.getId());

            itemDto.setMedicineId(item.getMedicine().getId());
            itemDto.setMedicineName(item.getMedicine().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getPrice());
            itemDto.setSubtotal(item.getSubtotal());
            itemDto.setSaleUnit(item.getSaleUnit());
            itemDto.setStockQuantity(item.getStockQuantity());

            return itemDto;

        }).toList();

        BillDto response = new BillDto();

        response.setId(bill.getId());
        response.setCompanyId(bill.getCompany().getId());
        response.setBillNumber(bill.getBillNumber());
        response.setCustomerName(bill.getCustomerName());
        response.setPaymentMethod(bill.getPaymentMethod());
        response.setDiscount(bill.getDiscount());
        response.setTotalAmount(bill.getTotalAmount());

        response.setReturnAmount(bill.getReturnAmount());
        response.setNetAmount(bill.getNetAmount());
        response.setItems(itemDtos);

        return response;
    }

    @Transactional
    public List<BillDto> getBillsByCompany(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<Bill> bills =
                billRepository.findByCompanyOrderByCreatedAtDesc(company);

        return bills.stream().map(bill -> {

            BillDto dto = new BillDto();

            dto.setId(bill.getId());
            dto.setCompanyId(company.getId());
            dto.setBillNumber(bill.getBillNumber());
            dto.setCustomerName(bill.getCustomerName());
            dto.setPaymentMethod(bill.getPaymentMethod());
            dto.setDiscount(bill.getDiscount());
            dto.setTotalAmount(bill.getTotalAmount());

            dto.setReturnAmount(bill.getReturnAmount());
            dto.setNetAmount(bill.getNetAmount());

            return dto;

        }).toList();
    }
}