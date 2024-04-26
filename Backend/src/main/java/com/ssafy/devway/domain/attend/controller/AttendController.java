package com.ssafy.devway.domain.attend.controller;

import com.ssafy.devway.domain.attend.dto.request.CheckAttendRequest;
import com.ssafy.devway.domain.attend.dto.response.WeeklyAttendResponse;
import com.ssafy.devway.domain.attend.service.AttendService;
import com.ssafy.devway.domain.member.entity.Member;
import com.ssafy.devway.global.api.ApiResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public WeeklyAttendResponse attendList(@PathVariable Long memberId) throws IOException {
		return attendService.getList(memberId);
	}

	/**
	* 4.2 출석 업데이트해주기 : 오늘 진입하면 true로 변경
	*/
	@PutMapping("/update")
	public WeeklyAttendResponse updateAttend(@RequestBody CheckAttendRequest request){
		return attendService.updateList(request.getMember().getMemberId());
	}


	/**
	* 4.3 출석 초기화 (일주일 지나면)
	*/
	@PostMapping("/reset")
	public WeeklyAttendResponse resetAttend(@PathVariable Long memberId) throws IOException{
		return attendService.resetList(memberId);
	}


}
