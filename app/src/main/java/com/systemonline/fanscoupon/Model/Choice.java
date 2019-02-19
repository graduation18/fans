package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 10/9/2016.
 */

public class Choice {
    private String choiceText;
    private int isTrue;
    private int optionID;

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public int getIsTrue() {
        return isTrue;
    }

    public void setIsTrue(int isTrue) {
        this.isTrue = isTrue;
    }

    public int getOptionID() {
        return optionID;
    }

    public void setOptionID(int optionID) {
        this.optionID = optionID;
    }
}
