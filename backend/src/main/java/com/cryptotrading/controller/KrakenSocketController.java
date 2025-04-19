package com.cryptotrading.controller;

import com.cryptotrading.api.Data;
import com.cryptotrading.service.IKrakenSocketService;
import com.cryptotrading.service.impl.KrakenSocketServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/kraken")
@RequiredArgsConstructor
public class KrakenSocketController {

    private final KrakenSocketServiceImpl krakenSocketService;

    @GetMapping("/data")
    public Data getDataByName(@RequestParam("symbol") String symbol) {
        Data data = krakenSocketService.getDataByName(symbol.toUpperCase());

        if (data == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data found for symbol: " + symbol);
        }

        return data;
    }

    @GetMapping("/currencies")
    public List<Data> getAllCurrencies(){
        List<Data> currencies = krakenSocketService.getCurrenciesInformation();

        if(currencies == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data found for the currencies");
        }

        return currencies;
    }
}

