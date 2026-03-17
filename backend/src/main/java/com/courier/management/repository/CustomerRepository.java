package com.courier.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courier.management.entity.Customer;
import com.courier.management.projection.CustProjection;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<CustProjection> findByClientId(Integer clientId);

    Customer findByCustCode(String custCode);

}