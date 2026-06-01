package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.MedicineReturnDto;
import com.asarfi.acquirer.medical.dto.MedicineReturnItemDto;
import com.asarfi.acquirer.medical.entity.*;
import com.asarfi.acquirer.medical.entity.enums.ReturnType;
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
public class MedicineReturnService {

    private final MedicineReturnRepository medicineReturnRepository;
    private final MedicineReturnItemRepository medicineReturnItemRepository;
    private final CompanyRepository companyRepository;
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final MedicineStockRepository medicineStockRepository;

    @Transactional
    public MedicineReturnDto createReturn(MedicineReturnDto dto) {

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Bill bill = billRepository.findById(dto.getBillId())
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        MedicineReturn medicineReturn = new MedicineReturn();
        medicineReturn.setCompany(company);
        medicineReturn.setBill(bill);
        medicineReturn.setReturnNumber("RET-" + UUID.randomUUID().toString().substring(0, 8));
        medicineReturn.setReturnType(ReturnType.CUSTOMER);
        medicineReturn.setReason(dto.getReason());
        medicineReturn.setCreatedAt(LocalDateTime.now());
        medicineReturn.setTotalAmount(BigDecimal.ZERO);

        MedicineReturn savedReturn = medicineReturnRepository.save(medicineReturn);

        BigDecimal totalReturnAmount = BigDecimal.ZERO;

        for (MedicineReturnItemDto itemDto : dto.getItems()) {

            BillItem billItem = billItemRepository.findById(itemDto.getBillItemId())
                    .orElseThrow(() -> new RuntimeException("Bill item not found"));

            Integer alreadyReturned =
                    medicineReturnItemRepository.getTotalReturnedQuantityByBillItemId(
                            billItem.getId()
                    );

            int remainingReturnableQuantity =
                    billItem.getQuantity() - alreadyReturned;

            if (itemDto.getQuantity() > remainingReturnableQuantity) {
                throw new RuntimeException(
                        "Return quantity cannot be greater than remaining returnable quantity"
                );
            }

            BigDecimal price = billItem.getPrice();

            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(itemDto.getQuantity())
            );

            MedicineReturnItem returnItem = new MedicineReturnItem();
            returnItem.setMedicineReturn(savedReturn);
            returnItem.setBillItem(billItem);
            returnItem.setMedicine(billItem.getMedicine());
            returnItem.setQuantity(itemDto.getQuantity());
            returnItem.setPrice(price);
            returnItem.setSubtotal(subtotal);

            medicineReturnItemRepository.save(returnItem);

            MedicineStock stock = new MedicineStock();
            stock.setCompany(company);
            stock.setMedicine(billItem.getMedicine());
            stock.setQuantity(itemDto.getQuantity());
            stock.setCreatedAt(LocalDateTime.now());

            medicineStockRepository.save(stock);

            totalReturnAmount = totalReturnAmount.add(subtotal);
        }

        savedReturn.setTotalAmount(totalReturnAmount);

        MedicineReturn finalReturn = medicineReturnRepository.save(savedReturn);
        BigDecimal currentReturnAmount = bill.getReturnAmount() != null
                ? bill.getReturnAmount()
                : BigDecimal.ZERO;

        bill.setReturnAmount(
                currentReturnAmount.add(totalReturnAmount)
        );

        bill.setNetAmount(
                bill.getTotalAmount().subtract(
                        bill.getReturnAmount()
                )
        );

        billRepository.save(bill);


        MedicineReturnDto response = new MedicineReturnDto();
        response.setId(finalReturn.getId());
        response.setCompanyId(company.getId());
        response.setBillId(bill.getId());
        response.setReturnNumber(finalReturn.getReturnNumber());
        response.setReturnType(finalReturn.getReturnType());
        response.setReason(finalReturn.getReason());
        response.setTotalAmount(finalReturn.getTotalAmount());

        return response;
    }


    @Transactional
    public List<MedicineReturnDto> getReturnsByCompany(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<MedicineReturn> returns =
                medicineReturnRepository.findByCompanyOrderByCreatedAtDesc(company);

        return returns.stream().map(medicineReturn -> {

            MedicineReturnDto dto = new MedicineReturnDto();
            dto.setId(medicineReturn.getId());
            dto.setCompanyId(company.getId());
            dto.setBillId(medicineReturn.getBill().getId());
            dto.setReturnNumber(medicineReturn.getReturnNumber());
            dto.setReturnType(medicineReturn.getReturnType());
            dto.setReason(medicineReturn.getReason());
            dto.setTotalAmount(medicineReturn.getTotalAmount());

            return dto;

        }).toList();
    }


    @Transactional
    public MedicineReturnDto getReturnById(Long returnId) {

        MedicineReturn medicineReturn = medicineReturnRepository.findById(returnId)
                .orElseThrow(() -> new RuntimeException("Return not found"));

        List<MedicineReturnItem> returnItems =
                medicineReturnItemRepository.findByMedicineReturn(medicineReturn);

        List<MedicineReturnItemDto> itemDtos = returnItems.stream().map(item -> {

            MedicineReturnItemDto dto = new MedicineReturnItemDto();

            dto.setBillItemId(item.getBillItem().getId());
            dto.setMedicineId(item.getMedicine().getId());
            dto.setMedicineName(item.getMedicine().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            dto.setSubtotal(item.getSubtotal());

            return dto;

        }).toList();

        MedicineReturnDto response = new MedicineReturnDto();

        response.setId(medicineReturn.getId());
        response.setCompanyId(medicineReturn.getCompany().getId());
        response.setBillId(medicineReturn.getBill().getId());
        response.setReturnNumber(medicineReturn.getReturnNumber());
        response.setReturnType(medicineReturn.getReturnType());
        response.setReason(medicineReturn.getReason());
        response.setTotalAmount(medicineReturn.getTotalAmount());
        response.setItems(itemDtos);

        return response;
    }
}