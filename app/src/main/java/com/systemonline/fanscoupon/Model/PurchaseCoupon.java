package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 10/9/2016.
 */

public class PurchaseCoupon extends ChallengeBaseClass {
    private String fromDate, toDate, purchaseAmountMoreThan;

    public String getPurchaseAmountMoreThan() {
        return purchaseAmountMoreThan;
    }

    public void setPurchaseAmountMoreThan(String purchaseAmountMoreThan) {
        this.purchaseAmountMoreThan = purchaseAmountMoreThan;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}
