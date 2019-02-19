package com.systemonline.fanscoupon.Model;

import java.util.ArrayList;


public class Coupon implements Comparable<Coupon>, Cloneable {
    private String couponName, couponType, couponImage, couponStartDate, couponEndDate, couponInfo,
            customerType = "", couponRating, couponSlug;
    private int couponID, couponTypeID, couponSavedByCount, maxFansUse, maxUsePerFan, maxUsePerFanPerDay;
    private boolean hasSaved = false, affiliate, challengeDue, qualifiedUser;
    private ArrayList<Comment> couponComments;
    private ArrayList<Fan> couponSavedBy;
    private Brand couponBrand;
    private ArrayList<EventRate> eventRates;
    private CouponConditions couponConditions;
    private ArrayList<CustomerType> couponCustomerTypes;
    private ArrayList<EarnedPointsAverage> earnedPointsAverages;
    private Challenge couponChallenges;
    private ArrayList<String> disqualificationReasons, offerConditions;
    private CouponProgram couponProgram;
    private ArrayList<DayWorkingHour> otherInstruction = new ArrayList<>();

    public Coupon() {
    }

    //
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ArrayList<DayWorkingHour> getOtherInstruction() {
        return otherInstruction;
    }

    public void setOtherInstruction(ArrayList<DayWorkingHour> otherInstruction) {
        this.otherInstruction = otherInstruction;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        if (!couponName.isEmpty())
            this.couponName = couponName;
    }

    public int getCouponID() {
        return couponID;
    }

    public void setCouponID(int couponID) {
        if (couponID > 0)
            this.couponID = couponID;
    }

    public int getCouponTypeID() {
        return couponTypeID;
    }

    public void setCouponTypeID(int couponTypeID) {
        this.couponTypeID = couponTypeID;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        if (!couponType.isEmpty())
            this.couponType = couponType;
    }

    public String getCouponImage() {
        return couponImage;
    }

    public void setCouponImage(String couponImage) {
        if (!couponImage.isEmpty())
            this.couponImage = couponImage;
    }

    public String getCouponStartDate() {
        return couponStartDate;
    }

    public void setCouponStartDate(String couponStartDate) {
        if (!couponStartDate.isEmpty())
            this.couponStartDate = couponStartDate;
    }

    public String getCouponEndDate() {
        return couponEndDate;
    }

    public void setCouponEndDate(String couponEndDate) {
        if (!couponEndDate.isEmpty())
            this.couponEndDate = couponEndDate;
    }

    public String getCouponInfo() {
        return couponInfo;
    }

    public void setCouponInfo(String couponInfo) {
        if (!couponInfo.isEmpty())
            this.couponInfo = couponInfo;
    }

    public boolean getHasSaved() {
        return hasSaved;
    }

    public void setHasSaved(boolean hasSaved) {
        this.hasSaved = hasSaved;
    }

    public int getCouponSavedByCount() {
        return couponSavedByCount;
    }

    public void setCouponSavedByCount(int couponSavedByCount) {
        this.couponSavedByCount = couponSavedByCount;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        if (!customerType.isEmpty())
            this.customerType = customerType;
    }

    public boolean getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(boolean affiliate) {
        this.affiliate = affiliate;
    }

    public Brand getCouponBrand() {
        return couponBrand;
    }

    public void setCouponBrand(Brand couponBrand) {
        this.couponBrand = couponBrand;
    }

    public ArrayList<Comment> getCouponComments() {
        return couponComments;
    }

    public void setCouponComments(ArrayList<Comment> couponComments) {
        this.couponComments = couponComments;
    }

    public ArrayList<Fan> getCouponSavedBy() {
        return couponSavedBy;
    }

    public void setCouponSavedBy(ArrayList<Fan> couponSavedBy) {
        this.couponSavedBy = couponSavedBy;
    }

    public ArrayList<EventRate> getEventRates() {
        return eventRates;
    }

    public void setEventRates(ArrayList<EventRate> eventRates) {
        this.eventRates = eventRates;
    }

    public CouponConditions getCouponConditions() {
        return couponConditions;
    }

    public void setCouponConditions(CouponConditions couponConditions) {
        this.couponConditions = couponConditions;
    }

    public String getCouponRating() {
        return couponRating;
    }

    public void setCouponRating(String couponRating) {
        this.couponRating = couponRating;
    }

    public ArrayList<CustomerType> getCouponCustomerTypes() {
        return couponCustomerTypes;
    }

    public void setCouponCustomerTypes(ArrayList<CustomerType> couponCustomerTypes) {
        this.couponCustomerTypes = couponCustomerTypes;
    }

    public String getCouponSlug() {
        return couponSlug;
    }

    public void setCouponSlug(String couponSlug) {
        this.couponSlug = couponSlug;
    }

    public ArrayList<EarnedPointsAverage> getEarnedPointsAverages() {
        return earnedPointsAverages;
    }

    public void setEarnedPointsAverages(ArrayList<EarnedPointsAverage> earnedPointsAverages) {
        this.earnedPointsAverages = earnedPointsAverages;
    }

    @Override
    public int compareTo(Coupon another) {
        return getCouponStartDate().compareTo(another.getCouponStartDate());
    }

    public boolean getChallengeDue() {
        return challengeDue;
    }

    public void setChallengeDue(boolean challengeDue) {
        this.challengeDue = challengeDue;
    }

    public Challenge getCouponChallenges() {
        return couponChallenges;
    }

    public void setCouponChallenge(Challenge couponChallenges) {
        this.couponChallenges = couponChallenges;
    }

    public int getMaxUsePerFanPerDay() {
        return maxUsePerFanPerDay;
    }

    public void setMaxUsePerFanPerDay(int maxUsePerFanPerDay) {
        this.maxUsePerFanPerDay = maxUsePerFanPerDay;
    }

    public int getMaxUsePerFan() {
        return maxUsePerFan;
    }

    public void setMaxUsePerFan(int maxUsePerFan) {
        this.maxUsePerFan = maxUsePerFan;
    }

    public int getMaxFansUse() {
        return maxFansUse;
    }

    public void setMaxFansUse(int maxFansUse) {
        this.maxFansUse = maxFansUse;
    }

    public ArrayList<String> getDisqualificationReasons() {
        return disqualificationReasons;
    }

    public void setDisqualificationReasons(ArrayList<String> disqualificationReasons) {
        this.disqualificationReasons = disqualificationReasons;
    }

    public boolean isQualifiedUser() {
        return qualifiedUser;
    }

    public void setQualifiedUser(boolean qualifiedUser) {
        this.qualifiedUser = qualifiedUser;
    }

    public CouponProgram getCouponProgram() {
        return couponProgram;
    }

    public void setCouponProgram(CouponProgram couponProgram) {
        this.couponProgram = couponProgram;
    }

    public ArrayList<String> getOfferConditions() {
        return offerConditions;
    }

    public void setOfferConditions(ArrayList<String> offerConditions) {
        this.offerConditions = offerConditions;
    }
}
