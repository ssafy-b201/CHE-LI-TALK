package com.ssafy.devway.domain.chat.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatCheckRequestDto {

	String memberEmail;
	Long chatId;
}
