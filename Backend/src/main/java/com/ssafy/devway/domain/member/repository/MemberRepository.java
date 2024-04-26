package com.ssafy.devway.domain.member.repository;

import com.ssafy.devway.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByMemberId(Long memberId);
	Optional<Member> findByMemberEmail(String memberEmail);
}
