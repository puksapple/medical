package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MedicineDto {

    private Long id;

    private Long companyId;

    private String name;

    private String genericName;

    private BigDecimal price;

    private Boolean active;
}