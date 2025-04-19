package com.cryptotrading.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(
        Long id,
        ApplicationUserDTO user,
        String ticker,
        LocalDateTime time,
        Boolean bought,
        BigDecimal quantity,
        BigDecimal price
) { }
