package com.ssafy.devway.domain.attend.repository;

import com.ssafy.devway.domain.attend.entity.Attend;
import com.ssafy.devway.domain.member.entity.Member;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendRepository extends JpaRepository<Attend, Long> {

	Attend findByMemberAndAttendDate(Member member, LocalDate date);
	List<Attend> findByMemberAndAttendDateBetween(Member member, LocalDate start, LocalDate end);
}
