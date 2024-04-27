package com.ssafy.devway.domain.attend.dto.response;

import com.ssafy.devway.domain.member.entity.Member;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendResDto {

	Member member;
	LocalDate attendDate;
	boolean attendIsAttended;

}
