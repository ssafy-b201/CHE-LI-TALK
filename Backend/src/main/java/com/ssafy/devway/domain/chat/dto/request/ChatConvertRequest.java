package com.ssafy.devway.domain.chat.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class ChatConvertRequest {

    String memberEmail;
    MultipartFile audioFile;

}
