package com.cryptotrading.service;

import com.cryptotrading.api.Data;

import java.util.List;

public interface IKrakenSocketService {
    public Data getDataByName(final String name);

    public List<Data> getCurrenciesInformation();
}
