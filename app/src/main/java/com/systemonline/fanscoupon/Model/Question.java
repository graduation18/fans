package com.systemonline.fanscoupon.Model;

import java.util.ArrayList;

/**
 * Created by SystemOnline1 on 10/9/2016.
 */

public class Question {
    private int questionID, questionDegree, questionType;
    private String questionTitle;
    private ArrayList<Choice> questionChoices;

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public ArrayList<Choice> getQuestionChoices() {
        return questionChoices;
    }

    public void setQuestionChoices(ArrayList<Choice> questionChoices) {
        this.questionChoices = questionChoices;
    }

    public int getQuestionDegree() {
        return questionDegree;
    }

    public void setQuestionDegree(int questionDegree) {
        this.questionDegree = questionDegree;
    }

    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }
}
