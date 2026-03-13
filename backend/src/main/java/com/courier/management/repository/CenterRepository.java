package com.courier.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courier.management.entity.Center;

@Repository
public interface CenterRepository extends JpaRepository<Center, Long> {}
