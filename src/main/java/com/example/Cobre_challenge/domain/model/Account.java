package com.example.Cobre_challenge.domain.model;

import com.example.Cobre_challenge.domain.exception.InsufficientFundsException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class Account {

    private final String accountId;
    private final String currency;
    private BigDecimal balance;

    public Account(String accountId, String currency, BigDecimal balance) {
        this.accountId = Objects.requireNonNull(accountId);
        this.currency = Objects.requireNonNull(currency);
        this.balance = Objects.requireNonNull(balance);
    }

    public void debit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a debitar debe ser positivo.");
        }

        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(this.accountId, this.currency, amount.doubleValue());
        }

        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a acreditar debe ser positivo.");
        }

        this.balance = this.balance.add(amount);
    }
}