package com.ssafy.devway.domain.member.service;

import com.ssafy.devway.domain.attend.dto.AttendDto;
import com.ssafy.devway.domain.attend.entity.Attend;
import com.ssafy.devway.domain.attend.repository.AttendRepository;
import com.ssafy.devway.domain.attend.service.AttendService;
import com.ssafy.devway.domain.member.dto.request.MemberSignupRequest;
import com.ssafy.devway.domain.member.dto.response.MemberDetailResponse;
import com.ssafy.devway.domain.member.entity.Member;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final AttendRepository attendRepository;

    public void signup(MemberSignupRequest request) {

        Member member = memberRepository.findByMemberEmail(request.getMemberEmail());

        if(member != null){
            throw new IllegalArgumentException("이미 가입 된 이메일입니다.");
        }

        Member newMember = Member.builder()
            .memberNickname(request.getMemberNickname())
            .memberEmail(request.getMemberEmail())
            .build();

        memberRepository.save(newMember);

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        for(int i=0;i<7;i++){
            LocalDate date = startOfWeek.plusDays(i);
            Attend attend = new Attend();
            attend.setMember(newMember);
            attend.setAttendDate(date);
            attend.setAttendIsAttended(false);
            attendRepository.save(attend);
        }
    }

    public MemberDetailResponse detailMember(String memberEmail) {

        Member findedMember = getMember(memberEmail);

        if (findedMember == null) {
            throw new IllegalArgumentException("해당 회원을 찾을 수 없습니다.");
        }

        Long memberId = findedMember.getMemberId();

        List<AttendDto> attendList = new ArrayList<>();

        return MemberDetailResponse.builder()
            .memberNickname(findedMember.getMemberNickname())
            .attendList(attendList)
            .build();
    }

    public Boolean validMember(String memberEmail) {

        Member findedMember = getMember(memberEmail);


        // false : 가입하지 않은 유저
        return findedMember != null;
    }

    public Member getMember(String memberEmail) {
        return memberRepository.findByMemberEmail(memberEmail);
    }

}
