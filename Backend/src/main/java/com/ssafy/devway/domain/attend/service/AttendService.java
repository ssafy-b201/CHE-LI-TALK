package com.ssafy.devway.domain.attend.service;

import com.ssafy.devway.domain.attend.dto.response.WeeklyAttendResponse;
import com.ssafy.devway.domain.attend.repository.AttendRepository;
import com.ssafy.devway.domain.member.entity.Member;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendService {

	private final AttendRepository attendRepository;
	private final MemberRepository memberRepository;


	//출석 리스트 조회하기
	public WeeklyAttendResponse getList(Long memberId) {
		Member member = memberRepository.findByMemberId(memberId);

		LocalDate today = LocalDate.now();
		LocalDate monday = today.with(DayOfWeek.MONDAY);
		LocalDate tuesday = today.with(DayOfWeek.TUESDAY);
		LocalDate wednesday = today.with(DayOfWeek.WEDNESDAY);
		LocalDate thursday = today.with(DayOfWeek.THURSDAY);
		LocalDate friday = today.with(DayOfWeek.FRIDAY);
		LocalDate saturday = today.with(DayOfWeek.SATURDAY);
		LocalDate sunday = today.with(DayOfWeek.SUNDAY);
		Boolean isAttended = false;
		HashMap<LocalDate, Boolean> map = new HashMap<>();

		map.put(monday, isAttended);
		map.put(tuesday, isAttended);
		map.put(wednesday, isAttended);
		map.put(thursday, isAttended);
		map.put(friday, isAttended);
		map.put(saturday, isAttended);
		map.put(sunday, isAttended);

		WeeklyAttendResponse responsedto = new WeeklyAttendResponse(member, map);
		return responsedto;
	}


	//출석 업데이트
	public WeeklyAttendResponse updateList(Long memberId) {
		Member member = memberRepository.findByMemberId(memberId);
		LocalDate today = LocalDate.now();
		HashMap<LocalDate, Boolean> map = new HashMap<>();
		map.put(today, true);
		WeeklyAttendResponse responsedto = new WeeklyAttendResponse(member, map);
		return responsedto;
	}

	//출석 초기화
	public WeeklyAttendResponse resetList(Long memberId) {
		Member member = memberRepository.findByMemberId(memberId);

		//지난주 월요일
		LocalDate previousMonday = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));

		HashMap<LocalDate, Boolean> map = new HashMap<>();
		LocalDate date = previousMonday;

		for(int i=0;i<7;i++){
			map.put(date, false);
			date = date.plusDays(1);
		}

		WeeklyAttendResponse responsedto = new WeeklyAttendResponse(member, map);
		return responsedto;
	}



}
