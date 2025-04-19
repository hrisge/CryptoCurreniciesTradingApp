package com.cryptotrading.repository;

import com.cryptotrading.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUser_Id(Long userId);

    void deleteAllByUser_Id(Long userId);
}
