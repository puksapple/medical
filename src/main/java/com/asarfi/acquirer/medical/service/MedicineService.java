package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.dto.MedicineDto;
import com.asarfi.acquirer.medical.entity.Company;
import com.asarfi.acquirer.medical.entity.Medicine;
import com.asarfi.acquirer.medical.repository.CompanyRepository;
import com.asarfi.acquirer.medical.repository.MedicineRepository;
import com.asarfi.acquirer.medical.repository.MedicineStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final CompanyRepository companyRepository;
    private final MedicineStockRepository medicineStockRepository;

    public MedicineDto createMedicine(MedicineDto medicineDto) {

        Company company = companyRepository.findById(medicineDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Medicine medicine = new Medicine();

        medicine.setCompany(company);
        medicine.setName(medicineDto.getName());
        medicine.setGenericName(medicineDto.getGenericName());
        medicine.setPrice(medicineDto.getPrice());
        medicine.setActive(true);

        Medicine savedMedicine = medicineRepository.save(medicine);

        return mapToDto(savedMedicine);
    }

    public List<MedicineDto> searchMedicines(
            Long companyId,
            String keyword
    ) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        List<Medicine> medicines =
                medicineRepository.findByCompanyAndActiveTrueAndNameContainingIgnoreCase(
                        company,
                        keyword
                );

        return medicines.stream()
                .map(this::mapToDto)
                .toList();
    }

    public MedicineDto updateMedicine(
            Long medicineId,
            MedicineDto medicineDto
    ) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        medicine.setName(medicineDto.getName());
        medicine.setGenericName(medicineDto.getGenericName());
        medicine.setPrice(medicineDto.getPrice());

        Medicine updatedMedicine = medicineRepository.save(medicine);

        return mapToDto(updatedMedicine);
    }

    public String deleteMedicine(Long medicineId) {

        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        Integer totalStock =
                medicineStockRepository.getTotalStockByMedicineId(medicineId);

        if (totalStock != null && totalStock > 0) {
            throw new RuntimeException(
                    "Medicine has remaining stock. Please sell or adjust stock to 0 before deactivating."
            );
        }

        medicine.setActive(false);

        medicineRepository.save(medicine);

        return "Medicine deactivated successfully";
    }

    private MedicineDto mapToDto(Medicine medicine) {

        MedicineDto dto = new MedicineDto();

        dto.setId(medicine.getId());
        dto.setCompanyId(medicine.getCompany().getId());
        dto.setName(medicine.getName());
        dto.setGenericName(medicine.getGenericName());
        dto.setPrice(medicine.getPrice());
        dto.setActive(medicine.getActive());

        return dto;
    }
}