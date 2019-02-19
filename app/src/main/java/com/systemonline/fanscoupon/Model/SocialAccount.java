package com.systemonline.fanscoupon.Model;


public class SocialAccount {
    private String accountName, socialNetworkAccountID;
    private int socialNetworkType, accountID;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getSocialNetworkAccountID() {
        return socialNetworkAccountID;
    }

    public void setSocialNetworkAccountID(String socialNetworkAccountID) {
        this.socialNetworkAccountID = socialNetworkAccountID;
    }

    public int getSocialNetworkType() {
        return socialNetworkType;
    }

    public void setSocialNetworkType(int socialNetworkType) {
        this.socialNetworkType = socialNetworkType;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
}
