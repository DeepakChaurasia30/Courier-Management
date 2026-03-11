package com.courier.management.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.courier.management.entity.Entry;
import com.courier.management.entity.Invoice;
import com.courier.management.projection.EntryProjection;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {

    // Fixme Projection can be used we need awbno & invoice ! Entity
    // @Query("SELECT e FROM Entry e WHERE e.awbNo = :awbNo")
    Optional<Entry> findByAwbNo(String awbNo);

    @Query("SELECT e FROM Entry e WHERE e.awbNo = :awbNo")
    Entry findByAwbNo1(String awbNo);

    // Invoice Genertion Part
    // fix me Typo to Snake case
    List<Entry> findByCustidAndClientidAndAwbDateBetweenAndInvoiceIsNull(
            Long custid,
            Integer clientid,
            LocalDate startDate,
            LocalDate endDate);

    List<Entry> findByInvoice(Invoice invoice);

    @Query("""
                SELECT
                    e.awbNo      AS awbNo,
                    e.awbDate    AS awbDate,
                    c.destName   AS centerName,
                    s.stateCode      AS  stateCode,
                    e.weight     AS weight,
                    e.charge     AS charge,
                    e.srvType    AS srvType
                FROM Entry e
                JOIN e.center c JOIN c.state s
                WHERE e.invoice.id = :invoiceId
            """) 
    List<EntryProjection> findEntriesByInvoice(@Param("invoiceId") Long invoiceId);

}