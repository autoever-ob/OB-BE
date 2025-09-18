package com.campick.server.api.transaction.repository;

import com.campick.server.api.member.dto.TransactionResponseDto;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.product.entity.Product;
import com.campick.server.api.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTransactionsByBuyer(Member buyer);

    List<Transaction> findTransactionsBySeller(Member seller);
}
