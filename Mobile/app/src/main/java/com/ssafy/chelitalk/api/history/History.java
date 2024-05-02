package com.ssafy.chelitalk.api.history;

import java.time.LocalDateTime;

public class History {
    private LocalDateTime createdAt;
    private String memberEmail;

    public History(LocalDateTime createdAt, String memberEmail) {
        this.createdAt = createdAt;
        this.memberEmail = memberEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }
}
