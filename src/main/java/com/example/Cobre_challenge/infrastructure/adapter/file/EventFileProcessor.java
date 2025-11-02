package com.example.Cobre_challenge.infrastructure.adapter.file;

import com.example.Cobre_challenge.application.dto.TransactionEventDTO;
import com.example.Cobre_challenge.application.port.in.ProcessTransactionUseCase;
import com.example.Cobre_challenge.application.port.out.AccountRepositoryPort;
import com.example.Cobre_challenge.domain.model.Account;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

/**
 * Adaptador de Entrada (Driving Adapter)
 * Se ejecuta al inicio de la aplicación, lee el archivo de eventos
 * y dispara el procesamiento concurrente.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventFileProcessor implements CommandLineRunner {

    private final ProcessTransactionUseCase processTransactionUseCase;
    private final AccountRepositoryPort accountRepository;
    private final ObjectMapper objectMapper;

    // Hacemos la ruta del archivo configurable, con un valor por defecto.
    // El archivo debe estar en 'src/main/resources'
    @Value("${app.event.file.path:events.json}")
    private String eventFilePath;

    @Override
    public void run(String... args) throws Exception {
        log.info("--- INICIANDO PROCESADOR DE EVENTOS CBMM ---");

        // 1. Establecer las condiciones iniciales de las cuentas
        setupInitialAccounts();

        // 2. Cargar y parsear el archivo de eventos
        log.info("Cargando archivo de eventos desde la ruta: {}", eventFilePath);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(eventFilePath)) {
            if (inputStream == null) {
                log.error("¡Archivo de eventos no encontrado en '{}'!", eventFilePath);
                log.error("Asegúrate de que el archivo existe en 'src/main/resources'");
                return;
            }

            // Leemos la lista completa de eventos
            List<TransactionEventDTO> events = objectMapper.readValue(inputStream, new TypeReference<List<TransactionEventDTO>>() {});
            log.info("Se encontraron {} eventos. Iniciando procesamiento concurrente...", events.size());

            // 3. Procesamiento Concurrente
            // Usamos parallelStream() para procesar los eventos en paralelo
            events.parallelStream().forEach(this::processEventWrapper);

            log.info("--- PROCESAMIENTO DE EVENTOS FINALIZADO ---");

            // 4. Mostrar saldos finales
            logFinalBalances();

        } catch (Exception e) {
            log.error("Error fatal al leer o procesar el archivo de eventos.", e);
        }
    }

    /**
     * Wrapper para manejar excepciones individuales en el stream paralelo.
     * Esto evita que un evento fallido (ej. fondos insuficientes)
     * detenga el procesamiento de todos los demás eventos.
     */
    private void processEventWrapper(TransactionEventDTO event) {
        try {
            log.debug("Procesando evento: {}", event.getEventId());
            processTransactionUseCase.processEvent(event);
            log.debug("Evento procesado: {}", event.getEventId());
        } catch (Exception e) {
            // Captura excepciones por evento (ej. InsufficientFundsException, OptimisticLocking)
            log.error("Error al procesar el evento {}: {}", event.getEventId(), e.getMessage());
        }
    }

    private void setupInitialAccounts() {
        log.info("Estableciendo condiciones iniciales de las cuentas...");

        Account acc1 = new Account("ACC123456789", "COP", new BigDecimal("200000.00"));
        Account acc2 = new Account("ACC987654321", "USD", new BigDecimal("0.00"));

        // Usamos el puerto para guardarlas
        accountRepository.save(acc1);
        accountRepository.save(acc2);

        log.info("Cuenta {} creada con saldo: {} {}", acc1.getAccountId(), acc1.getCurrency(), acc1.getBalance());
        log.info("Cuenta {} creada con saldo: {} {}", acc2.getAccountId(), acc2.getCurrency(), acc2.getBalance());
    }

    /**
     * Muestra los saldos finales de las cuentas procesadas.
     * Esto demuestra el requisito de "Final Balance Calculation".
     */
    private void logFinalBalances() {
        log.info("--- CALCULANDO SALDOS FINALES ---");

        accountRepository.findById("ACC123456789").ifPresent(acc ->
                log.info("SALDO FINAL: Cuenta: {} | Saldo: {} {}", acc.getAccountId(), acc.getCurrency(), acc.getBalance()));

        accountRepository.findById("ACC987654321").ifPresent(acc ->
                log.info("SALDO FINAL: Cuenta: {} | Saldo: {} {}", acc.getAccountId(), acc.getCurrency(), acc.getBalance()));
    }
}
