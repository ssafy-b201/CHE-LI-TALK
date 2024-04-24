package com.ssafy.devway.domain.alert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseDto {
	//fcm에 실제 전송될 데이터의 dto
	private boolean validateOnly;
	private ResponseDto.Message message;

	@Builder
	@AllArgsConstructor
	@Getter
	public static class Message{
		private ResponseDto.Notification notification;
		private String token;
	}

	@Builder
	@AllArgsConstructor
	@Getter
	public static class Notification{
		private String title;
		private String body;
	}
}