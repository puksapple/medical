package com.asarfi.acquirer.medical.repository;

import com.asarfi.acquirer.medical.entity.Bill;
import com.asarfi.acquirer.medical.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillItemRepository extends JpaRepository<BillItem, Long> {

    List<BillItem> findByBill(Bill bill);

    List<BillItem> findByBillIn(List<Bill> bills);
}