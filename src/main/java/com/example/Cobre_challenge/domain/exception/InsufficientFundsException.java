package com.example.Cobre_challenge.domain.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String accountId, String currency, double attemptedAmount) {
        super(String.format(
                "La cuenta %s no tiene fondos suficientes. Se intentó debitar %.2f %s.",
                accountId,
                attemptedAmount,
                currency
        ));
    }
}
