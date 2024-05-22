package com.ssafy.devway.domain.alert.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestDto {
	//모바일에서 전달받은 객체를 매핑하는 dto
	private String token;
	private String title;
	private String body;

	@Builder(toBuilder = true)
	public RequestDto(String token, String title, String body){
		this.token = token;
		this.title = title;
		this.body = body;
	}
}
