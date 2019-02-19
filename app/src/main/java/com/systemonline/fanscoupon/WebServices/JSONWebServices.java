package com.systemonline.fanscoupon.WebServices;

import com.systemonline.fanscoupon.Base.BaseActivity;
import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.CampaignLocationsService;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.MainActivity;
import com.systemonline.fanscoupon.TrackingReceiver;
import com.systemonline.fanscoupon.brands_tabs.BrandsTab;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;

import org.apache.http.NameValuePair;

import java.util.List;

public class JSONWebServices {


    private BaseAdapter adapter;
    private BaseActivity activity;
    private BaseFragment fragment;
    private CampaignLocationsService campaignLocationsService;
    private TrackingReceiver trackingReceiver;


    public JSONWebServices(BaseFragment fragment) {
        this.fragment = fragment;
    }

    public JSONWebServices(BaseActivity activity) {
        this.activity = activity;
    }

    public JSONWebServices(BaseAdapter adapter) {
        this.adapter = adapter;
    }

    public JSONWebServices(TrackingReceiver trackingReceiver) {
        this.trackingReceiver = trackingReceiver;
    }

    public JSONWebServices(CampaignLocationsService campaignLocationsService) {
        this.campaignLocationsService = campaignLocationsService;
    }

    /**
     * make a request to get all coupons
     *
     * @param serviceParam
     */
    public JSONAsync getAllCoupons(List<NameValuePair> serviceParam) {
        String temp;
        if (MainActivity.currentFan == null)
            temp = "";
        else
            temp = "user/";
        JSONAsync call = new JSONAsync(this.fragment, temp + "home/coupons", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;

    }

    /**
     * make a request to get all coupons
     *
     * @param serviceParam
     */
    public JSONAsync getLoyaltyCoupons(List<NameValuePair> serviceParam) {
        String temp;
        if (MainActivity.currentFan == null)
            temp = "";
        else
            temp = "user/";
        JSONAsync call = new JSONAsync(this.fragment, temp + "home/coupons/loyalty", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    /**
     * make a request to get all coupons
     *
     * @param serviceParam
     */
    public JSONAsync getSpecialOffersCoupons(List<NameValuePair> serviceParam) {
        String temp;
        if (MainActivity.currentFan == null)
            temp = "";
        else
            temp = "user/";
        JSONAsync call = new JSONAsync(this.fragment, temp + "home/coupons/special-offers", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    /**
     * make a request to get single coupon
     *
     * @param serviceParam
     * @param coupSlug
     */
    public JSONAsync getSingleCoupon(List<NameValuePair> serviceParam, String coupSlug) {

        String temp;
        if (MainActivity.currentFan == null)
            temp = "";
        else
            temp = "user/";

        JSONAsync call = new JSONAsync(this.fragment, temp + "single/coupon/" + coupSlug, serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    /**
     * make a request to register a fan
     *
     * @param serviceParam
     */
    public JSONAsync registerFan(List<NameValuePair> serviceParam) {


        JSONAsync call = new JSONAsync(this.fragment, "register/facebook", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    /**
     * make a request to get user's coupons
     *
     * @param serviceParam
     */
    public JSONAsync getMyCoupons(List<NameValuePair> serviceParam) {

        JSONAsync call = new JSONAsync(this.fragment, "user/my-coupons", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    /**
     * make a request to get user's loyalty coupons
     *
     * @param serviceParam
     */
    public JSONAsync getMyLoyaltyCoupons(List<NameValuePair> serviceParam) {

        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/loyalty", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    /**
     * make a request to get user's special offers coupons
     *
     * @param serviceParam
     */
    public JSONAsync getMySpecialOffersCoupons(List<NameValuePair> serviceParam) {

        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/offers", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    /**
     * make a request to renew access token
     *
     * @param serviceParam
     */
    public JSONAsync renewAccessTokenActivity(List<NameValuePair> serviceParam) {

        JSONAsync call = new JSONAsync(this.activity, "oauth/access_token", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    /**
     * make a request to renew access token
     *
     * @param serviceParam
     */
    public JSONAsync renewAccessTokenFragment(List<NameValuePair> serviceParam) {

        JSONAsync call = new JSONAsync(this.fragment, "oauth/access_token", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    /**
     * make a request to get all filters
     */
    public JSONAsync getAllFilters() {
        String temp;
        if (MainActivity.currentFan == null)
            temp = "";
        else
            temp = "user/";

        JSONAsync call = new JSONAsync(this.fragment, temp + "home/filter", null, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    /**
     * make a request to get brand filters
     */
    public JSONAsync getBrandFilters() {
        String temp;
        if (MainActivity.currentFan == null)
            temp = "";
        else
            temp = "user/";

        JSONAsync call = new JSONAsync(this.fragment, temp + "filters", null, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    /**
     * soft event request in Activity
     */
    public JSONAsync eventRequestActivity(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.activity, "user/softevent", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    /**
     * like/dislike request in Activity
     */
    public JSONAsync likeRequestActivity(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.activity, "user/brands/like-brand-page", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    /**
     * get coupon type
     */
    public JSONAsync getCoupTypeActivity(List<NameValuePair> serviceParam, String coupSlug) {
        JSONAsync call = new JSONAsync(this.activity, "coupons/" + coupSlug + "/type", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }


    public JSONAsync eventRequestFragment(List<NameValuePair> serviceParam) {

        JSONAsync call = new JSONAsync(this.fragment, "user/softevent", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync eventRequestAdapter(List<NameValuePair> serviceParam) {

        JSONAsync call = new JSONAsync(this.adapter, "user/softevent", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync addToWalletFragment(List<NameValuePair> serviceParam, String coupSlug) {

        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/" + coupSlug + "/add-to-my-coupons", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync addToWalletActivity(List<NameValuePair> serviceParam, String coupSlug) {

        JSONAsync call = new JSONAsync(this.activity, "user/coupons/" + coupSlug + "/add-to-my-coupons", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync addToWalletAdapter(List<NameValuePair> serviceParam, String coupSlug) {

        JSONAsync call = new JSONAsync(this.adapter, "user/coupons/" + coupSlug + "/add-to-my-coupons", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync couponCommentRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/" + MainActivity.selectedCoupon.getCouponSlug() + "/add-comment", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync unRegisterGCM(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.activity, "user/gcms/unregister", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync getContestsRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/" + CouponTab.coupSlugTemp + "/additional-data", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync getSurveyRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/" + CouponTab.coupSlugTemp + "/additional-data", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync getWhoDareRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/" + CouponTab.coupSlugTemp + "/additional-data", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync sendWhoDareRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/" + CouponTab.coupSlugTemp + "/save/video", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync getCollectAndWinRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/" + CouponTab.coupSlugTemp + "/additional-data", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync sendCollectAndWinRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/scan/code", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync interactionsRequest(List<NameValuePair> serviceParam, String coupSlug) {
        JSONAsync call = new JSONAsync(this.fragment, "user/missions/" + coupSlug, serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync purchaseRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/" + CouponTab.coupSlugTemp + "/additional-data", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync groupBuyingRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/" + CouponTab.coupSlugTemp + "/additional-data", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync sendSurveyData(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/surveys/solve", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync sendContestData(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/solve/contest/webservice", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync getSettingsData(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/lookups/settings", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync sendSettingsData(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/lookups/settings", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "PUT");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync getMyProfile(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/profile", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync updateMyProfile(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/profile", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "PUT");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync createMyProfile(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "signup", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync alphaRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/" + CouponTab.coupSlugTemp + "/additional-data", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync qualifiedToCouponRequestFragment(List<NameValuePair> serviceParam, String coupSlug) {
        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/" + coupSlug + "/coupon-conditions", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync qualifiedToCouponRequestAdapter(List<NameValuePair> serviceParam) {

        JSONAsync call = new JSONAsync(this.adapter, "user/coupons/" + CouponTab.coupSlugTemp + "/coupon-conditions", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync myCouponInfoFragment(List<NameValuePair> serviceParam, String coupSlug) {
        JSONAsync call = new JSONAsync(this.fragment, "user/coupons/" + coupSlug + "/coupon-details", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync myCouponInfoAdapter(List<NameValuePair> serviceParam, String coupSlug) {

        JSONAsync call = new JSONAsync(this.adapter, "user/coupons/" + coupSlug + "/coupon-details", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync sendReferrerReceiver(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.trackingReceiver, "events/trigger/mobile/install", serviceParam, Const.webServiceURL + "en/api/", "R_GET");
        call.execute();
//        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync sendReferrerActivity(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.activity, "events/trigger/mobile/install", serviceParam, Const.webServiceURL + "en/api/", "R_GET");
        call.execute();
//        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync getActiveCountries(String lang) {
        JSONAsync call = new JSONAsync(this.activity, "locations/countries", null, Const.webServiceURL + lang + "/api/", "GET");
        call.execute();
//        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync getAllBrands(List<NameValuePair> serviceParam) {
        String url;
        if (MainActivity.currentFan == null)
            url = "brands";
        else
            url = "user/brands/grid";
        JSONAsync call = new JSONAsync(this.fragment, url, serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync getSingleBrand(List<NameValuePair> serviceParam, String brandSlug) {
        String temp;
        if (MainActivity.currentFan == null)
            temp = "";
        else
            temp = "user/";

        JSONAsync call = new JSONAsync(this.fragment, temp + "brands/" + brandSlug + "/show", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync getAllCredit(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/my-credit", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync getAllFriends(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/showFriends", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync brandUpdateCommentRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/brands/" + BrandsTab.selectedBrand.getBrandSlug() + "/update-comment", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "PUT");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync brandCreateCommentRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/brands/" + BrandsTab.selectedBrand.getBrandSlug() + "/add-comment", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync allExperiencesRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/my/experiences", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }


    public JSONAsync createExperienceRequest(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/experiences", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync getRelatedTags(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "tags/autocomplete", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync editExperienceRequest(List<NameValuePair> serviceParam, String expSlug) {
        JSONAsync call = new JSONAsync(this.fragment, "user/experiences/" + expSlug, serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "PUT");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync deleteExperienceRequest(List<NameValuePair> serviceParam, String expSlug) {
        JSONAsync call = new JSONAsync(this.adapter, "user/experiences/" + expSlug, serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "DELETE");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync getUserCampaigns(List<NameValuePair> serviceParam, String accessToken) {
        if (MainActivity.langResult == null)
            MainActivity.langResult = "en";
        JSONAsync call = new JSONAsync(this.campaignLocationsService, accessToken, "user/campaigns/location-based", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
//        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync cloudCampDeliveryReport(List<NameValuePair> serviceParam, String accessToken) {
        if (MainActivity.langResult == null)
            MainActivity.langResult = "en";
        JSONAsync call = new JSONAsync(this.campaignLocationsService, accessToken, "user/campaigns/location-based/frequency", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");
        call.execute();
//        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync cloudCampClickedReport(List<NameValuePair> serviceParam) {
        if (MainActivity.langResult == null)
            MainActivity.langResult = "en";
        JSONAsync call = new JSONAsync(this.activity, "user/campaigns/location-based/clicked", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");
        call.execute();
//        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync addSocialAccount(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "user/social-accounts/add", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "POST");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync addSocialAccount_G(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "users/profile/google-login-token", serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "GET");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync editSocialAccount(List<NameValuePair> serviceParam, String networkAccID) {
        JSONAsync call = new JSONAsync(this.fragment, "user/social-accounts/" + networkAccID, serviceParam, Const.webServiceURL + MainActivity.langResult + "/api/", "PUT");
        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }

    public JSONAsync resendVerEmail(List<NameValuePair> serviceParam) {
        JSONAsync call = new JSONAsync(this.fragment, "/send-verification", serviceParam, Const.webServiceURL + MainActivity.langResult, "GET");

        call.execute();
        MainActivity.jsonAsync = call;
        return call;
    }



}