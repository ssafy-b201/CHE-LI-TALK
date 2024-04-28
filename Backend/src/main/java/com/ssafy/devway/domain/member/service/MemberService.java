package com.ssafy.devway.domain.member.service;

import com.ssafy.devway.domain.attend.dto.response.WeeklyAttendResponse;
import com.ssafy.devway.domain.attend.service.AttendService;
import com.ssafy.devway.domain.member.dto.request.MemberSignupRequest;
import com.ssafy.devway.domain.member.dto.response.MemberDetailResponse;
import com.ssafy.devway.domain.member.entity.Member;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final AttendService attendService;

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
    }

    public MemberDetailResponse detailMember(String memberEmail) {

        Member findedMember = getMember(memberEmail);

        if (findedMember == null) {
            throw new IllegalArgumentException("해당 회원을 찾을 수 없습니다.");
        }

        Long memberId = findedMember.getMemberId();

        WeeklyAttendResponse attend = attendService.getList(memberId);

        return MemberDetailResponse.builder()
            .memberNickname(findedMember.getMemberNickname())
            .attend(attend)
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
