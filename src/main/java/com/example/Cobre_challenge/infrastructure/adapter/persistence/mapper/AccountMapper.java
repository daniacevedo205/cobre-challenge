package com.example.Cobre_challenge.infrastructure.adapter.persistence.mapper;

import com.example.Cobre_challenge.domain.model.Account;
import com.example.Cobre_challenge.infrastructure.adapter.persistence.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    Account toDomain(AccountEntity entity);

    @Mapping(target = "version", ignore = true) // Ignoramos 'version' al crear
    AccountEntity toEntity(Account domain);

    @Mapping(target = "version", ignore = true) // La versión la maneja JPA
    void updateEntityFromDomain(Account domain, @MappingTarget AccountEntity entity);
}
