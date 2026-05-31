package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierDto {

    private Long id;

    private Long companyId;

    private String name;

    private String phone;

    private String email;

    private String address;

    private Boolean active;
}