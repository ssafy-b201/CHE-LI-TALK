package com.ssafy.chelitalk.api.member;

public class MemberData {
    private static MemberData instance;
    private String nickname;

    // private constructor to prevent instantiation
    private MemberData() {}

    // synchronized method to control concurrent access
    public static synchronized MemberData getInstance() {
        if (instance == null) {
            instance = new MemberData();
        }
        return instance;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
