package com.asarfi.acquirer.medical.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bill_item_stocks")

public class BillItemStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantityUsed;

    @ManyToOne
    @JoinColumn(name = "bill_item_id")
    private BillItem billItem;

    @ManyToOne
    @JoinColumn(name = "medicine_stock_id")
    private MedicineStock medicineStock;
}