package com.cryptotrading.service;

import com.cryptotrading.dto.ApplicationUserDTO;
import com.cryptotrading.dto.ApplicationUserHoldingDTO;
import com.cryptotrading.dto.TransactionDTO;
import com.cryptotrading.entity.ApplicationUserHolding;
import com.cryptotrading.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface IApplicationUserService {

    public TransactionDTO buy(final ApplicationUserDTO applicationUserDTO, final String symbol, final BigDecimal quantity);

    public TransactionDTO sell(final ApplicationUserDTO applicationUserDTO, final String symbol, final BigDecimal quantity);

    public List<TransactionDTO> getPastTransactions(final Long id);

    public ApplicationUserDTO logIn(final ApplicationUserDTO applicationUserDTO);

    public ApplicationUserDTO register(final ApplicationUserDTO applicationUserDTO);

    public ApplicationUserDTO restart(final ApplicationUserDTO applicationUserDTO);

    public List<ApplicationUserHoldingDTO> getPortfolio(final Long id);
}
