package com.ssafy.devway.domain.alert.controller;

import com.ssafy.devway.domain.alert.dto.RequestDto;
import com.ssafy.devway.domain.alert.dto.SuccessCode;
import com.ssafy.devway.domain.alert.service.FcmService;
import com.ssafy.devway.global.api.ApiResponseWrapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {

	private final FcmService fcmService;

	@PostMapping("/send")
	public ResponseEntity<ApiResponseWrapper<Object>> pushMessage(@RequestBody @Validated RequestDto requestDto) throws IOException {
		log.debug("푸시 메시지를 전송합니다: {}", requestDto);
		int result = fcmService.sendMessageTo(requestDto);

		ApiResponseWrapper<Object> arw = ApiResponseWrapper
			.builder()
			.result(result)
			.resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
			.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
			.build();

		return new ResponseEntity<>(arw, HttpStatus.OK);
	}
}
