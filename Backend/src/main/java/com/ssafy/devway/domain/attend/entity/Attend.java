package com.ssafy.devway.domain.attend.entity;

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
	private Member member;

	@Column(name = "attend_date")
	private LocalDate attendDate;

	@Column(name = "attend_is_attended")
	private boolean attendIsAttended;

}
