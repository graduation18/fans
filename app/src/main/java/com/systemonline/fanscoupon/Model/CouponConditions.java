package com.systemonline.fanscoupon.Model;


public class CouponConditions {
    private String pointsExpireDays, dueDays, redemptionThreshold, earnedPoints, earnedEquivalentCurrency;

    public String getPointsExpireDays() {
        return pointsExpireDays;
    }

    public void setPointsExpireDays(String pointsExpireDays) {
        this.pointsExpireDays = pointsExpireDays;
    }

    public String getDueDays() {
        return dueDays;
    }

    public void setDueDays(String dueDays) {
        this.dueDays = dueDays;
    }

    public String getRedemptionThreshold() {
        return redemptionThreshold;
    }

    public void setRedemptionThreshold(String redemptionThreshold) {
        this.redemptionThreshold = redemptionThreshold;
    }

    public String getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(String earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    public String getEarnedEquivalentCurrency() {
        return earnedEquivalentCurrency;
    }

    public void setEarnedEquivalentCurrency(String earnedEquivalentCurrency) {
        this.earnedEquivalentCurrency = earnedEquivalentCurrency;
    }
}
