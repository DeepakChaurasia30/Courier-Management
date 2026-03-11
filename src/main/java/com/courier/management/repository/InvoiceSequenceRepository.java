package com.courier.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.courier.management.entity.InvoiceSequence;

import java.util.Optional;

public interface InvoiceSequenceRepository 
        extends JpaRepository<InvoiceSequence, Integer> {

    Optional<InvoiceSequence> findByFy(String fy);

}
