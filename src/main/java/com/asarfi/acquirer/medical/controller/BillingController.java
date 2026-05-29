package com.asarfi.acquirer.medical.controller;

import com.asarfi.acquirer.medical.dto.BillDto;
import com.asarfi.acquirer.medical.service.BillingService;
import com.asarfi.acquirer.medical.service.InvoicePdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;
    private final InvoicePdfService invoicePdfService;

    @PostMapping
    public BillDto createBill(
            @RequestBody BillDto billDto
    ) {
        return billingService.createBill(billDto);
    }

    @GetMapping("/{billId}")
    public BillDto getBillById(@PathVariable Long billId) {
        return billingService.getBillById(billId);
    }


    @GetMapping("/company/{companyId}")
    public List<BillDto> getBillsByCompany(
            @PathVariable Long companyId
    ) {
        return billingService.getBillsByCompany(companyId);
    }

    @GetMapping("/{billId}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long billId) {

        byte[] pdf = invoicePdfService.generateInvoicePdf(billId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}