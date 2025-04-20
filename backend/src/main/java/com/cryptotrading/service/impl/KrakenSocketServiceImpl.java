package com.cryptotrading.service.impl;

import com.cryptotrading.api.Data;
import com.cryptotrading.service.IKrakenSocketService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@Component
public class KrakenSocketServiceImpl implements IKrakenSocketService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(KrakenSocketServiceImpl.class);
    private static final String KRAKEN_WS_URL = "wss://ws.kraken.com/v2";
    private final Map<String, Data> latestPrices = new HashMap<>();

    private final List<String> pairs = List.of("BTC/USD", "ETH/USD", "USDT/USD", "SOL/USD", "XRP/USD",
            "BNB/USD", "USDc/USD" , "DOGE/USD", "TRX/USD", "ADA/USD"
            , "LEO/USD", "LINK/USD", "ACAX/USD", "XLM/USD", "TON/USD",
            "SHIB/USD", "HBAR/USD", "SUI/USD", "BCH/USD", "HYPE/USD"
    );

    @Override
    public Data getDataByName(final String name) {
        return latestPrices.get(name);
    }

    @Override
    public List<Data> getCurrenciesInformation() {
        ArrayList<Data> currencies = new ArrayList<>();

        for(var pair : pairs){
            currencies.add(getDataByName(pair));
        }

        return currencies;
    }


    @PostConstruct
    public void connect() {
        HttpClient client = HttpClient.newHttpClient();

        client.newWebSocketBuilder()
                .buildAsync(URI.create(KRAKEN_WS_URL), new WebSocket.Listener() {

                    @Override
                    public void onOpen(WebSocket webSocket) {
                        log.info("🔌 Connected to Kraken WebSocket");

                        String symbolsArray = pairs.stream()
                                .map(pair -> "\"" + pair + "\"")
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("");

                        String subscription = """
                            {
                              "method": "subscribe",
                              "params": {
                                "channel": "ticker",
                                "symbol": [%s]
                              }
                            }
                            """.formatted(symbolsArray);

                        webSocket.sendText(subscription, true);

                        WebSocket.Listener.super.onOpen(webSocket);
                    }

                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        log.info("Received: {}", data);

                        try {
                            JsonNode node = objectMapper.readTree(data.toString());
                            if ("ticker".equals(node.path("channel").asText()) && "update".equals(node.path("type").asText())) {
                                JsonNode tickerData = node.path("data").get(0);

                                Data parsed = new Data(
                                        (float) tickerData.path("ask").asDouble(),
                                        (float) tickerData.path("ask_qty").asDouble(),
                                        (float) tickerData.path("bid").asDouble(),
                                        (float) tickerData.path("bid_qty").asDouble(),
                                        (float) tickerData.path("change").asDouble(),
                                        (float) tickerData.path("change_pct").asDouble(),
                                        (float) tickerData.path("high").asDouble(),
                                        (float) tickerData.path("last").asDouble(),
                                        (float) tickerData.path("low").asDouble(),
                                        tickerData.path("symbol").asText(),
                                        (float) tickerData.path("volume").asDouble(),  // Convert double to Float
                                        (float) tickerData.path("vwap").asDouble()
                                );
                                latestPrices.put(parsed.symbol(), parsed);
                            }
                        } catch (Exception e) {
                            log.error("Failed to parse ticker message", e);
                        }


                        return WebSocket.Listener.super.onText(webSocket, data, last);
                    }

                    @Override
                    public void onError(WebSocket webSocket, Throwable error) {
                        log.error("WebSocket error: {}", error.getMessage(), error);
                    }
                });
    }
}
