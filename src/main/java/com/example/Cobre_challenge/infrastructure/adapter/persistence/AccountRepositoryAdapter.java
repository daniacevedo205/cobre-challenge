package com.example.Cobre_challenge.infrastructure.adapter.persistence;

import com.example.Cobre_challenge.application.port.out.AccountRepositoryPort;
import com.example.Cobre_challenge.domain.model.Account;
import com.example.Cobre_challenge.infrastructure.adapter.persistence.entity.AccountEntity;
import com.example.Cobre_challenge.infrastructure.adapter.persistence.mapper.AccountMapper;
import com.example.Cobre_challenge.infrastructure.adapter.persistence.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountRepository repository;
    private final AccountMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findById(String accountId) {
        return repository.findById(accountId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void save(Account account) {

        AccountEntity entity = repository.findById(account.getAccountId())
                .orElse(new AccountEntity()); // Crea una nueva si no existe

        mapper.updateEntityFromDomain(account, entity);
        entity.setAccountId(account.getAccountId());
        repository.save(entity);
    }
}
