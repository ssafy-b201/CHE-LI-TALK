package com.ssafy.devway.domain.alert.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponseWrapper<T> {
	private T result;
	private String resultCode;
	private String resultMsg;
}
