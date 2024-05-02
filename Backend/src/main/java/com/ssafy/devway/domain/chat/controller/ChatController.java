package com.ssafy.devway.domain.chat.controller;


import com.ssafy.devway.domain.chat.dto.request.ChatCheckRequestDto;
import com.ssafy.devway.domain.chat.dto.request.ChatDetailRequestDto;
import com.ssafy.devway.domain.chat.dto.request.ChatRequestDto;
import com.ssafy.devway.domain.chat.dto.response.ChatListDetailResponse;
import com.ssafy.devway.domain.chat.dto.response.ChatListResponse;
import com.ssafy.devway.domain.chat.entity.Chat;
import com.ssafy.devway.domain.chat.service.ChatService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cherry/api/chat")
public class ChatController {

    private final ChatService chatService;

    /**
     * 채팅 시작
     */
    @PostMapping("/begin")
    public String beginChatting(@RequestBody ChatRequestDto dto) throws IOException {
        return chatService.beginChatting(dto);
    }

    /**
     * 채팅 전송
     */
    @PostMapping
    public String sendQuestion(@RequestBody ChatRequestDto dto) throws IOException {
        return chatService.continueChatting(dto);
    }

    /**
     * 채팅 목록 조회-날짜반환
     */
    @GetMapping("/list")
    public List<ChatListResponse> chatList(@RequestParam String memberEmail) {
        return chatService.chatList(memberEmail);
    }

    /**
     * 채팅 상세 조회-날짜누르면 대화 상세보기
     */
    @GetMapping("/list/detail")
    public List<ChatListDetailResponse> chatDetail(@RequestBody ChatDetailRequestDto dto)
        throws IOException {
        return chatService.chatDetail(dto);
    }

    /**
     * 채팅 문법 체크
     */
    @GetMapping("/check")
    public String chatCheck(@RequestBody ChatCheckRequestDto dto) throws IOException {
        return chatService.checkChat(dto);
    }
}
