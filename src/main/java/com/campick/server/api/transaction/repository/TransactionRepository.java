package com.campick.server.api.transaction.repository;

import com.campick.server.api.member.entity.Member;
import com.campick.server.api.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findTransactionsByBuyer(Member buyer, Pageable pageable);

    Page<Transaction> findTransactionsBySeller(Member seller, Pageable pageable);
}
