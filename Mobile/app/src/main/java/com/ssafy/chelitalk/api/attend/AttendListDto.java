package com.ssafy.chelitalk.api.attend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;
import java.util.Locale;

public class AttendListDto {
    Date attendDate;
    Boolean isAttend;

    public AttendListDto(Date attendDate, Boolean isAttend) {
        this.attendDate = attendDate;
        this.isAttend = isAttend;
    }

    public Date getAttendDate() {
        return attendDate;
    }

    public Boolean getAttend() {
        return isAttend;
    }

    @Override
    public String toString(){
        return isAttend ? "T" : "F";
    }

}
