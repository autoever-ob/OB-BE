package com.campick.server.api.chat.repository;

import com.campick.server.api.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT cm FROM ChatMessage cm " +
            "JOIN FETCH cm.member m " +
            "WHERE cm.chatRoom.id = :chatRoomId " +
            "ORDER BY cm.createdAt ASC")
    List<ChatMessage> findMessagesByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Modifying
    @Query("UPDATE ChatMessage m " +
            "SET m.isRead = true " +
            "WHERE m.chatRoom.id = :chatRoomId " +
            "AND m.member.id <> :memberId " +
            "AND m.isRead = false")
    Integer markMessagesAsRead(@Param("chatRoomId") Long chatRoomId,
                           @Param("memberId") Long memberId);


    @Query("SELECT m FROM ChatMessage m " +
            "WHERE m.chatRoom.id = :chatRoomId " +
            "ORDER BY m.createdAt DESC LIMIT 1")
    ChatMessage findLastMessageByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("""
        SELECT m FROM ChatMessage m
        WHERE m.chatRoom.id IN :chatRoomIds
        AND m.createdAt IN (
            SELECT MAX(m2.createdAt) FROM ChatMessage m2 WHERE m2.chatRoom.id = m.chatRoom.id
        )
    """)
    List<ChatMessage> findLastMessages(@Param("chatRoomIds") List<Long> chatRoomIds);

    @Query("SELECT COUNT(m) FROM ChatMessage m " +
            "WHERE m.chatRoom.id = :chatRoomId " +
            "AND m.isRead = false " +
            "AND m.member.id <> :memberId") // 내가 보낸 메시지는 제외
    Integer countUnreadMessages(@Param("chatRoomId") Long chatRoomId,
                             @Param("memberId") Long memberId);

    @Query("SELECT COUNT(cm) " +
            "FROM ChatMessage cm " +
            "WHERE (cm.chatRoom.seller.id = :memberId OR cm.chatRoom.buyer.id = :memberId) " +
            "AND cm.isRead = false " +
            "AND cm.member.id <> :memberId")
    Integer countAllUnreadMessages(@Param("memberId") Long memberId);

    Page<ChatMessage> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable);
}
