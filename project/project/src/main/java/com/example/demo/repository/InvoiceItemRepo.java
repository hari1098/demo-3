package com.example.demo.repository;

import com.example.demo.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepo extends JpaRepository<InvoiceItem, Long> {
    
    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.invoice.id = :invoiceId")
    List<InvoiceItem> findByInvoiceId(@Param("invoiceId") Long invoiceId);
    
    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.item.id = :itemId")
    List<InvoiceItem> findByItemId(@Param("itemId") Long itemId);
}