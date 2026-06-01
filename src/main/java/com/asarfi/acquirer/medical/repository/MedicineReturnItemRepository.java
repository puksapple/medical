package com.asarfi.acquirer.medical.repository;

import com.asarfi.acquirer.medical.entity.MedicineReturn;
import com.asarfi.acquirer.medical.entity.MedicineReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineReturnItemRepository
        extends JpaRepository<MedicineReturnItem, Long> {

    List<MedicineReturnItem> findByMedicineReturn(
            MedicineReturn medicineReturn
    );
}