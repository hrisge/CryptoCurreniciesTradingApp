package com.cryptotrading.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "application_user")
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(final String username) {
        if(username == null){
            throw new IllegalArgumentException("Username is null");
        }

        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(final String email) {
        if(email == null){
            throw new IllegalArgumentException("Email is null");
        }

        this.email = email;
    }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(final BigDecimal balance) {
        if(balance == null || balance.signum() < 0) {
            throw new IllegalArgumentException("Balance is null or negative");
        }

        this.balance = balance;
    }

    public void setPassword(final String password){
        if(password == null){
            throw new IllegalArgumentException("Password is null");
        }

        this.password = password;
    }
    public String getPassword(){ return password;}
}
