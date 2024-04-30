package com.ssafy.devway.domain.attend.service;

import com.ssafy.devway.domain.attend.dto.response.WeeklyAttendResponse;
import com.ssafy.devway.domain.attend.entity.Attend;
import com.ssafy.devway.domain.attend.repository.AttendRepository;
import com.ssafy.devway.domain.member.entity.Member;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendService {

	private final AttendRepository attendRepository;
	private final MemberRepository memberRepository;
	private final LocalDate TODAY = LocalDate.now();

	//출석 리스트 조회하기
	public WeeklyAttendResponse getList(Long memberId) {
		Member member = getMember(memberId);

		LocalDate date = TODAY.with(DayOfWeek.MONDAY);
		Boolean isAttended = false;
		HashMap<LocalDate, Boolean> map = new HashMap<>();
		for(int i=0;i<7;i++){
			map.put(date, isAttended);
			date = date.plusDays(1);
		}
		WeeklyAttendResponse responsedto = new WeeklyAttendResponse(member, map);
		return responsedto;
	}


	//출석 업데이트
	public WeeklyAttendResponse updateList(Long memberId) {

		Member member = getMember(memberId);
		LocalDate today = LocalDate.now();
		Attend attend = attendRepository.findByMemberAndAttendDate(memberId, today);
		attend.setAttendIsAttended(true);
		attendRepository.save(attend);

		HashMap<LocalDate, Boolean> map = new HashMap<>();
		map.put(today, true);

		WeeklyAttendResponse responsedto = new WeeklyAttendResponse(member, map);

		memberRepository.save(member);
		return responsedto;
	}

	//출석 초기화
	public WeeklyAttendResponse resetList(Long memberId) {
		Member member = getMember(memberId);

		//지난주 월요일(서버에서 자동으로 월요일 되면 false로 초기화)
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

	public Member getMember(Long memberId){
		return memberRepository.findByMemberId(memberId)
			.orElseThrow(() -> new IllegalArgumentException("해당 멤버를 찾을 수 없습니다."));
	}



}
