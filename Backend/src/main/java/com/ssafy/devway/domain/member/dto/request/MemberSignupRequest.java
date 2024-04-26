package com.ssafy.devway.domain.member.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberSignupRequest {

    String memberNickname;
    String memberEmail;

}
