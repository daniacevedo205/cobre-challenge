package com.example.Cobre_challenge.application.port.out;

import com.example.Cobre_challenge.domain.model.Transaction;

public interface TransactionRepositoryPort {

    void save(Transaction transaction);
    boolean existsByEventId(String eventId);
}
