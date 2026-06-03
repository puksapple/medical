package com.asarfi.acquirer.medical.entity;

import com.asarfi.acquirer.medical.entity.enums.MedicineUnit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter

public class MedicineReturnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "medicine_return_id")
    private MedicineReturn medicineReturn;

    @ManyToOne
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @ManyToOne
    @JoinColumn(name = "bill_item_id")
    private BillItem billItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "return_unit", length = 50)
    private MedicineUnit returnUnit;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;
}