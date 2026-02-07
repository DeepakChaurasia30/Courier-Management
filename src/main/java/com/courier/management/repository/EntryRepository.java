package com.courier.management.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.courier.management.entity.Entry;
import com.courier.management.entity.Invoice;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {

    // Fixme Projection can be used we need awbno & invoice ! Entity
    // @Query("SELECT e FROM Entry e WHERE e.awbNo = :awbNo")
    Optional<Entry> findByAwbNo(String awbNo);

    @Query("SELECT e FROM Entry e WHERE e.awbNo = :awbNo")
    Entry findByAwbNo1(String awbNo);

    // Invoice Genertion Part
    //fix me Typo to Snake case
    List<Entry> findByCustidAndClientidAndAwbDateBetweenAndInvoiceIsNull(
            Long custid,
            Integer clientid,
            LocalDate startDate,
            LocalDate endDate);

    List<Entry> findByInvoice(Invoice invoice); 

}