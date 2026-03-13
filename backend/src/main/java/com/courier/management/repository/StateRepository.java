package com.courier.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.courier.management.entity.State;

@Repository
public interface StateRepository extends JpaRepository<State, String> {}