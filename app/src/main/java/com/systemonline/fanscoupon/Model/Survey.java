package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 10/9/2016.
 */

public class Survey extends ContestsBaseClass {
    private String surveySlug;
    private int surveyID;

    public int getSurveyID() {
        return surveyID;
    }

    public void setSurveyID(int surveyID) {
        this.surveyID = surveyID;
    }

    public String getSurveySlug() {
        return surveySlug;
    }

    public void setSurveySlug(String surveySlug) {
        this.surveySlug = surveySlug;
    }
}
