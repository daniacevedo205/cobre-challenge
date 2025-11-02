package com.example.Cobre_challenge.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Transaction {

    private final UUID transactionId;
    private final String accountId;
    private final String eventId; // Para trazabilidad e idempotencia
    private final TransactionType type;
    private final BigDecimal amount;
    private final Instant operationDate;

    public Transaction(
            String accountId,
            String eventId,
            TransactionType type,
            BigDecimal amount,
            Instant operationDate) {

        this.transactionId = UUID.randomUUID();
        this.accountId = accountId;
        this.eventId = eventId;
        this.type = type;
        this.amount = amount;
        this.operationDate = operationDate;
    }
}
