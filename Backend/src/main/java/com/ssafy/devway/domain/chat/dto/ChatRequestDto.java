package com.ssafy.devway.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRequestDto {

    String memberEmail;
    String content;

}
