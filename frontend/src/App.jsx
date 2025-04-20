import React, { useState, useEffect } from "react";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "bootstrap/dist/css/bootstrap.min.css";

const API_URL = "http://localhost:8081";

export default function App() {
  const [user, setUser] = useState({ username: "", email: "", password: "" });
  const [loggedInUser, setLoggedInUser] = useState(null);
  const [view, setView] = useState("login");
  const [balance, setBalance] = useState(null);
  const [holdings, setHoldings] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [symbol, setSymbol] = useState("");
  const [quantity, setQuantity] = useState(0);
  const [prices, setPrices] = useState([]);
  const [showTransactions, setShowTransactions] = useState(false);
  const [portfolio, setPortfolio] = useState([]);
  const [showPrices, setShowPrices] = useState(false);
  const [allPrices, setAllPrices] = useState([]);

  const handleInput = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const post = async (endpoint, body = user, method = "POST") => {
    const url = `${API_URL}${endpoint}`;
    const res = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });

    if (!res.ok) {
      const error = await res.text();
      toast.error(error || "Request failed");
      throw new Error(error || "Request failed");
    }

    return res.json();
  };

  const login = async () => {
    try {
      const data = await post("/api/auth/login");
      if (data.username) {
        setLoggedInUser(data);
        setBalance(parseFloat(data.balance).toFixed(2));
        localStorage.setItem("token", data.token);
        setView("dashboard");
        fetchPortfolio(data);
        fetchUserPortfolio(data.id);
        fetchAllPrices();
        toast.success("Login successful");
      }
    } catch (err) {
      toast.error("Login failed");
    }
  };

  const fetchUserPortfolio = async (userId) => {
    const res = await fetch(`${API_URL}/api/user/portfolio/${userId}`);
    const data = await res.json();
    setPortfolio(data);
  };

  const register = async () => {
    try {
      const data = await post("/api/auth/register");
      if (data.username) {
        toast.success("Registration successful");
        setView("login");
      }
    } catch (err) {
      toast.error("Registration failed");
    }
  };

  const restart = async () => {
    try {
      const data = await post("/api/auth/restart", loggedInUser);
      if (data) {
        setLoggedInUser(data);
        setBalance(parseFloat(data.balance).toFixed(2));
        setPortfolio([]);
        setHoldings([]);
        setTransactions([]);
        toast.success("Account reset successful");
      }
    } catch (err) {
      toast.error("Account reset failed");
    }
  };

  const fetchPortfolio = async (user) => {
    try {
      const res = await fetch(`${API_URL}/api/user/transactions/${user.id}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
        },
      });

      if (!res.ok) throw new Error("Failed to fetch transactions");

      const data = await res.json();
      setTransactions(data.reverse());

      if (data.length > 0) {
        setBalance(parseFloat(data[data.length - 1].user.balance).toFixed(2));
      } else {
        setBalance(parseFloat(user.balance).toFixed(2));
      }
    } catch (error) {
      console.error("Error fetching portfolio:", error);
      toast.error("Failed to fetch portfolio");
    }
  };

  const fetchAllPrices = async () => {
    try {
      const res = await fetch(`${API_URL}/api/kraken/currencies`);
      const data = await res.json();
      setAllPrices(data);
    } catch (err) {
      console.error("Error fetching all prices:", err);
      toast.error("Failed to fetch prices");
    }
  };

  const logout = () => {
    setLoggedInUser(null);
    setView("login");
    localStorage.removeItem("token");
    toastr.success("Logged out successfully");
  };
  

  const trade = async (type) => {
    try {
      const res = await fetch(`${API_URL}/api/user/${type}?symbol=${symbol}&quantity=${quantity}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loggedInUser),
      });
  
      if (!res.ok) {
        const errorMsg = await res.text();
        throw new Error(errorMsg || `Failed to ${type}`);
      }
  
      const data = await res.json();
  
      // You can log or inspect what the actual response is:
      console.log("Trade response:", data);
  
      if (data && data.user) {
        setLoggedInUser(data.user);
        setBalance(parseFloat(data.user.balance).toFixed(2));
        await fetchPortfolio(data.user || loggedInUser);
        await fetchUserPortfolio(data.user.id);
        toast.success(`${type.toUpperCase()} successful`);
      } else {
        toast.success(`${type.toUpperCase()} possibly successful`);
        await fetchPortfolio(loggedInUser);
      }
    } catch (err) {
      console.error(`Error during ${type}:`, err);
      toast.error(err.message || `Failed to ${type}`);
    }
  };
  

  if (view === "login") {
    return (
      <div className="container mt-5">
        <ToastContainer />
        <h2>Login</h2>
        <input name="username" className="form-control my-2" placeholder="Username" onChange={handleInput} />
        <input name="password" type="password" className="form-control my-2" placeholder="Password" onChange={handleInput} />
        <button className="btn btn-primary" onClick={login}>Login</button>
        <p className="mt-2">Don't have an account? <button className="btn btn-link" onClick={() => setView("register")}>Register</button></p>
      </div>
    );
  }

  if (view === "register") {
    return (
      <div className="container mt-5">
        <ToastContainer />
        <h2>Register</h2>
        <input name="username" className="form-control my-2" placeholder="Username" onChange={handleInput} />
        <input name="email" className="form-control my-2" placeholder="Email" onChange={handleInput} />
        <input name="password" type="password" className="form-control my-2" placeholder="Password" onChange={handleInput} />
        <button className="btn btn-success" onClick={register}>Register</button>
        <p className="mt-2">Already have an account? <button className="btn btn-link" onClick={() => setView("login")}>Login</button></p>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <ToastContainer />
      <h1 className="mb-3">Welcome, {loggedInUser.username}</h1>
      <div className="d-flex justify-content-end">
  <button className="btn btn-outline-danger mb-3" onClick={logout}>
    Logout
  </button>
</div>
      <p className="lead">Balance: ${balance}</p>

      <div className="mb-4">
        <div className="row g-2 align-items-center">
          <div className="col-auto">
            <label className="form-label">Symbol</label>
            <select
              className="form-select"
              value={symbol}
              onChange={(e) => setSymbol(e.target.value)}
            >
              <option value="">Select Symbol</option>
              {allPrices
                .filter((item) => item && item.symbol)
                .map((item, idx) => (
                  <option key={idx} value={item.symbol}>
                    {item.symbol}
                  </option>
                ))}
            </select>
          </div>

          <div className="col-auto">
            <label className="form-label">Quantity</label>
            <input
              type="number"
              className="form-control"
              placeholder="Quantity"
              value={quantity}
              onChange={(e) => setQuantity(e.target.value)}
            />
          </div>

          <div className="col-auto">
            <button className="btn btn-success me-2" onClick={() => trade("buy")}>
              Buy
            </button>
            <button className="btn btn-danger" onClick={() => trade("sell")}>
              Sell
            </button>
          </div>
        </div>
      </div>

      <button
        className="btn btn-secondary me-2"
        onClick={() => {
          if (!showPrices) fetchAllPrices();
          setShowPrices(!showPrices);
        }}
      >
        {showPrices ? "Hide Prices" : "See Prices"}
      </button>

      <button className="btn btn-warning" onClick={restart}>Reset Account</button>

      {showPrices && (
        <div className="mt-4">
          <h4>Live Prices</h4>
          {allPrices.length === 0 ? (
            <p>Loading prices...</p>
          ) : (
            <ul className="list-group">
              {allPrices
                .filter(item => item && item.symbol)
                .map((item, idx) => (
                  <li key={idx} className="list-group-item">
                    {item.symbol}: {item.ask != null ? `$${parseFloat(item.ask).toFixed(4)}` : "N/A"}
                  </li>
                ))}
            </ul>
          )}
        </div>
      )}

      <h3 className="mt-4">Your Portfolio</h3>
      {portfolio.length === 0 ? (
        <p>No portfolio data available.</p>
      ) : (
        <ul className="list-group">
          {portfolio.map((item, idx) => (
            <li key={idx} className="list-group-item">
              {item.symbol}: {parseFloat(item.quantity).toFixed(6)}
            </li>
          ))}
        </ul>
      )}

      <h3 className="mt-4">Transactions</h3>
      <button
        className="btn btn-outline-primary mb-2"
        onClick={async () => {
          if (!showTransactions) await fetchPortfolio(loggedInUser);
          setShowTransactions(!showTransactions);
        }}
      >
        {showTransactions ? "Hide Transactions" : "See All Transactions"}
      </button>

      {showTransactions && (
        <ul className="list-group">
          {transactions.map((t, i) => (
            <li key={i} className="list-group-item">
              <strong>{t.bought ? "Bought" : "Sold"}</strong> {t.ticker} — Qty: {t.quantity}, Total: ${(t.quantity * t.price).toFixed(2)}, Price: ${parseFloat(t.price).toFixed(2)}
              <br />
              <small>Time: {new Date(t.time).toLocaleString()}</small>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}