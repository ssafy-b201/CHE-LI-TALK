package com.ssafy.devway.domain.member.entity;

import com.ssafy.devway.domain.attend.entity.Attend;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_nickname")
    private String memberNickname;

    @Column(name = "member_is_alert")
    private Boolean memberIsAlert;

    @Column(name = "member_email")
    private String memberEmail;

    @OneToMany(mappedBy = "member")
    private List<Attend> memberAttend;

    @Builder
    public Member(String memberNickname, String memberEmail) {
        this.memberNickname = memberNickname;
        this.memberIsAlert = true;
        this.memberEmail = memberEmail;
        this.memberAttend = new ArrayList<>();
    }
}
