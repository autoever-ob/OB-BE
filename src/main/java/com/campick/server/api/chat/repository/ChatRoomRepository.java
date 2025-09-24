package com.campick.server.api.chat.repository;

import com.campick.server.api.chat.entity.ChatRoom;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.seller s " +
            "JOIN FETCH cr.buyer b " +
            "JOIN FETCH cr.product p " +
            "LEFT JOIN FETCH cr.seller.dealer d " + // 딜러 정보
            "WHERE cr.id = :chatRoomId")
    Optional<ChatRoom> findDetailById(@Param("chatRoomId") Long chatRoomId);

    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.product p " +
            "JOIN FETCH cr.seller s " +
            "JOIN FETCH cr.buyer b " +
            "WHERE (s.id = :memberId OR b.id = :memberId) " +
            "AND ((s.id = :memberId AND cr.isSellerOut = false) " +
            "     OR (b.id = :memberId AND cr.isBuyerOut = false))")
    List<ChatRoom> findAllByMemberId(@Param("memberId") Long memberId);

    @Query("""
        SELECT c FROM ChatRoom c
        WHERE c.seller.id = :memberId OR c.buyer.id = :memberId
        ORDER BY (
            SELECT MAX(m.createdAt) FROM ChatMessage m WHERE m.chatRoom = c
        ) DESC
    """)
    Page<ChatRoom> findByMemberIdOrderByLastMessageDesc(@Param("memberId") Long memberId, Pageable pageable);

    Optional<ChatRoom> findByProductAndSellerAndBuyer(Product product, Member seller, Member buyer);

    @Query("""
        SELECT COUNT(c) FROM ChatRoom c
        WHERE c.seller.id = :memberId OR c.buyer.id = :memberId
        """)
    long countChatRoomByMemberId(@Param("memberId") Long memberId);
}
