package com.ssafy.devway.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long member_id;

	@Column(name = "member_nickname")
	private String memberNickname;

	@Column(name = "member_is_first")
	private boolean memberIsFirst;

	@Column(name = "member_is_alert")
	private boolean memberIsAlert;

}
