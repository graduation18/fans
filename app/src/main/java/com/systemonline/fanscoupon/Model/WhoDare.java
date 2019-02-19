package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 10/16/2016.
 */

public class WhoDare extends ChallengeBaseClass {
    private String missionName, missionSlug, missionDesc, missionURL, missionStartDate, missionEndDate;
    private int missionID, missionBrandID;

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public String getMissionDesc() {
        return missionDesc;
    }

    public void setMissionDesc(String missionDesc) {
        this.missionDesc = missionDesc;
    }

    public String getMissionURL() {
        return missionURL;
    }

    public void setMissionURL(String missionURL) {
        this.missionURL = missionURL;
    }

    public int getMissionID() {
        return missionID;
    }

    public void setMissionID(int missionID) {
        this.missionID = missionID;
    }

    public String getMissionStartDate() {
        return missionStartDate;
    }

    public void setMissionStartDate(String missionStartDate) {
        this.missionStartDate = missionStartDate;
    }

    public String getMissionEndDate() {
        return missionEndDate;
    }

    public void setMissionEndDate(String missionEndDate) {
        this.missionEndDate = missionEndDate;
    }

    public String getMissionSlug() {
        return missionSlug;
    }

    public void setMissionSlug(String missionSlug) {
        this.missionSlug = missionSlug;
    }

    public int getMissionBrandID() {
        return missionBrandID;
    }

    public void setMissionBrandID(int missionBrandID) {
        this.missionBrandID = missionBrandID;
    }
}
