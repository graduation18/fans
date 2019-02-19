package com.systemonline.fanscoupon.Model;


public class EventRate {
    private String eventName, pointsEarned;

    public EventRate(String eventName, String pointsEarned) {
        this.eventName = eventName;
        this.pointsEarned = pointsEarned;
    }

    public String getEventName() {
        return eventName;
    }

    public String getPointsEarned() {
        return pointsEarned;
    }
}
