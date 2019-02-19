package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 4/24/2017.
 */

public class CreditPerBrand extends Brand {
    private CustomerType customerType;
    private int totalPoints, usedCoupons, availableCoupons, exchangedPoints;

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getUsedCoupons() {
        return usedCoupons;
    }

    public void setUsedCoupons(int usedCoupons) {
        this.usedCoupons = usedCoupons;
    }

    public int getAvailableCoupons() {
        return availableCoupons;
    }

    public void setAvailableCoupons(int availableCoupons) {
        this.availableCoupons = availableCoupons;
    }

    public int getExchangedPoints() {
        return exchangedPoints;
    }

    public void setExchangedPoints(int exchangedPoints) {
        this.exchangedPoints = exchangedPoints;
    }
}
