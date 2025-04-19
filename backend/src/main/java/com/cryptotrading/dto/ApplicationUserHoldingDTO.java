package com.cryptotrading.dto;

import com.cryptotrading.entity.ApplicationUser;

import java.math.BigDecimal;

public record ApplicationUserHoldingDTO(
        Long id,
        String symbol,
        BigDecimal quantity,
        ApplicationUserDTO user
) {
}
