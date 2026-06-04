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
    private final MedicineReturnRepository medicineReturnRepository;
    private final MedicineReturnItemRepository medicineReturnItemRepository;

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

        BigDecimal grossSales = bills.stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal salesReturnAmount = bills.stream()
                .map(bill -> bill.getReturnAmount() != null
                        ? bill.getReturnAmount()
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netSales = grossSales.subtract(salesReturnAmount);

        List<BillDto> billDtos = bills.stream().map(bill -> {

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

        SalesReportDto response = new SalesReportDto();

        response.setGrossSales(grossSales);
        response.setSalesReturnAmount(salesReturnAmount);
        response.setNetSales(netSales);

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

        BigDecimal grossSales = BigDecimal.ZERO;
        BigDecimal salesReturnAmount = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        Map<Long, ProfitMedicineDto> medicineProfitMap = new HashMap<>();

        for (BillItem billItem : billItems) {

            Medicine medicine = billItem.getMedicine();

            int soldStockQuantity = billItem.getStockQuantity() != null
                    ? billItem.getStockQuantity()
                    : billItem.getQuantity();

            BigDecimal salesAmount = billItem.getSubtotal();

            BigDecimal costPerBaseUnit = getCostPerBaseUnit(medicine);

            BigDecimal costAmount = costPerBaseUnit.multiply(
                    BigDecimal.valueOf(soldStockQuantity)
            );

            BigDecimal profitAmount = salesAmount.subtract(costAmount);

            grossSales = grossSales.add(salesAmount);
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

            dto.setQuantitySold(dto.getQuantitySold() + soldStockQuantity);
            dto.setSalesAmount(dto.getSalesAmount().add(salesAmount));
            dto.setCostAmount(dto.getCostAmount().add(costAmount));
            dto.setProfitAmount(dto.getProfitAmount().add(profitAmount));

            medicineProfitMap.put(medicine.getId(), dto);
        }

        salesReturnAmount = bills.stream()
                .map(bill -> bill.getReturnAmount() != null
                        ? bill.getReturnAmount()
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal returnedCost = BigDecimal.ZERO;

        List<MedicineReturn> returns =
                medicineReturnRepository.findByCompanyAndCreatedAtBetween(
                        company,
                        fromDateTime,
                        toDateTime
                );

        for (MedicineReturn returnRecord : returns) {

            List<MedicineReturnItem> returnItems =
                    medicineReturnItemRepository.findByMedicineReturn(returnRecord);

            for (MedicineReturnItem returnItem : returnItems) {

                Medicine medicine = returnItem.getMedicine();

                int returnedStockQuantity =
                        returnItem.getStockQuantity() != null
                                ? returnItem.getStockQuantity()
                                : returnItem.getQuantity();

                BigDecimal returnCostAmount =
                        getCostPerBaseUnit(medicine).multiply(
                                BigDecimal.valueOf(returnedStockQuantity)
                        );

                returnedCost = returnedCost.add(returnCostAmount);

                ProfitMedicineDto dto = medicineProfitMap.get(medicine.getId());

                if (dto != null) {
                    dto.setQuantitySold(dto.getQuantitySold() - returnedStockQuantity);
                    dto.setSalesAmount(dto.getSalesAmount().subtract(returnItem.getSubtotal()));
                    dto.setCostAmount(dto.getCostAmount().subtract(returnCostAmount));
                    dto.setProfitAmount(
                            dto.getSalesAmount().subtract(dto.getCostAmount())
                    );
                }
            }
        }

        BigDecimal netSales = grossSales.subtract(salesReturnAmount);
        BigDecimal netCost = totalCost.subtract(returnedCost);
        BigDecimal grossProfit = netSales.subtract(netCost);

        BigDecimal margin = BigDecimal.ZERO;

        if (netSales.compareTo(BigDecimal.ZERO) > 0) {
            margin = grossProfit
                    .multiply(BigDecimal.valueOf(100))
                    .divide(netSales, 2, RoundingMode.HALF_UP);
        }

        ProfitReportDto response = new ProfitReportDto();

        response.setGrossSales(grossSales);
        response.setSalesReturnAmount(salesReturnAmount);
        response.setNetSales(netSales);
        response.setTotalCost(netCost);
        response.setGrossProfit(grossProfit);
        response.setProfitMarginPercentage(margin);
        response.setMedicines(
                new ArrayList<>(medicineProfitMap.values())
        );

        return response;
    }


    public SalesReturnReportDto getSalesReturnReport(
            Long companyId,
            LocalDate fromDate,
            LocalDate toDate
    ) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(23, 59, 59);

        List<MedicineReturn> returns =
                medicineReturnRepository.findByCompanyAndCreatedAtBetween(
                        company,
                        fromDateTime,
                        toDateTime
                );

        BigDecimal totalReturnAmount = returns.stream()
                .map(MedicineReturn::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MedicineReturnDto> returnDtos = returns.stream().map(returnRecord -> {

            MedicineReturnDto dto = new MedicineReturnDto();

            dto.setId(returnRecord.getId());
            dto.setCompanyId(company.getId());
            dto.setBillId(returnRecord.getBill().getId());
            dto.setReturnNumber(returnRecord.getReturnNumber());
            dto.setReturnType(returnRecord.getReturnType());
            dto.setReason(returnRecord.getReason());
            dto.setTotalAmount(returnRecord.getTotalAmount());

            return dto;

        }).toList();

        SalesReturnReportDto response = new SalesReturnReportDto();
        response.setTotalReturnAmount(totalReturnAmount);
        response.setTotalReturns(returns.size());
        response.setReturns(returnDtos);

        return response;
    }
    private BigDecimal getCostPerBaseUnit(Medicine medicine) {

        List<PurchaseItem> purchaseItems =
                purchaseItemRepository.findByMedicine(medicine);

        if (purchaseItems.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal purchasePrice = purchaseItems.get(0).getPurchasePrice();

        int unitsPerPack = medicine.getUnitsPerPack() != null
                && medicine.getUnitsPerPack() > 0
                ? medicine.getUnitsPerPack()
                : 1;

        return purchasePrice.divide(
                BigDecimal.valueOf(unitsPerPack),
                4,
                RoundingMode.HALF_UP
        );
    }


}