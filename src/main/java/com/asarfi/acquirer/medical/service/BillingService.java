package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.BillDto;
import com.asarfi.acquirer.medical.dto.BillItemDto;
import com.asarfi.acquirer.medical.entity.Bill;
import com.asarfi.acquirer.medical.entity.BillItem;
import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.entity.Medicine;
import com.asarfi.acquirer.medical.entity.MedicineStock;
import com.asarfi.acquirer.medical.repository.BillItemRepository;
import com.asarfi.acquirer.medical.repository.BillRepository;
import com.asarfi.acquirer.medical.repository.CompanyRepository;
import com.asarfi.acquirer.medical.repository.MedicineRepository;
import com.asarfi.acquirer.medical.repository.MedicineStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public BillDto createBill(BillDto billDto) {

        Company company = companyRepository.findById(billDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Bill bill = new Bill();
        bill.setCompany(company);
        bill.setCustomerName(billDto.getCustomerName());
        bill.setPaymentMethod(billDto.getPaymentMethod());
        bill.setBillNumber("BILL-" + UUID.randomUUID().toString().substring(0, 8));
        bill.setCreatedAt(LocalDateTime.now());
        bill.setTotalAmount(BigDecimal.ZERO);

        Bill savedBill = billRepository.save(bill);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (BillItemDto itemDto : billDto.getItems()) {

            Medicine medicine = medicineRepository.findById(itemDto.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found"));

            int requiredQuantity = itemDto.getQuantity();

            List<MedicineStock> stocks =
                    medicineStockRepository.findByCompanyAndMedicineOrderByExpiryDateAsc(
                            company,
                            medicine
                    );

            int availableQuantity = stocks.stream()
                    .mapToInt(MedicineStock::getQuantity)
                    .sum();

            if (availableQuantity < requiredQuantity) {
                throw new RuntimeException("Not enough stock for " + medicine.getName());
            }

            int remainingQuantity = requiredQuantity;

            for (MedicineStock stock : stocks) {

                if (remainingQuantity <= 0) {
                    break;
                }

                if (stock.getQuantity() >= remainingQuantity) {

                    stock.setQuantity(stock.getQuantity() - remainingQuantity);
                    medicineStockRepository.save(stock);
                    remainingQuantity = 0;

                } else {

                    remainingQuantity = remainingQuantity - stock.getQuantity();
                    stock.setQuantity(0);
                    medicineStockRepository.save(stock);
                }
            }

            BigDecimal price = medicine.getPrice();

            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(itemDto.getQuantity())
            );

            BillItem billItem = new BillItem();
            billItem.setBill(savedBill);
            billItem.setMedicine(medicine);
            billItem.setQuantity(itemDto.getQuantity());
            billItem.setPrice(price);
            billItem.setSubtotal(subtotal);

            billItemRepository.save(billItem);

            totalAmount = totalAmount.add(subtotal);
        }

        BigDecimal discount = billDto.getDiscount() != null
                ? billDto.getDiscount()
                : BigDecimal.ZERO;

        savedBill.setDiscount(discount);
        savedBill.setTotalAmount(totalAmount.subtract(discount));

        Bill finalBill = billRepository.save(savedBill);

        BillDto response = new BillDto();
        response.setId(finalBill.getId());
        response.setCompanyId(company.getId());
        response.setBillNumber(finalBill.getBillNumber());
        response.setCustomerName(finalBill.getCustomerName());
        response.setPaymentMethod(finalBill.getPaymentMethod());
        response.setDiscount(finalBill.getDiscount());
        response.setTotalAmount(finalBill.getTotalAmount());

        return response;
    }

    public BillDto getBillById(Long billId) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        List<BillItem> billItems = billItemRepository.findByBill(bill);

        List<BillItemDto> itemDtos = billItems.stream().map(item -> {

            BillItemDto itemDto = new BillItemDto();

            itemDto.setMedicineId(item.getMedicine().getId());
            itemDto.setMedicineName(item.getMedicine().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getPrice());
            itemDto.setSubtotal(item.getSubtotal());

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
        response.setItems(itemDtos);

        return response;
    }

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

            return dto;

        }).toList();
    }
}