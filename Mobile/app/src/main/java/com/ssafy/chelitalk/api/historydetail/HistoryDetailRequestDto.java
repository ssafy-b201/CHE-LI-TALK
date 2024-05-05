package com.ssafy.chelitalk.api.historydetail;

import java.time.LocalDateTime;

public class HistoryDetailRequestDto {
    private String memberEmail;
    private LocalDateTime createdAt;

    public HistoryDetailRequestDto(String memberEmail, LocalDateTime createdAt) {
        this.memberEmail = memberEmail;
        this.createdAt = createdAt;
    }

    public String getMemberEmail() {
        return memberEmail;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
