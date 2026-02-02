package com.courier.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courier.management.entity.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {}