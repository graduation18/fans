package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 10/8/2016.
 */

public class Challenge {
    private int offerID;
    private String challengeName = "", challengeTitle = "", challengeDesc;
    private boolean missionStatus = false;

    public boolean isMissionStatus() {
        return missionStatus;
    }

    public void setMissionStatus(boolean missionStatus) {
        this.missionStatus = missionStatus;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public String getChallengeDesc() {
        return challengeDesc;
    }

    public void setChallengeDesc(String challengeDesc) {
        this.challengeDesc = challengeDesc;
    }

    public int getOfferID() {
        return offerID;
    }

    public void setOfferID(int offerID) {
        this.offerID = offerID;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }
}
