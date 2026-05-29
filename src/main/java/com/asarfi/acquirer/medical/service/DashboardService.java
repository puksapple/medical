package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.BillDto;
import com.asarfi.acquirer.medical.dto.DashboardDto;
import com.asarfi.acquirer.medical.entity.Bill;
import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.repository.BillRepository;
import com.asarfi.acquirer.medical.repository.CompanyRepository;
import com.asarfi.acquirer.medical.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CompanyRepository companyRepository;
    private final MedicineRepository medicineRepository;
    private final BillRepository billRepository;

    public DashboardDto getDashboard(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        DashboardDto dashboardDto = new DashboardDto();

        dashboardDto.setTotalMedicines(
                medicineRepository.countByCompanyAndActiveTrue(company)
        );

        dashboardDto.setTotalBills(
                billRepository.countByCompany(company)
        );

        dashboardDto.setTotalSales(
                billRepository.getTotalSalesByCompany(company)
        );

        dashboardDto.setTodaySales(
                billRepository.getTodaySalesByCompany(company)
        );

        return dashboardDto;
    }

    public List<BillDto> getRecentBills(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<Bill> bills =
                billRepository.findTop5ByCompanyOrderByCreatedAtDesc(company);

        return bills.stream().map(bill -> {

            BillDto dto = new BillDto();

            dto.setId(bill.getId());
            dto.setCompanyId(company.getId());
            dto.setBillNumber(bill.getBillNumber());
            dto.setCustomerName(bill.getCustomerName());
            dto.setTotalAmount(bill.getTotalAmount());

            return dto;

        }).toList();
    }
}