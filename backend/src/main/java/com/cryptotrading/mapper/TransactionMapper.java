package com.cryptotrading.mapper;

import com.cryptotrading.dto.TransactionDTO;
import com.cryptotrading.entity.Transaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ApplicationUserMapper.class})
public interface TransactionMapper {
    Transaction dtoToEntity(final TransactionDTO transactionDTO);

    TransactionDTO entityToDto(final Transaction transaction);

    List<Transaction> dtoListToEntityList(final List<TransactionDTO> transactionDTOList);

    List<TransactionDTO> entityListToDtoList(final List<Transaction> transactionList);
}
