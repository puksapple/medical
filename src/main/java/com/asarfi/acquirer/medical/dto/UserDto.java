package com.asarfi.acquirer.medical.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long id;

    private String fullName;

    private String email;

    private String password;

    private Boolean active;

    private String roleName;

    private String token;


    private Long companyId;

    private String companyName;

    private String companyEmail;

    private String companyPhone;

    private String companyAddress;
}