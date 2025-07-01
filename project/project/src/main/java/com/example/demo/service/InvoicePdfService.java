package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.Item;
import com.example.demo.model.Qitem;
import com.example.demo.model.Quat;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InvoicePdfService {

    @Autowired
    private QuatService quatService;

    @Autowired
    private QitemService qitemService;

    // Company Information
    private static final String COMPANY_NAME = "Your Company Name";
    private static final String COMPANY_ADDRESS = "123 Business Street, Coimbatore - 641001";
    private static final String COMPANY_EMAIL = "sales@yourcompany.com";
    private static final String COMPANY_PHONE = "+91 9876543210";
    private static final String COMPANY_WEBSITE = "www.yourcompany.com";
    private static final String COMPANY_GSTIN = "22AAAAA0000A1Z5";

    private static final float GST_RATE = 0.18f;
    private static final float DISCOUNT_RATE = 0.05f; // 5% discount

    public byte[] generateInvoicePdf(Long quatId) throws IOException {
        // Validate quatId
        if (quatId == null || quatId <= 0) {
            throw new IllegalArgumentException("Invalid quotation ID");
        }

        // Fetch quotation data
        Quat quat = quatService.getQuatById(quatId)
                .orElseThrow(() -> new RuntimeException("Quotation not found with ID: " + quatId));

        // Fetch customer data
        Customer customer = quat.getCustomer();
        if (customer == null) {
            throw new RuntimeException("Customer not found for quotation ID: " + quatId);
        }

        // Fetch quotation items
        List<Qitem> quotationItems = qitemService.getQitemsByQuotationId(quatId);

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
            yPosition = addInvoiceDetails(contentStream, yPosition, margin, pageWidth, quat);

            // Add customer details
            yPosition = addCustomerDetails(contentStream, yPosition, margin, pageWidth, customer);

            // Add items table
            BigDecimal subTotal = addItemsTable(contentStream, yPosition, margin, pageWidth, quotationItems);

            // Add totals with GST and discount
            addInvoiceTotals(contentStream, 200, margin, pageWidth, subTotal);

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

    private float addInvoiceDetails(PDPageContentStream contentStream, float yPosition, float margin, float pageWidth, Quat quat) throws IOException {
        // Invoice number (based on quotation)
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Invoice No: ");
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.showText("INV-" + quat.getQuatno());
        contentStream.endText();

        // Invoice date (current date)
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(margin + 300, yPosition);
        contentStream.showText("Date: ");
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.showText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        contentStream.endText();

        yPosition -= 20;

        // Reference quotation
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Reference Quote: ");
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.showText(quat.getQuatno());
        contentStream.endText();

        // Due date (30 days from invoice date)
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(margin + 300, yPosition);
        contentStream.showText("Due Date: ");
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.showText(LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
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

    private BigDecimal addItemsTable(PDPageContentStream contentStream, float yPosition, float margin, float pageWidth, List<Qitem> qitems) throws IOException {
        float tableTop = yPosition;
        float tableHeight = 20;
        float rowHeight = 20;

        // Table headers
        String[] headers = {"#", "Description", "License", "Qty", "Unit Price", "Amount"};
        float[] columnWidths = {30, 200, 80, 50, 80, 80};

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
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
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

        BigDecimal subTotal = BigDecimal.ZERO;
        float currentY = tableTop - tableHeight;
        int rowNum = 1;

        // Add data rows
        for (Qitem qitem : qitems) {
            Item item = qitem.getItem();
            String itemName = item != null ? item.getItemname() : "Unknown Item";
            String licenseType = qitem.getLicenseType() != null ? qitem.getLicenseType() : "Standard";
            BigDecimal unitPrice = qitem.getUnitPrice() != null ? qitem.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal itemTotal = qitem.getTotalPrice() != null ? qitem.getTotalPrice() : BigDecimal.ZERO;
            subTotal = subTotal.add(itemTotal);

            currentY -= rowHeight;
            currentX = margin;

            // Row data
            String[] rowData = {
                String.valueOf(rowNum++),
                itemName,
                licenseType,
                String.valueOf(qitem.getQuantity()),
                String.format("₹%.2f", unitPrice),
                String.format("₹%.2f", itemTotal)
            };

            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
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

        return subTotal;
    }

    private void addInvoiceTotals(PDPageContentStream contentStream, float yPosition, float margin, float pageWidth, BigDecimal subTotal) throws IOException {
        float totalBoxWidth = 250;
        float totalBoxX = margin + pageWidth - totalBoxWidth;
        float lineHeight = 25;

        // Calculate amounts
        BigDecimal discountAmount = subTotal.multiply(BigDecimal.valueOf(DISCOUNT_RATE));
        BigDecimal afterDiscount = subTotal.subtract(discountAmount);
        BigDecimal gstAmount = afterDiscount.multiply(BigDecimal.valueOf(GST_RATE));
        BigDecimal grandTotal = afterDiscount.add(gstAmount);

        // Subtotal
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.newLineAtOffset(totalBoxX, yPosition);
        contentStream.showText("Subtotal:");
        contentStream.newLineAtOffset(150, 0);
        contentStream.showText("₹" + String.format("%.2f", subTotal));
        contentStream.endText();

        yPosition -= lineHeight;

        // Discount
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.newLineAtOffset(totalBoxX, yPosition);
        contentStream.showText("Discount (" + (DISCOUNT_RATE * 100) + "%):");
        contentStream.newLineAtOffset(150, 0);
        contentStream.showText("-₹" + String.format("%.2f", discountAmount));
        contentStream.endText();

        yPosition -= lineHeight;

        // After discount
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.newLineAtOffset(totalBoxX, yPosition);
        contentStream.showText("After Discount:");
        contentStream.newLineAtOffset(150, 0);
        contentStream.showText("₹" + String.format("%.2f", afterDiscount));
        contentStream.endText();

        yPosition -= lineHeight;

        // GST
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.newLineAtOffset(totalBoxX, yPosition);
        contentStream.showText("GST (" + (GST_RATE * 100) + "%):");
        contentStream.newLineAtOffset(150, 0);
        contentStream.showText("₹" + String.format("%.2f", gstAmount));
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
        contentStream.showText("₹" + String.format("%.2f", grandTotal));
        contentStream.endText();

        // Border around totals
        contentStream.addRect(totalBoxX - 5, yPosition - 5, totalBoxWidth, 130);
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