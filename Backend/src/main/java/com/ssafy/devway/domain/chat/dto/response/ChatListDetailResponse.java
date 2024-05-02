package com.ssafy.devway.domain.chat.dto.response;

import com.ssafy.devway.domain.chat.entity.Sentence;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatListDetailResponse {

	String sentenceSender;
	String sentenceContent;

}
