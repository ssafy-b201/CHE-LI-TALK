package com.ssafy.devway.domain.member.service;

import com.ssafy.devway.domain.member.dto.request.MemberSignupRequest;
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

    public void signup(MemberSignupRequest request) {

        Member newMember = Member.builder()
            .memberNickname(request.getMemberNickname())
            .memberEmail(request.getMemberEmail())
            .build();

        memberRepository.save(newMember);

    }

}
