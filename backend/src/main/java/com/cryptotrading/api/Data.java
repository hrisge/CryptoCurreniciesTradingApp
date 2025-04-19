package com.cryptotrading.api;

public record Data(
        Float ask,
        Float ask_qty,
        Float bid,
        Float bid_qty,
        Float change,
        Float change_pct,
        Float high,
        Float last,
        Float low,
        String symbol,
        Float volume,
        Float vwap
) {
}
