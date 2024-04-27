package com.ssafy.devway.domain.attend.entity;

import com.ssafy.devway.domain.attend.dto.response.AttendResDto;
import com.ssafy.devway.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Attend {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "attend_id")
	private Long attendId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	@Setter
	private Member member;

	@Column(name = "attend_date")
	@Setter
	private LocalDate attendDate;

	@Column(name = "attend_is_attended")
	@Setter
	private boolean attendIsAttended;

	public AttendResDto toDto(){
		AttendResDto dto = new AttendResDto();
		dto.setMember(this.member);
		dto.setMember(this.member);
		dto.setAttendIsAttended(this.attendIsAttended);
		return dto;
	}

}
