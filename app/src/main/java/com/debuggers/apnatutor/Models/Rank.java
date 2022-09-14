package com.debuggers.apnatutor.Models;

import org.parceler.Parcel;

import java.util.Objects;

@Parcel
public class Rank {
    String userId;
    Double percentage;

    public Rank() {
    }

    public Rank(String userId, Double percentage) {
        this.userId = userId;
        this.percentage = percentage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rank)) return false;
        Rank rank = (Rank) o;
        return getUserId().equals(rank.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }
}
