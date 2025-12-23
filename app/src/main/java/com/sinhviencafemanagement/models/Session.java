package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

public class Session {
    private final int userId;
    private final String token;
    private final long expiredAt;

    public Session(int userId, String token, long expiredAt) {
        this.userId = userId;
        this.token = token;
        this.expiredAt = expiredAt;
    }

    public Session() {
        this.userId = 0;
        this.token = "";
        this.expiredAt = 0L;
    }

    public int getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public long getExpiredAt() {
        return expiredAt;
    }

    @NonNull
    @Override
    public String toString() {
        return "Session{" +
                "userId=" + userId +
                ", token='" + token + '\'' +
                ", expiredAt=" + expiredAt +
                '}';
    }

}
