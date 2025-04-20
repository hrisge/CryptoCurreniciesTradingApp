package com.cryptotrading.controller;

import com.cryptotrading.dto.ApplicationUserDTO;
import com.cryptotrading.dto.ApplicationUserHoldingDTO;
import com.cryptotrading.dto.TransactionDTO;
import com.cryptotrading.service.IApplicationUserService;
import com.cryptotrading.service.impl.ApplicationUserServiceImpl;
import com.cryptotrading.service.impl.KrakenSocketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> buy(
            @RequestBody ApplicationUserDTO userDTO,
            @RequestParam String symbol,
            @RequestParam BigDecimal quantity
    ) {
        try{
            TransactionDTO toReturn = applicationUserService.buy(userDTO, symbol, quantity);
            return ResponseEntity.ok(toReturn);
        }catch(IllegalArgumentException | NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters!");
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PostMapping("/sell")
    public ResponseEntity<?> sell(
            @RequestBody ApplicationUserDTO userDTO,
            @RequestParam String symbol,
            @RequestParam BigDecimal quantity
    ) {
        try{
            TransactionDTO toReturn = applicationUserService.sell(userDTO, symbol, quantity);
            return ResponseEntity.ok(toReturn);
        }catch(IllegalArgumentException | NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters!");
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<?> getPastTransactions(@PathVariable Long userId) {
        try {
            List<TransactionDTO> toReturn = applicationUserService.getPastTransactions(userId);
            return ResponseEntity.ok(toReturn);
        }catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Parameters!");
        }
    }

    @GetMapping("/portfolio/{userId}")
    public ResponseEntity<?> getUserPortfolio(@PathVariable Long userId){
        try{
            List<ApplicationUserHoldingDTO> toReturn = applicationUserService.getPortfolio(userId);
            return ResponseEntity.ok(toReturn);
        }catch(IllegalArgumentException | NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters!");
        }
    }
}
