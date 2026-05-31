package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class BillDto {

    private Long id;

    private Long companyId;

    private String billNumber;

    private String customerName;

    private BigDecimal totalAmount;

    private List<BillItemDto> items;

    private String paymentMethod;

    private BigDecimal discount;

    private Long customerId;


}