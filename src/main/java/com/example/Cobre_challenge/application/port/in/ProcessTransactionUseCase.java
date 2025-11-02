package com.example.Cobre_challenge.application.port.in;

import com.example.Cobre_challenge.application.dto.TransactionEventDTO;

@FunctionalInterface
public interface ProcessTransactionUseCase {

    void processEvent(TransactionEventDTO event);
}
