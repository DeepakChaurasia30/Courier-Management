package com.courier.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courier.management.entity.Entry;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {}