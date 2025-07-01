package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InvoiceService {

    @Autowired
    private InvoiceRepo invoiceRepo;

    @Autowired
    private InvoiceItemRepo invoiceItemRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private LoginRepo loginRepo;

    @Autowired
    private QuatRepo quatRepo;

    @Autowired
    private ItemRepos itemRepo;

    public List<Invoice> getAllInvoices() {
        return invoiceRepo.findAll();
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepo.findById(id);
    }

    public List<Invoice> getInvoicesByCustomerId(Long customerId) {
        return invoiceRepo.findByCustomerId(customerId);
    }

    public List<Invoice> getInvoicesByUserId(Long userId) {
        return invoiceRepo.findByUserId(userId);
    }

    public List<Invoice> getInvoicesByQuotationId(Long quotationId) {
        return invoiceRepo.findByQuotationId(quotationId);
    }

    public List<Invoice> getOverdueInvoices() {
        return invoiceRepo.findOverdueInvoices(LocalDateTime.now());
    }

    public Invoice createInvoice(Invoice invoice) {
        // Validate and set customer
        if (invoice.getCustomer() != null && invoice.getCustomer().getId() != null) {
            Optional<Customer> customer = customerRepo.findById(invoice.getCustomer().getId());
            if (customer.isPresent()) {
                invoice.setCustomer(customer.get());
            } else {
                throw new RuntimeException("Customer not found with id: " + invoice.getCustomer().getId());
            }
        }

        // Validate and set user
        if (invoice.getUser() != null && invoice.getUser().getId() != null) {
            Optional<Login> user = loginRepo.findById(invoice.getUser().getId());
            if (user.isPresent()) {
                invoice.setUser(user.get());
            } else {
                throw new RuntimeException("User not found with id: " + invoice.getUser().getId());
            }
        }

        // Validate and set quotation if provided
        if (invoice.getQuotation() != null && invoice.getQuotation().getId() != null) {
            Optional<Quat> quotation = quatRepo.findById(invoice.getQuotation().getId());
            if (quotation.isPresent()) {
                invoice.setQuotation(quotation.get());
            } else {
                throw new RuntimeException("Quotation not found with id: " + invoice.getQuotation().getId());
            }
        }

        // Check if invoice number already exists
        if (invoiceRepo.existsByInvoiceno(invoice.getInvoiceno())) {
            throw new RuntimeException("Invoice number already exists: " + invoice.getInvoiceno());
        }

        // Set default values
        if (invoice.getInvoiceDate() == null) {
            invoice.setInvoiceDate(LocalDateTime.now());
        }
        
        if (invoice.getStatus() == null) {
            invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        }
        
        if (invoice.getPaymentStatus() == null) {
            invoice.setPaymentStatus(Invoice.PaymentStatus.UNPAID);
        }

        // Calculate totals
        calculateInvoiceTotals(invoice);

        return invoiceRepo.save(invoice);
    }

    public Invoice updateInvoice(Long id, Invoice updateInvoice) {
        return invoiceRepo.findById(id).map(invoice -> {
            if (updateInvoice.getInvoiceno() != null) {
                // Check if new invoice number already exists (excluding current record)
                if (!invoice.getInvoiceno().equals(updateInvoice.getInvoiceno()) && 
                    invoiceRepo.existsByInvoiceno(updateInvoice.getInvoiceno())) {
                    throw new RuntimeException("Invoice number already exists: " + updateInvoice.getInvoiceno());
                }
                invoice.setInvoiceno(updateInvoice.getInvoiceno());
            }
            
            if (updateInvoice.getInvoiceDate() != null) {
                invoice.setInvoiceDate(updateInvoice.getInvoiceDate());
            }
            
            if (updateInvoice.getDueDate() != null) {
                invoice.setDueDate(updateInvoice.getDueDate());
            }
            
            if (updateInvoice.getValidity() != null) {
                invoice.setValidity(updateInvoice.getValidity());
            }
            
            if (updateInvoice.getStatus() != null) {
                invoice.setStatus(updateInvoice.getStatus());
            }
            
            if (updateInvoice.getPaymentStatus() != null) {
                invoice.setPaymentStatus(updateInvoice.getPaymentStatus());
            }
            
            if (updateInvoice.getNotes() != null) {
                invoice.setNotes(updateInvoice.getNotes());
            }
            
            if (updateInvoice.getTerms() != null) {
                invoice.setTerms(updateInvoice.getTerms());
            }
            
            // Update customer if provided
            if (updateInvoice.getCustomer() != null && updateInvoice.getCustomer().getId() != null) {
                Optional<Customer> customer = customerRepo.findById(updateInvoice.getCustomer().getId());
                customer.ifPresent(invoice::setCustomer);
            }
            
            // Update user if provided
            if (updateInvoice.getUser() != null && updateInvoice.getUser().getId() != null) {
                Optional<Login> user = loginRepo.findById(updateInvoice.getUser().getId());
                user.ifPresent(invoice::setUser);
            }
            
            // Update quotation if provided
            if (updateInvoice.getQuotation() != null && updateInvoice.getQuotation().getId() != null) {
                Optional<Quat> quotation = quatRepo.findById(updateInvoice.getQuotation().getId());
                quotation.ifPresent(invoice::setQuotation);
            }
            
            // Recalculate totals
            calculateInvoiceTotals(invoice);
            
            return invoiceRepo.save(invoice);
        }).orElse(null);
    }

    public boolean deleteInvoice(Long id) {
        if (invoiceRepo.existsById(id)) {
            invoiceRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public Invoice createInvoiceFromQuotation(Long quotationId) {
        Optional<Quat> quotationOpt = quatRepo.findById(quotationId);
        if (!quotationOpt.isPresent()) {
            throw new RuntimeException("Quotation not found with id: " + quotationId);
        }

        Quat quotation = quotationOpt.get();
        
        // Generate invoice number
        String invoiceNumber = generateInvoiceNumber();
        
        Invoice invoice = new Invoice();
        invoice.setInvoiceno(invoiceNumber);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setDueDate(LocalDateTime.now().plusDays(30)); // Default 30 days
        invoice.setValidity(quotation.getValidity());
        invoice.setCustomer(quotation.getCustomer());
        invoice.setUser(quotation.getUser());
        invoice.setQuotation(quotation);
        invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        invoice.setPaymentStatus(Invoice.PaymentStatus.UNPAID);
        
        // Save invoice first
        invoice = invoiceRepo.save(invoice);
        
        // Create invoice items from quotation items
        List<Qitem> quotationItems = quotation.getQuotationItems();
        if (quotationItems != null) {
            for (Qitem qitem : quotationItems) {
                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setInvoice(invoice);
                invoiceItem.setItem(qitem.getItem());
                invoiceItem.setQuantity(qitem.getQuantity());
                invoiceItem.setUnitPrice(qitem.getUnitPrice());
                invoiceItem.setLicenseType(qitem.getLicenseType());
                invoiceItemRepo.save(invoiceItem);
            }
        }
        
        // Reload invoice with items and recalculate totals
        invoice = invoiceRepo.findById(invoice.getId()).orElse(invoice);
        calculateInvoiceTotals(invoice);
        
        return invoiceRepo.save(invoice);
    }

    private void calculateInvoiceTotals(Invoice invoice) {
        if (invoice.getInvoiceItems() == null || invoice.getInvoiceItems().isEmpty()) {
            invoice.setSubtotal(BigDecimal.ZERO);
            invoice.setTaxAmount(BigDecimal.ZERO);
            invoice.setDiscountAmount(BigDecimal.ZERO);
            invoice.setTotalAmount(BigDecimal.ZERO);
            return;
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (InvoiceItem item : invoice.getInvoiceItems()) {
            if (item.getQuantity() != null && item.getUnitPrice() != null) {
                BigDecimal itemSubtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                subtotal = subtotal.add(itemSubtotal);
                
                if (item.getDiscountPercentage() != null) {
                    BigDecimal discountAmount = itemSubtotal.multiply(item.getDiscountPercentage().divide(BigDecimal.valueOf(100)));
                    totalDiscount = totalDiscount.add(discountAmount);
                }
                
                if (item.getTaxPercentage() != null) {
                    BigDecimal afterDiscount = itemSubtotal.subtract(
                        itemSubtotal.multiply(item.getDiscountPercentage() != null ? 
                            item.getDiscountPercentage().divide(BigDecimal.valueOf(100)) : BigDecimal.ZERO)
                    );
                    BigDecimal taxAmount = afterDiscount.multiply(item.getTaxPercentage().divide(BigDecimal.valueOf(100)));
                    totalTax = totalTax.add(taxAmount);
                }
            }
        }

        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(totalTax);
        invoice.setDiscountAmount(totalDiscount);
        invoice.setTotalAmount(subtotal.subtract(totalDiscount).add(totalTax));
    }

    private String generateInvoiceNumber() {
        String prefix = "INV-" + LocalDateTime.now().getYear() + "-";
        
        // Find the last invoice with this year's prefix
        List<Invoice> invoices = invoiceRepo.findAll();
        int maxNumber = 0;
        
        for (Invoice invoice : invoices) {
            if (invoice.getInvoiceno().startsWith(prefix)) {
                try {
                    String numberPart = invoice.getInvoiceno().substring(prefix.length());
                    int number = Integer.parseInt(numberPart);
                    maxNumber = Math.max(maxNumber, number);
                } catch (NumberFormatException e) {
                    // Ignore invalid formats
                }
            }
        }
        
        return prefix + String.format("%04d", maxNumber + 1);
    }

    public Invoice updateInvoiceStatus(Long id, Invoice.InvoiceStatus status) {
        return invoiceRepo.findById(id).map(invoice -> {
            invoice.setStatus(status);
            return invoiceRepo.save(invoice);
        }).orElse(null);
    }

    public Invoice updatePaymentStatus(Long id, Invoice.PaymentStatus paymentStatus) {
        return invoiceRepo.findById(id).map(invoice -> {
            invoice.setPaymentStatus(paymentStatus);
            return invoiceRepo.save(invoice);
        }).orElse(null);
    }
}