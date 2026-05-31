package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CustomerDto {

    private Long id;

    private Long companyId;

    private String name;

    private String phone;

    private String address;

    private BigDecimal discountPercentage;

    private Boolean active;
}