package com.ssafy.devway.domain.alert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssafy.devway.domain.alert.dto.ResponseDto;
import com.ssafy.devway.domain.alert.dto.RequestDto;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FcmService {

	//메시지를 구성하고 토큰을 받아서 fcm 으로 메시지 처리를 수행하는 비즈니스 로직
	public int sendMessageTo(RequestDto requestDto) throws IOException {
		String message = makeMessage(requestDto);
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + getAccessToken());

		HttpEntity entity = new HttpEntity<>(message, headers);

		String API_URL = "https://fcm.googleapis.com/v1/projects/devway-5fba5/messages:send";
		ResponseEntity response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
		System.out.println(response.getStatusCode());

		return response.getStatusCode() == HttpStatus.OK ? 1 : 0;
	}

	//FCM 전송 정보를 기반으로 메시지 구성하기
	private String makeMessage(RequestDto requestDto) throws JsonProcessingException {
		ObjectMapper om = new ObjectMapper();
		ResponseDto responseDto = ResponseDto.builder()
			.message(ResponseDto.Message.builder()
				.token(requestDto.getToken())
				.notification(ResponseDto.Notification.builder()
					.title(requestDto.getTitle())
					.body(requestDto.getBody())
					.build()
				).build()).validateOnly(false).build();

		return om.writeValueAsString(responseDto);
	}

	//Firebase Admin SDK의 비공개 키를 참조하여 Bearer 토큰 발급받기
	private String getAccessToken() throws IOException {
		String firebaseConfigPath = "firebase/firebase_settings.json";

		GoogleCredentials googleCredentials = GoogleCredentials
			.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
			.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

		googleCredentials.refreshIfExpired();
		return googleCredentials.getAccessToken().getTokenValue();
	}

}