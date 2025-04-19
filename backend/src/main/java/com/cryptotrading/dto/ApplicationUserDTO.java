package com.cryptotrading.dto;

import java.math.BigDecimal;

public record ApplicationUserDTO(
        Long id,
        BigDecimal balance,
        String username,
        String email,
        String password
) { }
