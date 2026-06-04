package com.asarfi.acquirer.medical.entity;

import com.asarfi.acquirer.medical.entity.enums.MedicineUnit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "medicines")
@Getter
@Setter
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "generic_name", length = 255)
    private String genericName;

    @Column(name = "individual_unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal individualUnitPrice;

    @Column(name = "pack_unit_price", precision = 10, scale = 2)
    private BigDecimal packUnitPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "base_unit", length = 50)
    private MedicineUnit baseUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "pack_unit", length = 50)
    private MedicineUnit packUnit;

    @Column(name = "units_per_pack")
    private Integer unitsPerPack;

    @Column(nullable = false)
    private Boolean active = true;
}