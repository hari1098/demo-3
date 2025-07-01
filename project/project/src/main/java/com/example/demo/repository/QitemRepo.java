package com.example.demo.repository;

import com.example.demo.model.Qitem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QitemRepo extends JpaRepository<Qitem, Long> {
    
    @Query("SELECT qi FROM Qitem qi WHERE qi.quotation.id = :quatId")
    List<Qitem> findByQuotationId(@Param("quatId") Long quatId);
    
    @Query("SELECT qi FROM Qitem qi WHERE qi.item.id = :itemId")
    List<Qitem> findByItemId(@Param("itemId") Long itemId);
}