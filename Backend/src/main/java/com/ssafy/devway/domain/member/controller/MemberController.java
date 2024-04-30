package com.ssafy.devway.domain.member.controller;

import com.ssafy.devway.domain.attend.service.AttendService;
import com.ssafy.devway.domain.member.dto.request.MemberSignupRequest;
import com.ssafy.devway.domain.member.dto.response.MemberDetailResponse;
import com.ssafy.devway.domain.member.service.MemberService;
import com.ssafy.devway.global.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cherry/api/member")
public class MemberController {

    private final MemberService memberService;
    private final AttendService attendService;

    /**
     * 회원 가입
     */
    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody MemberSignupRequest request){

        memberService.signup(request);

        return ApiResponse.ok(request.getMemberNickname());
    }

    /**
     * 멤버 정보 조회 (메인페이지)
     */
    @GetMapping("/detail")
    public ApiResponse<MemberDetailResponse> memberDetail(@RequestParam String memberEmail){

        return ApiResponse.ok(memberService.detailMember(memberEmail));
    }

    /**
     * 가입한 유저인지 체크
     */
    @GetMapping("/valid")
    public ApiResponse<Boolean> memberValid(@RequestParam String memberEmail){

        return ApiResponse.ok(memberService.validMember(memberEmail));
    }

    @GetMapping
    public String test(){
        return "hi";
    }

}
