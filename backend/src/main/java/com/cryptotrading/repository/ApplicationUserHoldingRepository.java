package com.cryptotrading.repository;

import com.cryptotrading.entity.ApplicationUserHolding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationUserHoldingRepository extends JpaRepository<ApplicationUserHolding, Long> {
    void deleteAllByUser_Id(Long userId);

    Optional<ApplicationUserHolding> findByUserIdAndSymbol(Long userId, String symbol);

    List<ApplicationUserHolding> findByUserId(Long userId);
}
