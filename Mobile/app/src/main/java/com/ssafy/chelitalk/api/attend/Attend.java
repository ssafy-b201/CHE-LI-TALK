package com.ssafy.chelitalk.api.attend;

import java.time.LocalDate;

public class Attend {
    LocalDate attendDate;
    Boolean isAttend;

    public Boolean getAttend() {
        return isAttend;
    }

    public Attend(LocalDate attendDate, Boolean isAttend) {
        this.attendDate = attendDate;
        this.isAttend = isAttend;
    }
}
