# Cobre Challenge - Módulo de Cuentas (Tarea 2)

Esta es la implementación de la **Tarea 2** del caso de estudio "Sr. Software Engineer". El objetivo es un módulo robusto de cuentas capaz de gestionar saldos y un ledger de transacciones, procesando eventos de forma concurrente.

## 🏛️ Arquitectura

La solución está implementada en **Spring Boot** y sigue los principios de la **Arquitectura Hexagonal (Puertos y Adaptadores)**.

* **`domain`**: El núcleo del negocio. Contiene las entidades `Account` y `Transaction` como POJOs puros, con la lógica de negocio (ej. `debit()`, `credit()`) y excepciones de dominio (`InsufficientFundsException`). No tiene dependencias de Spring o JPA.
* **`application`**: La capa de orquestación.
    * **`port.in` (Caso de Uso)**: Define la interfaz `ProcessTransactionUseCase`.
    * **`port.out` (Repositorios)**: Define las interfaces `AccountRepositoryPort` y `TransactionRepositoryPort`.
    * **`service`**: Implementa el caso de uso (`ProcessTransactionService`), coordina la lógica y maneja la transaccionalidad.
* **`infrastructure`**: El mundo exterior.
    * **`adapter.persistence`**: Adaptador de salida (driven) que implementa los puertos de repositorio usando **Spring Data JPA**.
    * **`adapter.file`**: Adaptador de entrada (driving) que usa un `CommandLineRunner` para leer un archivo JSON y disparar el caso de uso.

## ✨ Características Clave

* **Gestión de Saldos y Ledger**: Mantiene el saldo actual y un historial auditable de transacciones (créditos y débitos).
* **Procesamiento Concurrente**: Utiliza `parallelStream()` para procesar la lista de eventos del archivo en paralelo, demostrando la capacidad del sistema para manejar concurrencia.
* **Manejo de Concurrencia (Datos)**: Utiliza **Bloqueo Optimista** (`@Version` en `AccountEntity`) para prevenir *race conditions* y garantizar la consistencia de los saldos sin bloquear la base de datos.
* **Idempotencia**: El `ProcessTransactionService` verifica el `event_id` antes de procesar cualquier evento, asegurando que un evento duplicado no se aplique dos veces. La lógica de inicialización (`setupInitialAccounts`) también es idempotente.
* **Separación de Conceptos**: La lógica de negocio (`domain`) está 100% desacoplada de la persistencia (JPA) y de la entrada (el procesador de archivos).

## 🚀 Cómo Ejecutar

La aplicación procesará un archivo de eventos al arrancar y mostrará los saldos finales en la consola.

### 1. Archivo de Eventos

La aplicación espera un archivo de eventos en `src/main/resources/events.json`.

Crea este archivo con el siguiente contenido de ejemplo (o una lista más grande):

```json
[
  {
    "event_id": "cbmm_20250909_000123",
    "event_type": "cross_border_money_movement",
    "operation_date": "2025-09-09T15:32:10Z",
    "origin": {
      "account_id": "ACC123456789",
      "currency": "COP",
      "amount": 15000.50
    },
    "destination": {
      "account_id": "ACC987654321",
      "currency": "USD",
      "amount": 880.25
    }
  }
]
```
*(Nota: El saldo inicial para ACC123456789 se ha asumido como 200000.00 COP para que este evento sea válido, corrigiendo la discrepancia del documento).*

### 2. Ejecutar (Opción A: H2 - Base de datos en memoria)

Este es el modo por defecto. Rápido y no requiere configuración.

```bash
# Ejecutar la aplicación
./gradlew bootRun
```

La aplicación creará las cuentas iniciales, procesará `events.json` y se detendrá.

### 3. Ejecutar (Opción B: SQL Server - Docker)

Modo recomendado para probar la persistencia real y el bloqueo optimista.

1.  **Asegurar la dependencia**: Verifica que `com.microsoft.sqlserver:mssql-jdbc'` esté en tu `build.gradle`.
2.  **Iniciar la Base de Datos**:
    ```bash
    docker-compose up -d
    ```
3.  **Ejecutar la aplicación (con perfil `docker`)**:
    ```bash
    ./gradlew bootRun --args='--spring.profiles.active=docker'
    ```

### 3. Revisar la Salida

Busca en los logs de la consola los saldos finales. Verás algo como:

```
INFO --- [main] c.c.c.a.i.a.f.EventFileProcessor : --- CALCULANDO SALDOS FINALES ---
INFO --- [main] c.c.c.a.i.a.f.EventFileProcessor : SALDO FINAL: Cuenta: ACC123456789 | Saldo: COP 184999.50
INFO --- [main] c.c.c.a.i.a.f.EventFileProcessor : SALDO FINAL: Cuenta: ACC987654321 | Saldo: USD 880.25
```

## 🤖 Uso de IA

Este proyecto fue desarrollado con la asistencia de un modelo de IA (Gemini). Se utilizó para:
* Configuración de `docker-compose.yml` y `application-docker.properties`.
* Generación de este `README.md`.
* Bocetado de la estructura de la Arquitectura Hexagonal
