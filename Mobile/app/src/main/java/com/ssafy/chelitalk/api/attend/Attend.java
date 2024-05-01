package com.ssafy.chelitalk.api.attend;

import java.time.LocalDate;

public class Attend {
    LocalDate attendDate;
    Boolean isAttend;

    public Attend() {
    }

    public LocalDate getAttendDate() {
        return attendDate;
    }

    public void setAttendDate(LocalDate attendDate) {
        this.attendDate = attendDate;
    }

    public Boolean getAttend() {
        return isAttend;
    }

    public void setAttend(Boolean attend) {
        isAttend = attend;
    }

    public Attend(LocalDate attendDate, Boolean isAttend) {
        this.attendDate = attendDate;
        this.isAttend = isAttend;
    }
}
