package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.BillDto;
import com.asarfi.acquirer.medical.dto.PurchaseDto;
import com.asarfi.acquirer.medical.dto.PurchaseReportDto;
import com.asarfi.acquirer.medical.dto.SalesReportDto;
import com.asarfi.acquirer.medical.entity.Bill;
import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.entity.Purchase;
import com.asarfi.acquirer.medical.repository.BillRepository;
import com.asarfi.acquirer.medical.repository.CompanyRepository;
import com.asarfi.acquirer.medical.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CompanyRepository companyRepository;
    private final BillRepository billRepository;

    private final PurchaseRepository purchaseRepository;

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
}