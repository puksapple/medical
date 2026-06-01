package com.asarfi.acquirer.medical.repository;

import com.asarfi.acquirer.medical.entity.MedicineReturn;
import com.asarfi.acquirer.medical.entity.MedicineReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicineReturnItemRepository
        extends JpaRepository<MedicineReturnItem, Long> {

    List<MedicineReturnItem> findByMedicineReturn(
            MedicineReturn medicineReturn
    );

    @Query("""
    SELECT COALESCE(SUM(mri.quantity), 0)
    FROM MedicineReturnItem mri
    WHERE mri.billItem.id = :billItemId
""")
    Integer getTotalReturnedQuantityByBillItemId(
            @Param("billItemId") Long billItemId
    );
}