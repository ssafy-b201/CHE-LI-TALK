package com.ssafy.devway.domain.member.dto.response;

import com.ssafy.devway.domain.attend.dto.response.WeeklyAttendResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDetailResponse {

    String memberEmail;
    String memberNickname;
    WeeklyAttendResponse attend;

}
