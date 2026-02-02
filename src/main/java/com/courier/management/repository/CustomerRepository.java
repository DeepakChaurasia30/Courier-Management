package com.courier.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courier.management.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {}