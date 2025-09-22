package com.campick.server.api.chat.repository;

import com.campick.server.api.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.seller s " +
            "JOIN FETCH cr.buyer b " +
            "JOIN FETCH cr.product p " +
            "LEFT JOIN FETCH cr.seller.dealer d " + // 딜러 정보
            "WHERE cr.id = :chatRoomId")
    Optional<ChatRoom> findDetailById(@Param("chatRoomId") Long chatRoomId);
}
