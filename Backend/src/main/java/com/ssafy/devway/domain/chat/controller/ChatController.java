package com.ssafy.devway.domain.chat.controller;


import com.ssafy.devway.domain.chat.dto.request.ChatCheckRequestDto;
import com.ssafy.devway.domain.chat.dto.request.ChatDetailRequestDto;
import com.ssafy.devway.domain.chat.dto.request.ChatRequestDto;
import com.ssafy.devway.domain.chat.dto.response.ChatListDetailResponse;
import com.ssafy.devway.domain.chat.dto.response.ChatListResponse;
import com.ssafy.devway.domain.chat.entity.Chat;
import com.ssafy.devway.domain.chat.service.ChatService;
import com.ssafy.devway.global.api.ApiResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
    public List<ChatListResponse> chatList(@RequestParam String memberEmail){
        return chatService.chatList(memberEmail);
    }

    /**
     * 채팅 상세 조회-날짜누르면 대화 상세보기
     */
    @GetMapping("/list/detail")
    public List<ChatListDetailResponse> chatDetail(
        @RequestParam("memberEmail") String memberEmail,
        @RequestParam("createdAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAt)
        throws IOException {
        ChatDetailRequestDto dto = ChatDetailRequestDto.builder()
            .memberEmail(memberEmail)
            .createdAt(createdAt)
            .build();
        return chatService.chatDetail(dto);
    }

    /**
     * 채팅 문법 체크
     */
    @PostMapping("/check")
    public String chatCheck(@RequestBody ChatCheckRequestDto dto) throws IOException{
        return chatService.checkChat(dto);
    }

    /**
     * 최근 채팅 조회
     */
    @GetMapping("/recent")
    public List<ChatListDetailResponse> chatRecent(@RequestParam String memberEmail){
        return chatService.getRecent(memberEmail);
    }

    /**
     * 즐겨 찾기 상태 변화
     */
    @PostMapping("/like")
    public String likeChat(@RequestParam Long sentenceId){
        if(chatService.likeChat(sentenceId)){
            return "liked";
        }else{
            return "don't like";
        }
    }


}
