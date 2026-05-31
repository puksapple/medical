package com.asarfi.acquirer.medical.repository;

import com.asarfi.acquirer.medical.entity.Medicine;
import com.asarfi.acquirer.medical.entity.Purchase;
import com.asarfi.acquirer.medical.entity.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    List<PurchaseItem> findByPurchase(Purchase purchase);
    List<PurchaseItem> findByMedicine(Medicine medicine);
}