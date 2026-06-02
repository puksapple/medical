package com.asarfi.acquirer.medical.repository;

import com.asarfi.acquirer.medical.entity.BillItem;
import com.asarfi.acquirer.medical.entity.BillItemStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillItemStockRepository
        extends JpaRepository<BillItemStock, Long> {

    List<BillItemStock> findByBillItem(BillItem billItem);
}