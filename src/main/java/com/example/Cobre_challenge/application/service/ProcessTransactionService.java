package com.example.Cobre_challenge.application.service;

import com.example.Cobre_challenge.application.dto.TransactionEventDTO;
import com.example.Cobre_challenge.application.port.in.ProcessTransactionUseCase;
import com.example.Cobre_challenge.application.port.out.AccountRepositoryPort;
import com.example.Cobre_challenge.application.port.out.TransactionRepositoryPort;
import com.example.Cobre_challenge.domain.model.Account;
import com.example.Cobre_challenge.domain.model.Transaction;
import com.example.Cobre_challenge.domain.model.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessTransactionService implements ProcessTransactionUseCase {

    private final AccountRepositoryPort accountRepository;
    private final TransactionRepositoryPort transactionRepository;

    @Override
    @Transactional
    public void processEvent(TransactionEventDTO event) {

        if (transactionRepository.existsByEventId(event.getEventId())) {
            log.warn("Evento ya procesado: {}. Omitiendo.", event.getEventId());
            return;
        }

        Account originAccount = loadAndValidateAccount(
                event.getOrigin().getAccountId(),
                event.getOrigin().getCurrency()
        );


        originAccount.debit(event.getOrigin().getAmount());

        Transaction debitTransaction = new Transaction(
                originAccount.getAccountId(),
                event.getEventId(),
                TransactionType.DEBIT,
                event.getOrigin().getAmount(),
                event.getOperationDate()
        );

        Account destinationAccount = loadAndValidateAccount(
                event.getDestination().getAccountId(),
                event.getDestination().getCurrency()
        );

        destinationAccount.credit(event.getDestination().getAmount());

        Transaction creditTransaction = new Transaction(
                destinationAccount.getAccountId(),
                event.getEventId(),
                TransactionType.CREDIT,
                event.getDestination().getAmount(),
                event.getOperationDate()
        );


        accountRepository.save(originAccount);
        accountRepository.save(destinationAccount);

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        log.info("Evento procesado exitosamente: {}", event.getEventId());
    }

    private Account loadAndValidateAccount(String accountId, String expectedCurrency) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada: " + accountId)); // Usar una excepción custom


        if (!account.getCurrency().equals(expectedCurrency)) {
            throw new IllegalArgumentException(String.format(
                    "Error de moneda para cuenta %s. Se esperaba %s pero se recibió %s.",
                    accountId, account.getCurrency(), expectedCurrency
            ));
        }
        return account;
    }
}
