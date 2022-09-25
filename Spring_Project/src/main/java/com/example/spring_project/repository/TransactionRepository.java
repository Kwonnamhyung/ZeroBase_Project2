package com.example.spring_project.repository;

import com.example.spring_project.domain.Account;
import com.example.spring_project.domain.AccountUser;
import com.example.spring_project.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    Optional<Transaction> findByTransactionId(String transactionId);

}
