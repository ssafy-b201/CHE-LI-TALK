package com.ssafy.devway.domain.attend.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AttendDto {

	LocalDate attendDate;
	Boolean isAttend;
}
