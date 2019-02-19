package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 6/24/2017.
 */

public class CampaignLocation {
    private int campaignID, branchId, branchNotifyRange, campaignCouponId, frequency, frequencyLimit, status, timeDifference;
    private String campaignName, campaignTile, campaignBody, campaignCouponImg, campaignCouponSlug, campaignCouponType, campaignEndDate, campaignDeliveryTime;
    private double campaignLat, campaignLong;

    public int getCampaignID() {
        return campaignID;
    }

    public void setCampaignID(int campaignID) {
        this.campaignID = campaignID;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getBranchNotifyRange() {
        return branchNotifyRange;
    }

    public void setBranchNotifyRange(int branchNotifyRange) {
        this.branchNotifyRange = branchNotifyRange;
    }

    public int getCampaignCouponId() {
        return campaignCouponId;
    }

    public void setCampaignCouponId(int campaignCouponId) {
        this.campaignCouponId = campaignCouponId;
    }

    public String getCampaignTitle() {
        return campaignTile;
    }

    public void setCampaignTitle(String campaignTile) {
        this.campaignTile = campaignTile;
    }

    public String getCampaignBody() {
        return campaignBody;
    }

    public void setCampaignBody(String campaignBody) {
        this.campaignBody = campaignBody;
    }

    public String getCampaignCouponImg() {
        return campaignCouponImg;
    }

    public void setCampaignCouponImg(String campaignCouponImg) {
        this.campaignCouponImg = campaignCouponImg;
    }

    public String getCampaignCouponSlug() {
        return campaignCouponSlug;
    }

    public void setCampaignCouponSlug(String campaignCouponSlug) {
        this.campaignCouponSlug = campaignCouponSlug;
    }

    public String getCampaignCouponType() {
        return campaignCouponType;
    }

    public void setCampaignCouponType(String campaignCouponType) {
        this.campaignCouponType = campaignCouponType;
    }

    public String getCampaignEndDate() {
        return campaignEndDate;
    }

    public void setCampaignEndDate(String campaignEndDate) {
        this.campaignEndDate = campaignEndDate;
    }

    public double getCampaignLat() {
        return campaignLat;
    }

    public void setCampaignLat(double campaignLat) {
        this.campaignLat = campaignLat;
    }

    public double getCampaignLong() {
        return campaignLong;
    }

    public void setCampaignLong(double campaignLong) {
        this.campaignLong = campaignLong;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequencyLimit() {
        return frequencyLimit;
    }

    public void setFrequencyLimit(int frequencyLimit) {
        this.frequencyLimit = frequencyLimit;
    }

    public int getTimeDifference() {
        return timeDifference;
    }

    public void setTimeDifference(int timeDifference) {
        this.timeDifference = timeDifference;
    }

    public String getCampaignDeliveryTime() {
        return campaignDeliveryTime;
    }

    public void setCampaignDeliveryTime(String campaignDeliveryTime) {
        this.campaignDeliveryTime = campaignDeliveryTime;
    }
}
