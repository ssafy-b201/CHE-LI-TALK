package com.ssafy.devway.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberValidResponse {

    Boolean isRegistered;
    String nickname;

}
