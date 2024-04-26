package com.ssafy.devway.domain.member.controller;

import com.ssafy.devway.domain.member.dto.request.MemberSignupRequest;
import com.ssafy.devway.domain.member.service.MemberService;
import com.ssafy.devway.global.api.ApiResponse;
import com.ssafy.devway.global.api.ApiResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody MemberSignupRequest request){

        memberService.signup(request);

        return ApiResponse.ok(request.getMemberNickname());
    }

}
