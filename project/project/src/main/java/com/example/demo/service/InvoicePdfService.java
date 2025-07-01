package com.example.demo.service;

import com.example.demo.model.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InvoicePdfService {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceItemRepo invoiceItemRepo;

    // Company Information
    private static final String COMPANY_NAME = "Your Company Name";
    private static final String COMPANY_ADDRESS = "123 Business Street, Coimbatore - 641001";
    private static final String COMPANY_EMAIL = "sales@yourcompany.com";
    private static final String COMPANY_PHONE = "+91 9876543210";
    private static final String COMPANY_WEBSITE = "www.yourcompany.com";
    private static final String COMPANY_GSTIN = "22AAAAA0000A1Z5";

    public byte[] generateInvoicePdf(Long invoiceId) throws IOException {
        // Validate invoiceId
        if (invoiceId == null || invoiceId <= 0) {
            throw new IllegalArgumentException("Invalid invoice ID");
        }

        // Fetch invoice data
        Invoice invoice = invoiceService.getInvoiceById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        // Fetch customer data
        Customer customer = invoice.getCustomer();
        if (customer == null) {
            throw new RuntimeException("Customer not found for invoice ID: " + invoiceId);
        }

        // Fetch invoice items
        List<InvoiceItem> invoiceItems = invoiceItemRepo.findByInvoiceId(invoiceId);

        // Create PDF document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            float yPosition = 750;
            float margin = 50;
            float pageWidth = page.getMediaBox().getWidth() - 2 * margin;

            // Add company header
            yPosition = addCompanyHeader(contentStream, yPosition, margin, pageWidth);

            // Add invoice title
            yPosition = addInvoiceTitle(contentStream, yPosition, margin, pageWidth);

            // Add invoice details
            yPosition = addInvoiceDetails(contentStream, yPosition, margin, pageWidth, invoice);

            // Add customer details
            yPosition = addCustomerDetails(contentStream, yPosition, margin, pageWidth, customer);

            // Add items table
            yPosition = addItemsTable(contentStream, yPosition, margin, pageWidth, invoiceItems);

            // Add totals
            addInvoiceTotals(contentStream, yPosition - 50, margin, pageWidth, invoice);

            // Add footer
            addInvoiceFooter(contentStream, 100, margin, pageWidth);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();

        return baos.toByteArray();
    }

    private float addCompanyHeader(PDPageContentStream contentStream, float yPosition, float margin, float pageWidth) throws IOException {
        // Company name
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText(COMPANY_NAME);
        contentStream.endText();

        yPosition -= 25;

        // Company details
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText(COMPANY_ADDRESS);
        contentStream.endText();

        yPosition -= 15;

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("GSTIN: " + COMPANY_GSTIN + " | Email: " + COMPANY_EMAIL);
        contentStream.endText();

        yPosition -= 15;

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Phone: " + COMPANY_PHONE + " | Website: " + COMPANY_WEBSITE);
        contentStream.endText();

        return yPosition - 30;
    }

    private float addInvoiceTitle(PDPageContentStream contentStream, float yPosition, float margin, float pageWidth) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
        contentStream.newLineAtOffset(margin + pageWidth/2 - 40, yPosition);
        contentStream.showText("INVOICE");
        contentStream.endText();

        return yPosition - 30;
    }

    private float addInvoiceDetails(PDPageContentStream contentStream, float yPosition, float margin, float pageWidth, Invoice invoice) throws IOException {
        // Invoice number
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Invoice No: ");
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.showText(invoice.getInvoiceno());
        contentStream.endText();

        // Invoice date
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(margin + 300, yPosition);
        contentStream.showText("Date: ");
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.showText(invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        contentStream.endText();

        yPosition -= 20;

        // Due date
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Due Date: ");
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.showText(invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        contentStream.endText();

        // Status
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(margin + 300, yPosition);
        contentStream.showText("Status: ");
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.showText(invoice.getStatus().toString());
        contentStream.endText();

        return yPosition - 30;
    }

    private float addCustomerDetails(PDPageContentStream contentStream, float yPosition, float margin, float pageWidth, Customer customer) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Bill To:");
        contentStream.endText();

        yPosition -= 20;

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText(customer.getCustomername());
        contentStream.endText();

        yPosition -= 15;

        if (customer.getCompanyname() != null && !customer.getCompanyname().isEmpty()) {
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText(customer.getCompanyname());
            contentStream.endText();
            yPosition -= 15;
        }

        if (customer.getAddress() != null && !customer.getAddress().isEmpty()) {
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText(customer.getAddress());
            contentStream.endText();
            yPosition -= 15;
        }

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Email: " + customer.getEmailid());
        contentStream.endText();

        yPosition -= 15;

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Phone: " + customer.getMobilenumber());
        contentStream.endText();

        return yPosition - 30;
    }

    private float addItemsTable(PDPageContentStream contentStream, float yPosition, float margin, float pageWidth, List<InvoiceItem> invoiceItems) throws IOException {
        float tableTop = yPosition;
        float tableHeight = 20;
        float rowHeight = 20;

        // Table headers
        String[] headers = {"#", "Description", "License", "Qty", "Unit Price", "Discount", "Tax", "Amount"};
        float[] columnWidths = {30, 150, 70, 40, 70, 60, 50, 70};

        // Draw table header
        contentStream.setLineWidth(1f);
        float currentX = margin;
        
        // Header background (light gray)
        contentStream.setNonStrokingColor(0.9f, 0.9f, 0.9f);
        contentStream.addRect(margin, tableTop - tableHeight, pageWidth, tableHeight);
        contentStream.fill();
        contentStream.setNonStrokingColor(0f, 0f, 0f);

        // Header text
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9);
        for (int i = 0; i < headers.length; i++) {
            contentStream.newLineAtOffset(currentX + 5, tableTop - 15);
            contentStream.showText(headers[i]);
            contentStream.newLineAtOffset(-(currentX + 5), -(tableTop - 15));
            currentX += columnWidths[i];
        }
        contentStream.endText();

        // Draw header borders
        currentX = margin;
        for (int i = 0; i <= headers.length; i++) {
            contentStream.moveTo(currentX, tableTop);
            contentStream.lineTo(currentX, tableTop - tableHeight);
            contentStream.stroke();
            if (i < headers.length) {
                currentX += columnWidths[i];
            }
        }

        // Top and bottom borders for header
        contentStream.moveTo(margin, tableTop);
        contentStream.lineTo(margin + pageWidth, tableTop);
        contentStream.stroke();
        contentStream.moveTo(margin, tableTop - tableHeight);
        contentStream.lineTo(margin + pageWidth, tableTop - tableHeight);
        contentStream.stroke();

        float currentY = tableTop - tableHeight;
        int rowNum = 1;

        // Add data rows
        for (InvoiceItem invoiceItem : invoiceItems) {
            Item item = invoiceItem.getItem();
            String itemName = item != null ? item.getItemname() : "Unknown Item";
            String licenseType = invoiceItem.getLicenseType() != null ? invoiceItem.getLicenseType() : "Standard";
            BigDecimal unitPrice = invoiceItem.getUnitPrice() != null ? invoiceItem.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal discountPercentage = invoiceItem.getDiscountPercentage() != null ? invoiceItem.getDiscountPercentage() : BigDecimal.ZERO;
            BigDecimal taxPercentage = invoiceItem.getTaxPercentage() != null ? invoiceItem.getTaxPercentage() : BigDecimal.ZERO;
            BigDecimal itemTotal = invoiceItem.getTotalPrice() != null ? invoiceItem.getTotalPrice() : BigDecimal.ZERO;

            currentY -= rowHeight;
            currentX = margin;

            // Row data
            String[] rowData = {
                String.valueOf(rowNum++),
                itemName,
                licenseType,
                String.valueOf(invoiceItem.getQuantity()),
                String.format("₹%.2f", unitPrice),
                String.format("%.1f%%", discountPercentage),
                String.format("%.1f%%", taxPercentage),
                String.format("₹%.2f", itemTotal)
            };

            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);
            for (int i = 0; i < rowData.length; i++) {
                contentStream.newLineAtOffset(currentX + 5, currentY + 5);
                contentStream.showText(rowData[i]);
                contentStream.newLineAtOffset(-(currentX + 5), -(currentY + 5));
                currentX += columnWidths[i];
            }
            contentStream.endText();

            // Draw row borders
            currentX = margin;
            for (int i = 0; i <= headers.length; i++) {
                contentStream.moveTo(currentX, currentY);
                contentStream.lineTo(currentX, currentY + rowHeight);
                contentStream.stroke();
                if (i < headers.length) {
                    currentX += columnWidths[i];
                }
            }

            // Bottom border for row
            contentStream.moveTo(margin, currentY);
            contentStream.lineTo(margin + pageWidth, currentY);
            contentStream.stroke();
        }

        return currentY - 20;
    }

    private void addInvoiceTotals(PDPageContentStream contentStream, float yPosition, float margin, float pageWidth, Invoice invoice) throws IOException {
        float totalBoxWidth = 250;
        float totalBoxX = margin + pageWidth - totalBoxWidth;
        float lineHeight = 25;

        // Subtotal
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.newLineAtOffset(totalBoxX, yPosition);
        contentStream.showText("Subtotal:");
        contentStream.newLineAtOffset(150, 0);
        contentStream.showText("₹" + String.format("%.2f", invoice.getSubtotal()));
        contentStream.endText();

        yPosition -= lineHeight;

        // Discount
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.newLineAtOffset(totalBoxX, yPosition);
        contentStream.showText("Discount:");
        contentStream.newLineAtOffset(150, 0);
        contentStream.showText("-₹" + String.format("%.2f", invoice.getDiscountAmount()));
        contentStream.endText();

        yPosition -= lineHeight;

        // Tax
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.newLineAtOffset(totalBoxX, yPosition);
        contentStream.showText("Tax:");
        contentStream.newLineAtOffset(150, 0);
        contentStream.showText("₹" + String.format("%.2f", invoice.getTaxAmount()));
        contentStream.endText();

        yPosition -= lineHeight;

        // Grand Total with background
        contentStream.setNonStrokingColor(0.9f, 0.9f, 0.9f);
        contentStream.addRect(totalBoxX - 5, yPosition - 5, totalBoxWidth, 25);
        contentStream.fill();
        contentStream.setNonStrokingColor(0f, 0f, 0f);

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
        contentStream.newLineAtOffset(totalBoxX, yPosition);
        contentStream.showText("Grand Total:");
        contentStream.newLineAtOffset(150, 0);
        contentStream.showText("₹" + String.format("%.2f", invoice.getTotalAmount()));
        contentStream.endText();

        // Border around totals
        contentStream.addRect(totalBoxX - 5, yPosition - 5, totalBoxWidth, 105);
        contentStream.stroke();
    }

    private void addInvoiceFooter(PDPageContentStream contentStream, float yPosition, float margin, float pageWidth) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Payment Terms:");
        contentStream.endText();

        yPosition -= 15;

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("• Payment due within 30 days of invoice date");
        contentStream.endText();

        yPosition -= 12;

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("• Late payment charges: 2% per month");
        contentStream.endText();

        yPosition -= 12;

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("• All disputes subject to Coimbatore jurisdiction");
        contentStream.endText();

        yPosition -= 20;

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Thank you for your business!");
        contentStream.endText();
    }
}