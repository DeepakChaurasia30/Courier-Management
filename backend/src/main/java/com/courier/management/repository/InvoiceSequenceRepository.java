package com.courier.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.courier.management.entity.InvoiceSequence;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceSequenceRepository extends JpaRepository<InvoiceSequence, Integer> {
    Optional<InvoiceSequence> findByClient_ClientIdAndFy(Integer clientId, String fy);
}
