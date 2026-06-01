package com.asarfi.acquirer.medical.dto;

import com.asarfi.acquirer.medical.entity.enums.ReturnType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class MedicineReturnDto {

    private Long id;

    private Long companyId;

    private Long billId;

    private String returnNumber;

    private ReturnType returnType;

    private String reason;

    private BigDecimal totalAmount;

    private List<MedicineReturnItemDto> items;
}