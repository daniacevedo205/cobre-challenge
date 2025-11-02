package com.example.Cobre_challenge.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEventDTO {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("operation_date")
    private Instant operationDate;

    @JsonProperty("origin")
    private OriginDTO origin;

    @JsonProperty("destination")
    private DestinationDTO destination;
}