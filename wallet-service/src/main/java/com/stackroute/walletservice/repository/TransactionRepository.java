package com.stackroute.walletservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.stackroute.walletservice.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
}

