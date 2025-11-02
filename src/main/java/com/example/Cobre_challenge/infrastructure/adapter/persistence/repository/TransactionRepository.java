package com.example.Cobre_challenge.infrastructure.adapter.persistence.repository;

import com.example.Cobre_challenge.infrastructure.adapter.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    boolean existsByEventId(String eventId);
}
