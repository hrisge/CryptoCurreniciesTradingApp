import React, { useEffect, useState } from "react";
import ReactDOM from "react-dom/client";

function App() {
  const [cryptos, setCryptos] = useState([]);
  const [balance, setBalance] = useState(0);
  const [holdings, setHoldings] = useState({});
  const [history, setHistory] = useState([]);
  const [selectedCrypto, setSelectedCrypto] = useState("");
  const [amount, setAmount] = useState("");

  // Load initial account info
  useEffect(() => {
    fetch("/api/account").then(res => res.json()).then(data => {
      setBalance(data.balance);
      setHoldings(data.holdings);
    });

    fetch("/api/history").then(res => res.json()).then(data => setHistory(data));
  }, []);

  // Kraken WebSocket connection
  useEffect(() => {
    const ws = new WebSocket("wss://ws.kraken.com/v2");
    ws.onopen = () => {
      ws.send(JSON.stringify({
        method: "subscribe",
        params: {
          channel: "ticker",
          symbols: ["BTC/USD", "ETH/USD", "USDT/USD", "SOL/USD", "XRP/USD",
                                "BNB/USD", "USDc/USD" , "DOGE/USD", "TRX/USD", "ADA/USD"
                                , "LEO/USD", "LINK/USD", "ACAX/USD", "XLM/USD", "TON/USD",
                                "SHIB/USD", "HBAR/USD", "SUI/USD", "BCH/USD", "HYPE/USD"]
        }
      }));
    };

    ws.onmessage = (msg) => {
      const data = JSON.parse(msg.data);
      if (data.channel === "ticker" && data.symbol) {
        setCryptos((prev) => {
          const updated = [...prev.filter(c => c.symbol !== data.symbol), {
            symbol: data.symbol,
            price: parseFloat(data.data.a[0])
          }];
          return updated.sort((a, b) => a.symbol.localeCompare(b.symbol));
        });
      }
    };

    return () => ws.close();
  }, []);

  const handleBuy = () => {
    const quantity = parseFloat(amount);
    if (quantity <= 0) return alert("Invalid amount");

    fetch("/api/buy", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ symbol: selectedCrypto, amount: quantity })
    })
      .then(res => res.json())
      .then(data => {
        setBalance(data.balance);
        setHoldings(data.holdings);
        setHistory(data.history);
        setAmount("");
      });
  };

  const handleSell = () => {
    const quantity = parseFloat(amount);
    if (quantity <= 0) return alert("Invalid amount");

    fetch("/api/sell", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ symbol: selectedCrypto, amount: quantity })
    })
      .then(res => res.json())
      .then(data => {
        setBalance(data.balance);
        setHoldings(data.holdings);
        setHistory(data.history);
        setAmount("");
      });
  };

  const handleReset = () => {
    fetch("/api/reset", { method: "POST" })
      .then(res => res.json())
      .then(data => {
        setBalance(data.balance);
        setHoldings({});
        setHistory([]);
      });
  };

  return (
    <div className="container">
      <h1>Crypto Trading Simulator</h1>

      <section>
        <h2>Top Cryptos</h2>
        <table>
          <thead>
            <tr><th>Symbol</th><th>Price (USD)</th></tr>
          </thead>
          <tbody>
            {cryptos.map(c => (
              <tr key={c.symbol}>
                <td>{c.symbol}</td>
                <td>${c.price.toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>

      <section>
        <h2>Buy / Sell</h2>
        <div className="trade-controls">
          <select value={selectedCrypto} onChange={e => setSelectedCrypto(e.target.value)}>
            <option value="">Select Crypto</option>
            {cryptos.map(c => <option key={c.symbol} value={c.symbol}>{c.symbol}</option>)}
          </select>
          <input type="number" value={amount} onChange={e => setAmount(e.target.value)} placeholder="Amount" />
          <button onClick={handleBuy}>Buy</button>
          <button onClick={handleSell}>Sell</button>
          <button onClick={handleReset}>Reset</button>
        </div>
        <p><strong>Balance:</strong> ${balance.toFixed(2)}</p>
      </section>

      <section>
        <h2>Transaction History</h2>
        <ul className="history">
          {history.map((h, i) => (
            <li key={i}>
              [{new Date(h.timestamp).toLocaleString()}] {h.type} {h.quantity} {h.symbol} @ ${h.price}
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);
