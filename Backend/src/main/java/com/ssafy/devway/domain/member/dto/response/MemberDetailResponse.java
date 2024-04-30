package com.ssafy.devway.domain.member.dto.response;

import com.ssafy.devway.domain.attend.dto.AttendDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDetailResponse {

    String memberEmail;
    String memberNickname;
    List<AttendDto> attendList;

}
