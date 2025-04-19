package com.cryptotrading.controller;

import com.cryptotrading.dto.ApplicationUserDTO;
import com.cryptotrading.dto.ApplicationUserHoldingDTO;
import com.cryptotrading.dto.TransactionDTO;
import com.cryptotrading.service.IApplicationUserService;
import com.cryptotrading.service.impl.ApplicationUserServiceImpl;
import com.cryptotrading.service.impl.KrakenSocketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class ApplicationUserController {
    private final IApplicationUserService applicationUserService;

    @Autowired
    ApplicationUserController(IApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    @PostMapping("/buy")
    public TransactionDTO buy(
            @RequestBody ApplicationUserDTO userDTO,
            @RequestParam String symbol,
            @RequestParam BigDecimal quantity
    ) {
        return applicationUserService.buy(userDTO, symbol, quantity);
    }

    @PostMapping("/sell")
    public TransactionDTO sell(
            @RequestBody ApplicationUserDTO userDTO,
            @RequestParam String symbol,
            @RequestParam BigDecimal quantity
    ) {
        return applicationUserService.sell(userDTO, symbol, quantity);
    }

    @GetMapping("/transactions/{userId}")
    public List<TransactionDTO> getPastTransactions(@PathVariable Long userId) {
        return applicationUserService.getPastTransactions(userId);
    }

    @GetMapping("/portfolio/{userId}")
    public List<ApplicationUserHoldingDTO> getUserPortfolio(@PathVariable Long userId){
        return applicationUserService.getPortfolio(userId);
    }
}
