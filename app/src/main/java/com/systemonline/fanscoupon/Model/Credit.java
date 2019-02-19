package com.systemonline.fanscoupon.Model;

import java.util.ArrayList;

/**
 * Created by SystemOnline1 on 4/24/2017.
 */

public class Credit {
    private ArrayList<CustomerType> totalCustomerTypes, totalBadges;
    private int usedCoupons, availableCoupons, exchangedPoints;
    private ArrayList<CreditPerBrand> brands;
    private String totalPoints;

    public ArrayList<CustomerType> getTotalCustomerTypes() {
        return totalCustomerTypes;
    }

    public void setTotalCustomerTypes(ArrayList<CustomerType> totalCustomerTypes) {
        this.totalCustomerTypes = totalCustomerTypes;
    }

    public ArrayList<CustomerType> getTotalBadges() {
        return totalBadges;
    }

    public void setTotalBadges(ArrayList<CustomerType> totalBadges) {
        this.totalBadges = totalBadges;
    }

    public String getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(String totalPoints) {
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

    public ArrayList<CreditPerBrand> getBrands() {
        return brands;
    }

    public void setBrands(ArrayList<CreditPerBrand> brands) {
        this.brands = brands;
    }
}
