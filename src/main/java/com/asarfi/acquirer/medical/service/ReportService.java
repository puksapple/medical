package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.BillDto;
import com.asarfi.acquirer.medical.dto.SalesReportDto;
import com.asarfi.acquirer.medical.entity.Bill;
import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.repository.BillRepository;
import com.asarfi.acquirer.medical.repository.CompanyRepository;
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
            dto.setTotalAmount(bill.getTotalAmount());
            return dto;
        }).toList();

        SalesReportDto response = new SalesReportDto();
        response.setTotalSales(totalSales);
        response.setBills(billDtos);

        return response;
    }
}