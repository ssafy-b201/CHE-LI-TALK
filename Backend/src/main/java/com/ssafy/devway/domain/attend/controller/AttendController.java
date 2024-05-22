package com.ssafy.devway.domain.attend.controller;

import com.ssafy.devway.domain.attend.dto.AttendDto;
import com.ssafy.devway.domain.attend.service.AttendService;
import com.ssafy.devway.domain.member.entity.Member;
import com.ssafy.devway.domain.member.repository.MemberRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cherry/api/attend")
public class AttendController {

	private final AttendService attendService;
	private final MemberRepository memberRepository;

	/**
	 * 4.1 출석 조회
	 */
	@GetMapping("/list")
	public List<AttendDto> attendList(@RequestParam String memberEmail){
		Member member = memberRepository.findByMemberEmail(memberEmail);
		Long memberId = member.getMemberId();
		return attendService.getList(memberId);
	}

	/**
	  * 4.2 출석 업데이트해주기
	  */
	@PatchMapping("/update")
	public ResponseEntity<String> updateAttend(@RequestParam String memberEmail){
		Member member = memberRepository.findByMemberEmail(memberEmail);
		Long memberId = member.getMemberId();
		return ResponseEntity.ok(attendService.updateList(memberId));
	}


	/**
	  * 4.3 출석 초기화 (일주일 지나면)
	  */
	@PostMapping("/reset")
	public ResponseEntity<String> resetAttend(@RequestParam String memberEmail){
		Member member = memberRepository.findByMemberEmail(memberEmail);
		Long memberId = member.getMemberId();
		return ResponseEntity.ok(attendService.resetList(memberId));
	}


}
