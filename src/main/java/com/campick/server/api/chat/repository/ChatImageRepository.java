package com.campick.server.api.chat.repository;

import com.campick.server.api.chat.entity.ChatImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatImageRepository extends JpaRepository<ChatImage, Long> {
}
