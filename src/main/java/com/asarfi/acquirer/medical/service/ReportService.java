package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.*;
import com.asarfi.acquirer.medical.entity.*;
import com.asarfi.acquirer.medical.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CompanyRepository companyRepository;
    private final BillRepository billRepository;

    private final PurchaseRepository purchaseRepository;
    private final MedicineStockRepository medicineStockRepository;
    private final MedicineRepository medicineRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;

    private final BillItemRepository billItemRepository;

    private final PurchaseItemRepository purchaseItemRepository;

    public SalesReportDto getSalesReport(
            Long companyId,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(23, 59, 59);

        List<Bill> bills = billRepository.findBillsByCompanyAndDateRange(
                company,
                fromDateTime,
                toDateTime
        );

        BigDecimal totalSales = bills.stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BillDto> billDtos = bills.stream().map(bill -> {

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

        SalesReportDto response = new SalesReportDto();
        response.setTotalSales(totalSales);
        response.setBills(billDtos);

        return response;
    }


    public PurchaseReportDto getPurchaseReport(
            Long companyId,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(23, 59, 59);

        List<Purchase> purchases =
                purchaseRepository.findPurchasesByCompanyAndDateRange(
                        company,
                        fromDateTime,
                        toDateTime
                );

        BigDecimal totalPurchaseAmount = purchases.stream()
                .map(Purchase::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PurchaseDto> purchaseDtos = purchases.stream().map(purchase -> {

            PurchaseDto dto = new PurchaseDto();

            dto.setId(purchase.getId());
            dto.setCompanyId(company.getId());

            if (purchase.getSupplier() != null) {
                dto.setSupplierId(purchase.getSupplier().getId());
                dto.setSupplierName(purchase.getSupplier().getName());
            } else {
                dto.setSupplierName(purchase.getSupplierName());
            }

            dto.setInvoiceNumber(purchase.getInvoiceNumber());
            dto.setTotalAmount(purchase.getTotalAmount());

            return dto;

        }).toList();

        PurchaseReportDto response = new PurchaseReportDto();
        response.setTotalPurchaseAmount(totalPurchaseAmount);
        response.setTotalPurchases(purchases.size());
        response.setPurchases(purchaseDtos);

        return response;
    }

    public List<MedicineStockDto> getStockReport(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<MedicineStock> stocks =
                medicineStockRepository.findByCompany(company);

        return stocks.stream().map(stock -> {

            MedicineStockDto dto = new MedicineStockDto();

            dto.setId(stock.getId());
            dto.setCompanyId(company.getId());
            dto.setMedicineId(stock.getMedicine().getId());
            dto.setMedicineName(stock.getMedicine().getName());
            dto.setQuantity(stock.getQuantity());
            dto.setBatchNo(stock.getBatchNo());
            dto.setExpiryDate(stock.getExpiryDate());

            dto.setPurchaseId(
                    stock.getPurchase() != null
                            ? stock.getPurchase().getId()
                            : null
            );

            dto.setSupplierName(
                    stock.getPurchase() != null
                            ? stock.getPurchase().getSupplierName()
                            : null
            );

            return dto;

        }).toList();
    }



    public List<StockAdjustmentDto> getStockAdjustmentReport(
            Long companyId
    ) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<StockAdjustment> adjustments =
                stockAdjustmentRepository
                        .findByCompanyOrderByCreatedAtDesc(company);

        return adjustments.stream().map(adjustment -> {

            StockAdjustmentDto dto = new StockAdjustmentDto();

            dto.setId(adjustment.getId());
            dto.setCompanyId(company.getId());
            dto.setMedicineId(adjustment.getMedicine().getId());
            dto.setMedicineName(adjustment.getMedicine().getName());
            dto.setQuantity(adjustment.getQuantity());
            dto.setAdjustmentType(adjustment.getAdjustmentType());
            dto.setReason(adjustment.getReason());

            return dto;

        }).toList();
    }


    public ProfitReportDto getProfitReport(
            Long companyId,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(23, 59, 59);

        List<Bill> bills = billRepository.findBillsByCompanyAndDateRange(
                company,
                fromDateTime,
                toDateTime
        );

        List<BillItem> billItems = billItemRepository.findByBillIn(bills);

        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        Map<Long, ProfitMedicineDto> medicineProfitMap = new HashMap<>();

        for (BillItem billItem : billItems) {

            Medicine medicine = billItem.getMedicine();

            Integer quantitySold = billItem.getQuantity();

            BigDecimal salesAmount = billItem.getSubtotal();

            List<PurchaseItem> purchaseItems =
                    purchaseItemRepository.findByMedicine(medicine);

            BigDecimal purchasePrice = purchaseItems.isEmpty()
                    ? BigDecimal.ZERO
                    : purchaseItems.get(0).getPurchasePrice();

            BigDecimal costAmount = purchasePrice.multiply(
                    BigDecimal.valueOf(quantitySold)
            );

            BigDecimal profitAmount = salesAmount.subtract(costAmount);

            totalSales = totalSales.add(salesAmount);
            totalCost = totalCost.add(costAmount);

            ProfitMedicineDto dto = medicineProfitMap.getOrDefault(
                    medicine.getId(),
                    new ProfitMedicineDto()
            );

            if (dto.getMedicineId() == null) {
                dto.setMedicineId(medicine.getId());
                dto.setMedicineName(medicine.getName());
                dto.setQuantitySold(0);
                dto.setSalesAmount(BigDecimal.ZERO);
                dto.setCostAmount(BigDecimal.ZERO);
                dto.setProfitAmount(BigDecimal.ZERO);
            }

            dto.setQuantitySold(dto.getQuantitySold() + quantitySold);
            dto.setSalesAmount(dto.getSalesAmount().add(salesAmount));
            dto.setCostAmount(dto.getCostAmount().add(costAmount));
            dto.setProfitAmount(dto.getProfitAmount().add(profitAmount));

            medicineProfitMap.put(medicine.getId(), dto);
        }

        BigDecimal grossProfit = totalSales.subtract(totalCost);

        BigDecimal margin = BigDecimal.ZERO;

        if (totalSales.compareTo(BigDecimal.ZERO) > 0) {
            margin = grossProfit
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalSales, 2, RoundingMode.HALF_UP);
        }

        ProfitReportDto response = new ProfitReportDto();

        response.setTotalSales(totalSales);
        response.setTotalCost(totalCost);
        response.setGrossProfit(grossProfit);
        response.setProfitMarginPercentage(margin);
        response.setMedicines(
                new ArrayList<>(medicineProfitMap.values())
        );

        return response;
    }



}