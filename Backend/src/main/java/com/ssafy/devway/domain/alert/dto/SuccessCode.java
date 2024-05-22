package com.ssafy.devway.domain.alert.dto;

public enum SuccessCode {
	SELECT_SUCCESS("200", "요청 성공"),
	UPDATE_SUCCESS("200", "업데이트 성공");

	private final String status;
	private final String message;

	SuccessCode(String status, String message) {
		this.status = status;
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}


}
