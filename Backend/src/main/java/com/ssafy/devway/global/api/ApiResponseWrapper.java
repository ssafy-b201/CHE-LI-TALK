package com.ssafy.devway.global.api;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponseWrapper<T> {
	private T result;
	private String resultCode;
	private String resultMsg;
}

