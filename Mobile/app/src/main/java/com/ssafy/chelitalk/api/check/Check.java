package com.ssafy.chelitalk.api.check;

public class Check {
    private String memberEmail;

    public Check(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

}
