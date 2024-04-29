package com.ssafy.devway.domain.attend.dto.request;

import com.ssafy.devway.domain.member.entity.Member;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;

@Builder
@Getter
@AllArgsConstructor
public class CheckAttendRequest {

	Member member;
	LocalDate attendDate;


}
