package com.systemonline.fanscoupon.Model;


import com.systemonline.fanscoupon.Base.BaseActivity;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import static com.systemonline.fanscoupon.MainActivity.IMEI;

public class Fan {
    private String fanFirstName, fanLastName, fanUserName, fanEMAil, fanImage, fanBadgesCount, bigFanBadgesCount, fanMob,
            superFanBadgesCount, fanGender, fanAccessToken, fanAddress, fanCity, fanCountry, fanBirthDate, fanPhone;
    private ArrayList<CustomerType> totalCustomerTypes, totalBadges;
    private ArrayList<String> fanBrandsLogos;
    private ArrayList<SocialAccount> socialAccounts;
    private int fanID, starCount, couponCount, fanAccessTokenExp, fanCountryID, fanCityID;
    private ArrayList<FaceBookPage> fanFbPages;
    private boolean stopNotification;

    public static JSONAsync requestNewAccessToken(String token, BaseFragment baseFragment, String userID) {
//        requestType = "requestNewAccessToken";
//        _utility.ShowDialog(getResources().getString(R.string.plz_w8));
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("grant_type", "social_network_login"));
        nameValuePairs.add(new BasicNameValuePair("socialNetworkType", "13"));
        nameValuePairs.add(new BasicNameValuePair("token[accessToken]", token));
        nameValuePairs.add(new BasicNameValuePair("fb_account_id", userID));
        nameValuePairs.add(new BasicNameValuePair("status", "connected"));
        nameValuePairs.add(new BasicNameValuePair("country", "EG"));
        nameValuePairs.add(new BasicNameValuePair("imei", IMEI));
        nameValuePairs.add(new BasicNameValuePair("client_id", "AcHQshNPKmxX2cgf9Nv6R3eTFuu5N9oYdZ7o467e"));
        nameValuePairs.add(new BasicNameValuePair("client_secret", "NobHR7EvF5PSpRLccPQw3PcL0Fk6yKddqlXhpLbr"));
        JSONWebServices service = new JSONWebServices(baseFragment);
        return service.renewAccessTokenFragment(nameValuePairs);
    }

    public static JSONAsync requestNewAccessToken(String userName, String userPassword, BaseFragment baseFragment) {
//        Log.e("accessToken", "request facebook button");
//        _utility.ShowDialog(getResources().getString(R.string.plz_w8));
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
//        nameValuePairs.add(new BasicNameValuePair("socialNetworkType", "13"));
        nameValuePairs.add(new BasicNameValuePair("username", userName));
        nameValuePairs.add(new BasicNameValuePair("password", userPassword));
        nameValuePairs.add(new BasicNameValuePair("imei", IMEI));
        nameValuePairs.add(new BasicNameValuePair("client_id", "AcHQshNPKmxX2cgf9Nv6R3eTFuu5N9oYdZ7o467e"));
        nameValuePairs.add(new BasicNameValuePair("client_secret", "NobHR7EvF5PSpRLccPQw3PcL0Fk6yKddqlXhpLbr"));
        JSONWebServices service = new JSONWebServices(baseFragment);
        return service.renewAccessTokenFragment(nameValuePairs);
    }

    public static JSONAsync requestNewAccessToken(String token, BaseActivity baseActivity, String userID) {
//        requestType = "requestNewAccessToken";
//        _utility.ShowDialog(getResources().getString(R.string.plz_w8));
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("grant_type", "social_network_login"));
        nameValuePairs.add(new BasicNameValuePair("socialNetworkType", "13"));
        nameValuePairs.add(new BasicNameValuePair("token[accessToken]", token));
        nameValuePairs.add(new BasicNameValuePair("fb_account_id", userID));
        nameValuePairs.add(new BasicNameValuePair("status", "connected"));
        nameValuePairs.add(new BasicNameValuePair("country", "EG"));
        nameValuePairs.add(new BasicNameValuePair("imei", IMEI));
        nameValuePairs.add(new BasicNameValuePair("client_id", "AcHQshNPKmxX2cgf9Nv6R3eTFuu5N9oYdZ7o467e"));
        nameValuePairs.add(new BasicNameValuePair("client_secret", "NobHR7EvF5PSpRLccPQw3PcL0Fk6yKddqlXhpLbr"));
        JSONWebServices service = new JSONWebServices(baseActivity);
        return service.renewAccessTokenActivity(nameValuePairs);
    }

    public String getFanPhone() {
        return fanPhone;
    }

    public void setFanPhone(String fanPhone) {
        this.fanPhone = fanPhone;
    }

    public String getFanMob() {
        return fanMob;
    }

    public void setFanMob(String fanMob) {
        this.fanMob = fanMob;
    }

    public String getFanBirthDate() {
        return fanBirthDate;
    }

    public void setFanBirthDate(String fanBirthDate) {
        this.fanBirthDate = fanBirthDate;
    }

    public String getFanFirstName() {
        return fanFirstName;
    }

    public void setFanFirstName(String fanFirstName) {
        this.fanFirstName = fanFirstName;
    }

    public String getFanLastName() {
        return fanLastName;
    }

    public void setFanLastName(String fanLastName) {
        this.fanLastName = fanLastName;
    }

    public String getFanEMAil() {
        return fanEMAil;
    }

    public void setFanEMAil(String fanEMAil) {
        this.fanEMAil = fanEMAil;
    }

    public String getFanImage() {
        return fanImage;
    }

    public void setFanImage(String fanImage) {
        this.fanImage = fanImage;
    }

    public int getFanID() {
        return fanID;
    }

    public void setFanID(int fanID) {
        this.fanID = fanID;
    }

    public String getFanBadgesCount() {
        return fanBadgesCount;
    }

    public void setFanBadgesCount(String fanBadgesCount) {
        this.fanBadgesCount = fanBadgesCount;
    }

    public String getBigFanBadgesCount() {
        return bigFanBadgesCount;
    }

    public void setBigFanBadgesCount(String bigFanBadgesCount) {
        this.bigFanBadgesCount = bigFanBadgesCount;
    }

    public ArrayList<SocialAccount> getSocialAccounts() {
        return socialAccounts;
    }

    public void setSocialAccounts(ArrayList<SocialAccount> socialAccounts) {
        this.socialAccounts = socialAccounts;
    }

    public String getSuperFanBadgesCount() {
        return superFanBadgesCount;
    }

    public void setSuperFanBadgesCount(String superFanBadgesCount) {
        this.superFanBadgesCount = superFanBadgesCount;
    }

    public int getCouponCount() {
        return couponCount;
    }

    public void setCouponCount(int couponCount) {
        this.couponCount = couponCount;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public String getFanUserName() {
        return fanUserName;
    }

    public void setFanUserName(String fanUserName) {
        this.fanUserName = fanUserName;
    }

    public String getFanGender() {
        return fanGender;
    }

    public void setFanGender(String fanGender) {
        this.fanGender = fanGender;
    }

    public String getFanAccessToken() {
        return fanAccessToken;
    }

    public void setFanAccessToken(String fanAccessToken) {
        this.fanAccessToken = fanAccessToken;
    }

    public int getFanAccessTokenExp() {
        return fanAccessTokenExp;
    }

    public void setFanAccessTokenExp(int fanAccessTokenExp) {
        this.fanAccessTokenExp = fanAccessTokenExp;
    }

    public String getFanAddress() {
        return fanAddress;
    }

    public void setFanAddress(String fanAddress) {
        this.fanAddress = fanAddress;
    }

    public String getFanCity() {
        return fanCity;
    }

    public void setFanCity(String fanCity) {
        this.fanCity = fanCity;
    }

    public String getFanCountry() {
        return fanCountry;
    }

    public void setFanCountry(String fanCountry) {
        this.fanCountry = fanCountry;
    }

    public ArrayList<FaceBookPage> getFanFbPages() {
        return fanFbPages;
    }

    public void setFanFbPages(ArrayList<FaceBookPage> fanFbPages) {
        this.fanFbPages = fanFbPages;
    }

    public int getFanCountryID() {
        return fanCountryID;
    }

    public void setFanCountryID(int fanCountryID) {
        this.fanCountryID = fanCountryID;
    }

    public int getFanCityID() {
        return fanCityID;
    }

    public void setFanCityID(int fanCityID) {
        this.fanCityID = fanCityID;
    }

    public ArrayList<CustomerType> getTotalCustomerTypes() {
        return totalCustomerTypes;
    }

    public void setTotalCustomerTypes(ArrayList<CustomerType> totalCustomerTypes) {
        this.totalCustomerTypes = totalCustomerTypes;
    }

    public ArrayList<CustomerType> getTotalBadges() {
        return totalBadges;
    }

    public void setTotalBadges(ArrayList<CustomerType> totalBadges) {
        this.totalBadges = totalBadges;
    }

    public ArrayList<String> getFanBrandsLogos() {
        return fanBrandsLogos;
    }

    public void setFanBrandsLogos(ArrayList<String> fanBrandsLogos) {
        this.fanBrandsLogos = fanBrandsLogos;
    }

    public boolean isStopNotification() {
        return stopNotification;
    }

    public void setStopNotification(boolean stopNotification) {
        this.stopNotification = stopNotification;
    }
}
