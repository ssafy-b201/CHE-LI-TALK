package com.ssafy.devway.domain.attend.controller;

import com.ssafy.devway.domain.attend.dto.AttendDto;
import com.ssafy.devway.domain.attend.service.AttendService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attend")
public class AttendController {

	private final AttendService attendService;

	/**
	 * 4.1 출석 조회
	 */
	@GetMapping("/list/{memberId}")
	public List<AttendDto> attendList(@PathVariable Long memberId) throws IOException {
		return attendService.getList(memberId);
	}

	/**
	  * 4.2 출석 업데이트해주기
	  */
	@PutMapping("/update/{memberId}")
	public ResponseEntity<String> updateAttend(@PathVariable Long memberId){
		return ResponseEntity.ok(attendService.updateList(memberId));
	}


	/**
	  * 4.3 출석 초기화 (일주일 지나면)
	  */
	@PostMapping("/reset")
	public ResponseEntity<String> resetAttend(Long memberId) throws IOException{
		return ResponseEntity.ok(attendService.resetList(memberId));
	}


}
