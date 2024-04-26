package com.ssafy.devway.domain.attend.dto.response;

import com.ssafy.devway.domain.member.entity.Member;
import java.time.LocalDate;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class WeeklyAttendResponse {

	Member member;
	HashMap<LocalDate, Boolean> map;
}
