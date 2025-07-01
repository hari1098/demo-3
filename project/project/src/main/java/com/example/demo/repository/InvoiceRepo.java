package com.example.demo.repository;

import com.example.demo.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepo extends JpaRepository<Invoice, Long> {
    
    Optional<Invoice> findByInvoiceno(String invoiceno);
    
    boolean existsByInvoiceno(String invoiceno);
    
    @Query("SELECT i FROM Invoice i WHERE i.customer.id = :customerId")
    List<Invoice> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId")
    List<Invoice> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT i FROM Invoice i WHERE i.quotation.id = :quotationId")
    List<Invoice> findByQuotationId(@Param("quotationId") Long quotationId);
    
    @Query("SELECT i FROM Invoice i WHERE i.status = :status")
    List<Invoice> findByStatus(@Param("status") Invoice.InvoiceStatus status);
    
    @Query("SELECT i FROM Invoice i WHERE i.paymentStatus = :paymentStatus")
    List<Invoice> findByPaymentStatus(@Param("paymentStatus") Invoice.PaymentStatus paymentStatus);
    
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :currentDate AND i.paymentStatus != 'PAID'")
    List<Invoice> findOverdueInvoices(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate")
    List<Invoice> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);
}