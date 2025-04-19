import React, { useState, useEffect } from "react";

const API_URL = "http://localhost:8081";

export default function App() {
  const [user, setUser] = useState({ username: "", email: "", password: "" });
  const [loggedInUser, setLoggedInUser] = useState(null);
  const [view, setView] = useState("login"); // login, register, dashboard, prices
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




  // Handle input changes
  const handleInput = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  // POST function to interact with the backend
  const post = async (endpoint, body = user, method = "POST") => {
    const url = `${API_URL}${endpoint}`;
    const res = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });

    if (!res.ok) {
      const error = await res.text();
      throw new Error(error || "Request failed");
    }

    return res.json();
  };

  // Login function
  const login = async () => {
    const data = await post("/api/auth/login");
    if (data.username) {
      setLoggedInUser(data); // Store logged-in user info
      setBalance(parseFloat(data.balance).toFixed(2));
      localStorage.setItem("token", data.token); // Store the token in localStorage
      setView("dashboard");
      fetchPortfolio(data); // Fetch the user's portfolio after login
      fetchHoldings(); // Fetch holdings after login
      fetchUserPortfolio(data.id);
    }
  };

  const fetchUserPortfolio = async (userId) => {
    const res = await fetch(`${API_URL}/api/user/portfolio/${userId}`);
    const data = await res.json();
    setPortfolio(data);
  };
  

  // Register function
  const register = async () => {
    const data = await post("/api/auth/register");
    if (data.username) {
      alert("Registration successful");
      setView("login");
    }
  };

  // Restart function
  const restart = async () => {
    const data = await post("/api/auth/restart", loggedInUser);
    if (data) {
      setLoggedInUser(data);
      setBalance(parseFloat(data.balance).toFixed(2));
      fetchPortfolio(data);
    }
  };

  // Fetch the user's portfolio transactions
  const fetchPortfolio = async (user) => {
    try {
      const res = await fetch(`${API_URL}/api/user/transactions/${user.id}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
        },
      });
  
      if (!res.ok) {
        throw new Error("Failed to fetch transactions");
      }
      

      const data = await res.json();
      setTransactions(data.reverse());

      console.log("Transactions Data: ", data); 


      if (data.length > 0) {
        setBalance(parseFloat(data[data.length - 1].user.balance).toFixed(2));
      }
    } catch (error) {
      console.error("Error fetching portfolio:", error);
    }
  };

  // Fetch live prices for a currency symbol
  const fetchAllPrices = async () => {
    try {
      const res = await fetch(`${API_URL}/api/kraken/currencies`);
      const data = await res.json();
      setAllPrices(data);
    } catch (err) {
      console.error("Error fetching all prices:", err);
    }
  };
  
  const fetchHoldings = async () => {
    const token = localStorage.getItem("token"); // Get the token from localStorage
    if (!token) {
      alert("You need to log in first!");
      return;
    }

    const user = { ...loggedInUser }; // Assuming 'loggedInUser' contains the user info

    const res = await fetch(`${API_URL}/api/user/transactions`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`, // Include the token in the header
      },
      body: JSON.stringify(user), // Send the user object in the body
    });

    if (!res.ok) {
      console.error("Failed to fetch holdings:", res.statusText);
      return;   
    }

    const data = await res.json();
    setHoldings(data); // Set holdings data
  };


  // Handle trade actions (buy/sell)
  const trade = async (type) => {
    const res = await fetch(`${API_URL}/api/user/${type}?symbol=${symbol}&quantity=${quantity}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(loggedInUser),
    });

    const data = await res.json();
    if (data.user) {
      setLoggedInUser(data.user);
      setBalance(parseFloat(data.user.balance).toFixed(2));
    }

    fetchPortfolio(data.user || loggedInUser);
    alert(`${type.toUpperCase()} successful`);
  };

  // Display the UI based on the view state
  if (view === "login") {
    return (
      <div className="container">
        <h2>Login</h2>
        <input name="username" placeholder="Username" onChange={handleInput} />
        <input name="password" type="password" placeholder="Password" onChange={handleInput} />
        <button onClick={login}>Login</button>
        <p>Don't have an account? <button onClick={() => setView("register")}>Register</button></p>
      </div>
    );
  }

  if (view === "register") {
    return (
      <div className="container">
        <h2>Register</h2>
        <input name="username" placeholder="Username" onChange={handleInput} />
        <input name="email" placeholder="Email" onChange={handleInput} />
        <input name="password" type="password" placeholder="Password" onChange={handleInput} />
        <button onClick={register}>Register</button>
        <p>Already have an account? <button onClick={() => setView("login")}>Login</button></p>
      </div>
    );
  }

  if (view === "prices") {
    const [liveSymbol, setLiveSymbol] = useState("");
    const [livePriceData, setLivePriceData] = useState(null);

    const getPrice = async () => {
      const data = await fetchLivePrice(liveSymbol);
      setLivePriceData(data);
    };

    return (
      <div className="container">
        <h2>Cryptocurrency Prices</h2>
        <div>
          <input
            placeholder="Enter symbol (e.g. BTCUSD)"
            value={liveSymbol}
            onChange={(e) => setLiveSymbol(e.target.value)}
          />
          <button onClick={getPrice}>Get Price</button>
        </div>

        {livePriceData && (
          <div style={{ marginTop: "20px" }}>
            <h3>{livePriceData.symbol}</h3>
            <p>Ask: ${livePriceData.ask}</p>
            <p>Bid: ${livePriceData.bid}</p>
            <p>Price: ${livePriceData.price}</p>
          </div>
        )}

        <button onClick={() => setView("dashboard")}>Back to Portfolio</button>
      </div>
    );
  }

  return (
    <div className="container">
      <h1>Welcome, {loggedInUser.username}</h1>
      <p>Balance: ${balance}</p>
  
      {/* Section for transaction input */}
      <div>
        <input placeholder="Symbol" onChange={(e) => setSymbol(e.target.value)} />
        <input type="number" placeholder="Quantity" onChange={(e) => setQuantity(e.target.value)} />
        <button onClick={() => trade("buy")}>Buy</button>
        <button onClick={() => trade("sell")}>Sell</button>
      </div>
  
      <button
  onClick={() => {
    if (!showPrices) fetchAllPrices();
    setShowPrices(!showPrices);
  }}
>
  {showPrices ? "Hide Prices" : "See Prices"}
</button>

      <button onClick={restart}>Reset Account</button>

      {showPrices && (
  <div style={{ marginTop: "20px" }}>
    <h2>Live Prices</h2>
    {allPrices.length === 0 ? (
      <p>Loading prices...</p>
    ) : (
      <ul>
        {allPrices
          .filter(item => item !== null) // remove nulls
          .map((item, idx) => (
            <li key={idx}>
              {item.symbol}: {item.ask != null ? `$${parseFloat(item.ask).toFixed(4)}` : "N/A"}
            </li>
        ))}
      </ul>
    )}
  </div>
)}
  

      <h2>Your Portfolio</h2>
{portfolio.length === 0 ? (
  <p>No portfolio data available.</p>
) : (
  <ul>
    {portfolio.map((item, idx) => (
      <li key={idx}>
        {item.symbol}: {parseFloat(item.quantity).toFixed(6)}
      </li>
    ))}
  </ul>
)}

      {/* Section for viewing transactions */}
      <h2>Transactions</h2>
  
      {/* Button to fetch all transactions */}
      <button
  onClick={async () => {
    if (!showTransactions) {
      await fetchPortfolio(loggedInUser); // Only fetch if not already visible
    }
    setShowTransactions(!showTransactions);
  }}
>
  {showTransactions ? "Hide Transactions" : "See All Transactions"}
</button>
  
      {showTransactions && (
      <ul>
        {transactions.map((t, i) => (
          <li key={i}>
            {t.bought ? "Bought" : "Sold"} - {t.ticker} 
            {" "}Quantity : {t.quantity} For ${(parseFloat(t.quantity) * parseFloat(t.price)).toFixed(2)} At Price: ${parseFloat(t.price).toFixed(2)} 

            <br />
            <small>Time: {new Date(t.time).toLocaleString()}</small>
          </li>
        ))}
      </ul>
      )}
    </div>
  );
}
	