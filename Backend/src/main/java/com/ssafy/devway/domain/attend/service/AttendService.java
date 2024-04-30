package com.ssafy.devway.domain.attend.service;

import com.ssafy.devway.domain.attend.dto.AttendDto;
import com.ssafy.devway.domain.attend.entity.Attend;
import com.ssafy.devway.domain.attend.repository.AttendRepository;
import com.ssafy.devway.domain.member.entity.Member;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendService {

	private final AttendRepository attendRepository;
	private final MemberRepository memberRepository;


	//출석 리스트 조회하기
	public List<AttendDto> getList(Long memberId) {
		Member member = getMember(memberId);

		LocalDate startOfWeek = LocalDate.now().with(ChronoField.DAY_OF_WEEK, 1);
		LocalDate endOfWeek = startOfWeek.plusDays(6);

		List<Attend> weeklyAttendance = attendRepository.findByMemberAndAttendDateBetween(member, startOfWeek, endOfWeek);
		List<AttendDto> attendList = new ArrayList<>(7);

		if(weeklyAttendance != null){
			for(int i=0;i<7;i++){

				Attend attend = attendRepository.findByMemberAndAttendDate(member, startOfWeek);
				Boolean isAttend = attend.isAttendIsAttended();

				attendList.add(new AttendDto(startOfWeek, isAttend));
				startOfWeek = startOfWeek.plusDays(1);
			}
		}
		return attendList;
	}


	//출석 업데이트 - 오늘 진입하면 true로 바꾸기
	public String updateList(Long memberId) {
		Member member = getMember(memberId);
		LocalDate today = LocalDate.now();
		Attend attend = attendRepository.findByMemberAndAttendDate(member, today);
		attend.setAttendIsAttended(true);
		Attend savedAttend = attendRepository.save(attend);
		if(savedAttend.isAttendIsAttended()){
			return "업데이트 성공";
		}else{
			return "업데이트 실패";
		}
	}

	//출석 초기화
	public String resetList(Long memberId) {
		//지난주 월요일(서버에서 자동으로 월요일 되면 false로 초기화)
		LocalDate previousMonday = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));

		LocalDate date = previousMonday;
		List<AttendDto> list = new ArrayList<>(7);

		for(int i=0;i<7;i++){
			list.add(new AttendDto(date, false));
			date = date.plusDays(1);
		}
		return "초기화 성공";
	}

	public Member getMember(Long memberId){
		return memberRepository.findByMemberId(memberId)
			.orElseThrow(() -> new IllegalArgumentException("해당 멤버를 찾을 수 없습니다."));
	}



}
