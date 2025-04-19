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
@Table(name = "holding")
public class ApplicationUserHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private BigDecimal quantity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(final String symbol) {
        if(symbol == null){
            throw new IllegalArgumentException("Symbol is null");
        }

        this.symbol = symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
    public void setQuantity(final BigDecimal quantity) {
        if(quantity == null || quantity.signum() < 0){
            throw new IllegalArgumentException("The quantity is either null or negative");
        }

        this.quantity = quantity;
    }

    public ApplicationUser getUser() {
        return user;
    }
    public void setUser(ApplicationUser user) {
        this.user = user;
    }
}
