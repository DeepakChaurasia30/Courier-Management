package com.courier.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.courier.management.entity.Invoice;
import com.courier.management.projection.InvProjection;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
   
    @Query("SELECT e FROM Invoice e WHERE e.invNo = :invNo")
    InvProjection findByInvNoS(String invNo);

    Optional<Invoice> findByInvNo(String invNo);
    
}