package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 8/8/2016.
 */
public class EarnedPointsAverage {
    private String purchaseFrom, purchaseTo;
    private int phase, points;

    public String getPurchaseFrom() {
        return purchaseFrom;
    }

    public void setPurchaseFrom(String purchaseFrom) {
        this.purchaseFrom = purchaseFrom;
    }

    public String getPurchaseTo() {
        return purchaseTo;
    }

    public void setPurchaseTo(String purchaseTo) {
        this.purchaseTo = purchaseTo;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }
}
