package com.example.Cobre_challenge.application.port.out;

import com.example.Cobre_challenge.domain.model.Account;

import java.util.Optional;

public interface AccountRepositoryPort {

    Optional<Account> findById(String accountId);
    void save(Account account);
}