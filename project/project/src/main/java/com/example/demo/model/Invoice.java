package com.example.demo.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Invoice {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long id;

    @Column(name = "invoiceno", unique = true, nullable = false)
    private String invoiceno;

    @Column(name = "invoice_date", nullable = false)
    private LocalDateTime quatDate;

    @Column(name = "validity", nullable = false)
    private Integer validity;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name="tax_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(name= "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    // Foreign key to Customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "FK_quat_customer"))
    private Customer customer;

    // Foreign key to Login (User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_quat_user"))
    private Login user;

    // One quotation can have many quotation items
    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Qitem> quotationItems;

    public Invoice(Long id, String invoiceno, LocalDateTime quatDate, Integer validity, BigDecimal totalAmount, BigDecimal taxAmount, BigDecimal discountAmount, Customer customer, Login user, List<Qitem> quotationItems) {
        this.id = id;
        this.invoiceno = invoiceno;
        this.quatDate = quatDate;
        this.validity = validity;
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.customer = customer;
        this.user = user;
        this.quotationItems = quotationItems;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceno() {
        return invoiceno;
    }

    public void setInvoiceno(String invoiceno) {
        this.invoiceno = invoiceno;
    }

    public LocalDateTime getQuatDate() {
        return quatDate;
    }

    public void setQuatDate(LocalDateTime quatDate) {
        this.quatDate = quatDate;
    }

    public Integer getValidity() {
        return validity;
    }

    public void setValidity(Integer validity) {
        this.validity = validity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Login getUser() {
        return user;
    }

    public void setUser(Login user) {
        this.user = user;
    }

    public List<Qitem> getQuotationItems() {
        return quotationItems;
    }

    public void setQuotationItems(List<Qitem> quotationItems) {
        this.quotationItems = quotationItems;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", invoiceno='" + invoiceno + '\'' +
                ", quatDate=" + quatDate +
                ", validity=" + validity +
                ", totalAmount=" + totalAmount +
                ", taxAmount=" + taxAmount +
                ", discountAmount=" + discountAmount +
                ", customer=" + customer +
                ", user=" + user +
                ", quotationItems=" + quotationItems +
                '}';
    }
}
