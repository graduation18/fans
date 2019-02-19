package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 10/9/2016.
 */

public class Contest extends ContestsBaseClass {
    private int minPassContestDegree, availableTrialsNumber;

    public int getMinPassContestDegree() {
        return minPassContestDegree;
    }

    public void setMinPassContestDegree(int minPassContestDegree) {
        this.minPassContestDegree = minPassContestDegree;
    }

    public int getAvailableTrialsNumber() {
        return availableTrialsNumber;
    }

    public void setAvailableTrialsNumber(int availableTrialsNumber) {
        this.availableTrialsNumber = availableTrialsNumber;
    }
}
