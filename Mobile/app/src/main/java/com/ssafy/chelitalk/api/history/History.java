package com.ssafy.chelitalk.api.history;

import java.time.LocalDateTime;
import java.util.Date;

public class History {
    private Date createdAt;

    public History(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }


}
