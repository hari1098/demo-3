package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_item")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage = BigDecimal.ZERO;

    @Column(name = "license_type")
    private String licenseType;

    // Foreign key to Invoice
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false, foreignKey = @ForeignKey(name = "FK_invoice_item_invoice"))
    private Invoice invoice;

    // Foreign key to Item
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "FK_invoice_item_item"))
    private Item item;

    public InvoiceItem() {
    }

    public InvoiceItem(Integer quantity, BigDecimal unitPrice, String licenseType, 
                      Invoice invoice, Item item) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.licenseType = licenseType;
        this.invoice = invoice;
        this.item = item;
        calculateTotalPrice();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
        calculateTotalPrice();
    }

    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
        calculateTotalPrice();
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        if (item != null && unitPrice == null) {
            this.unitPrice = item.getPrice();
            calculateTotalPrice();
        }
    }

    // Helper methods
    public Long getInvoiceId() {
        return invoice != null ? invoice.getId() : null;
    }

    public Long getItemId() {
        return item != null ? item.getId() : null;
    }

    // Calculate total price with discount and tax
    private void calculateTotalPrice() {
        if (quantity != null && unitPrice != null) {
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            
            // Apply discount
            BigDecimal discountAmount = subtotal.multiply(discountPercentage.divide(BigDecimal.valueOf(100)));
            BigDecimal afterDiscount = subtotal.subtract(discountAmount);
            
            // Apply tax
            BigDecimal taxAmount = afterDiscount.multiply(taxPercentage.divide(BigDecimal.valueOf(100)));
            
            this.totalPrice = afterDiscount.add(taxAmount);
        }
    }

    @PrePersist
    @PreUpdate
    private void prePersist() {
        calculateTotalPrice();
    }

    @Override
    public String toString() {
        return "InvoiceItem{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", discountPercentage=" + discountPercentage +
                ", taxPercentage=" + taxPercentage +
                ", licenseType='" + licenseType + '\'' +
                '}';
    }
}