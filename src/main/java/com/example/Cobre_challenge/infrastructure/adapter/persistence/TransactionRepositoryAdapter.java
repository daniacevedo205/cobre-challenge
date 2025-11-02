package com.example.Cobre_challenge.infrastructure.adapter.persistence;

import com.example.Cobre_challenge.application.port.out.TransactionRepositoryPort;
import com.example.Cobre_challenge.domain.model.Transaction;
import com.example.Cobre_challenge.infrastructure.adapter.persistence.entity.TransactionEntity;
import com.example.Cobre_challenge.infrastructure.adapter.persistence.mapper.TransactionMapper;
import com.example.Cobre_challenge.infrastructure.adapter.persistence.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionRepository repository;
    private final TransactionMapper mapper;

    @Override
    public void save(Transaction transaction) {
        TransactionEntity entity = mapper.toEntity(transaction);
        repository.save(entity);
    }

    @Override
    public boolean existsByEventId(String eventId) {
        return repository.existsByEventId(eventId);
    }
}
