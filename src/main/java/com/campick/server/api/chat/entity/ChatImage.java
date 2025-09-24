package com.campick.server.api.chat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_image")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatRoom chatRoom;
    
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
}
