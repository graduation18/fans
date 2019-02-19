package com.systemonline.fanscoupon.Model;

import java.util.ArrayList;

/**
 * Created by SystemOnline1 on 10/9/2016.
 */

public class ContestsBaseClass extends ChallengeBaseClass {
    private int contestId, brandID;
    private String contestTitle, availableFrom, availableTo;
    private ArrayList<Question> contestQuestions;

    public int getContestId() {
        return contestId;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }


    public String getContestTitle() {
        return contestTitle;
    }

    public void setContestTitle(String contestTitle) {
        this.contestTitle = contestTitle;
    }

    public ArrayList<Question> getContestQuestions() {
        return contestQuestions;
    }

    public void setContestQuestions(ArrayList<Question> contestQuestions) {
        this.contestQuestions = contestQuestions;
    }

    public int getBrandID() {
        return brandID;
    }

    public void setBrandID(int brandID) {
        this.brandID = brandID;
    }


    public String getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(String availableFrom) {
        this.availableFrom = availableFrom;
    }

    public String getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(String availableTo) {
        this.availableTo = availableTo;
    }
}
