package com.ssafy.devway.domain.chat.dto.request;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatDetailRequestDto {

	String memberEmail;
	LocalDateTime createdAt;

}
