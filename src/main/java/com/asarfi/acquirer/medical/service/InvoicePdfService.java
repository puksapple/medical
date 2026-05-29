package com.asarfi.acquirer.medical.service;

import com.asarfi.acquirer.medical.entity.Bill;
import com.asarfi.acquirer.medical.entity.BillItem;
import com.asarfi.acquirer.medical.repository.BillItemRepository;
import com.asarfi.acquirer.medical.repository.BillRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoicePdfService {

    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;

    public byte[] generateInvoicePdf(Long billId) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        List<BillItem> items = billItemRepository.findByBill(bill);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);

        document.add(new Paragraph(
                bill.getCompany().getName()
        ));

        document.add(new Paragraph(
                bill.getCompany().getAddress()
        ));

        document.add(new Paragraph(
                bill.getCompany().getPhone()
        ));

        document.add(new Paragraph(
                bill.getCompany().getEmail()
        ));

        document.add(new Paragraph(" "));

        document.open();

        try {
            document.add(new Paragraph("MEDICAL INVOICE"));
            document.add(new Paragraph("Bill No: " + bill.getBillNumber()));
            document.add(new Paragraph("Customer: " + bill.getCustomerName()));
            document.add(new Paragraph("Payment: " + bill.getPaymentMethod()));
            document.add(new Paragraph(" "));

            Table table = new Table(5);
            table.addCell("Medicine");
            table.addCell("Qty");
            table.addCell("Price");
            table.addCell("Subtotal");
            table.addCell(" ");

            for (BillItem item : items) {
                table.addCell(item.getMedicine().getName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.valueOf(item.getPrice()));
                table.addCell(String.valueOf(item.getSubtotal()));
                table.addCell("");
            }

            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Discount: " + bill.getDiscount()));
            document.add(new Paragraph("Total: " + bill.getTotalAmount()));

        } catch (DocumentException e) {
            throw new RuntimeException("PDF generation failed");
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }
}