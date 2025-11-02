package com.example.Cobre_challenge.infrastructure.adapter.persistence.mapper;

import com.example.Cobre_challenge.domain.model.Transaction;
import com.example.Cobre_challenge.infrastructure.adapter.persistence.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "transactionId", ignore = true)
    TransactionEntity toEntity(Transaction domain);
}
