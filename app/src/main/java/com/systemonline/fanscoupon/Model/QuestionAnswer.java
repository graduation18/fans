package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 10/18/2016.
 */

public class QuestionAnswer {
    private String answer;
    private int questionID, userDegree = 0, answerStatus;


    public int getUserDegree() {
        return userDegree;
    }

    public void setUserDegree(int userDegree) {
        this.userDegree = userDegree;
    }

    public int getAnswerStatus() {
        return answerStatus;
    }

    public void setAnswerStatus(int answerStatus) {
        this.answerStatus = answerStatus;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }
}
