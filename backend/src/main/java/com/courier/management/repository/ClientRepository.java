package com.courier.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courier.management.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {}