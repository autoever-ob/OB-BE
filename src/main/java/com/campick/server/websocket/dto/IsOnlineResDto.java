package com.campick.server.websocket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class IsOnlineResDto {
    private List<IsOnline> online;
}
