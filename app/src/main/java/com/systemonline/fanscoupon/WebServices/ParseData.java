package com.systemonline.fanscoupon.WebServices;

import android.util.Log;

import com.systemonline.fanscoupon.EditAccountFragment;
import com.systemonline.fanscoupon.FbMainFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.MainActivity;
import com.systemonline.fanscoupon.Model.Alpha;
import com.systemonline.fanscoupon.Model.Brand;
import com.systemonline.fanscoupon.Model.BrandBranch;
import com.systemonline.fanscoupon.Model.CampaignLocation;
import com.systemonline.fanscoupon.Model.Challenge;
import com.systemonline.fanscoupon.Model.ChallengeBaseClass;
import com.systemonline.fanscoupon.Model.Choice;
import com.systemonline.fanscoupon.Model.CollectAndWin;
import com.systemonline.fanscoupon.Model.Comment;
import com.systemonline.fanscoupon.Model.Contest;
import com.systemonline.fanscoupon.Model.Country;
import com.systemonline.fanscoupon.Model.Coupon;
import com.systemonline.fanscoupon.Model.CouponConditions;
import com.systemonline.fanscoupon.Model.CouponProgram;
import com.systemonline.fanscoupon.Model.Credit;
import com.systemonline.fanscoupon.Model.CreditPerBrand;
import com.systemonline.fanscoupon.Model.CustomerType;
import com.systemonline.fanscoupon.Model.DayWorkingHour;
import com.systemonline.fanscoupon.Model.EarnedPointsAverage;
import com.systemonline.fanscoupon.Model.EventRate;
import com.systemonline.fanscoupon.Model.Experience;
import com.systemonline.fanscoupon.Model.FaceBookPage;
import com.systemonline.fanscoupon.Model.Fan;
import com.systemonline.fanscoupon.Model.Filter;
import com.systemonline.fanscoupon.Model.GroupBuyingCoupon;
import com.systemonline.fanscoupon.Model.Interaction;
import com.systemonline.fanscoupon.Model.LBC;
import com.systemonline.fanscoupon.Model.PurchaseCoupon;
import com.systemonline.fanscoupon.Model.Question;
import com.systemonline.fanscoupon.Model.SocialAccount;
import com.systemonline.fanscoupon.Model.SocialNetwork;
import com.systemonline.fanscoupon.Model.Survey;
import com.systemonline.fanscoupon.Model.TagsChip;
import com.systemonline.fanscoupon.Model.WhoDare;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.brands_tabs.BrandsTab;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;
import com.systemonline.fanscoupon.experience_tabs.ExperienceTab;
import com.systemonline.fanscoupon.experience_tabs.WriteExperienceFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class ParseData {

    /**
     * Parse fan request
     *
     * @param result
     * @return
     */
    public static Fan parseFan(JSONTokener result, Utility utility) {
        try {
            Log.e("response - register", result.toString());

            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getString("status").equals("error")) {
//                utility.showMessage(jsonObject.getString("message"), true);
                FbMainFragment.requestError = jsonObject.getString("message");
                FbMainFragment.requestErrorType = jsonObject.getString("data");
                return null;
            }
            JSONObject user = jsonObject.getJSONObject("user");
            Fan fan = new Fan();
            fan.setFanID(user.getInt("user_id"));
            fan.setFanFirstName(user.getString("first_name"));
            fan.setFanLastName(user.getString("last_name"));
            fan.setFanUserName(user.getString("user_name"));
            fan.setStopNotification(user.getBoolean("mobile_notification"));
            fan.setFanGender(user.getString("gender"));
            fan.setFanImage(user.getString("picture"));
            fan.setFanBadgesCount(user.getString("fan"));
            fan.setBigFanBadgesCount(user.getString("big_fan"));
            fan.setSuperFanBadgesCount(user.getString("super_fan"));
            fan.setStarCount(user.getInt("stars"));
            fan.setCouponCount(user.getInt("coupons"));
            fan.setFanCountryID(user.getJSONObject("country").getInt("country_id"));
            fan.setFanAccessToken(user.getJSONObject("token").getString("access_token"));
            fan.setFanAccessTokenExp(user.getJSONObject("token").getInt("expires_in"));
            return fan;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Parse single coupon data request
     *
     * @param result
     * @return
     */
    public static Boolean ParseGetSingleCoupon(JSONTokener result) {

        try {
            JSONObject jsonObject = new JSONObject(result);

            if (MainActivity.selectedCoupon.getCouponID() == 0)
                parseRestCouponData(jsonObject);

            ArrayList<EarnedPointsAverage> earnedPointsAverageList = new ArrayList<>();
            EarnedPointsAverage earnedPointsAverage;
            JSONObject purchaseSingle;
            if (jsonObject.has("purchase")) {
                JSONArray purchaseArray = jsonObject.getJSONArray("purchase");
                for (int i = 0; i < purchaseArray.length(); i++) {
                    earnedPointsAverage = new EarnedPointsAverage();
                    purchaseSingle = purchaseArray.getJSONObject(i);
                    earnedPointsAverage.setPurchaseFrom(purchaseSingle.getString("purchase_amount_from"));
                    earnedPointsAverage.setPurchaseTo(purchaseSingle.getString("purchase_amount_to"));
                    earnedPointsAverage.setPoints(purchaseSingle.getInt("points"));
                    earnedPointsAverage.setPhase(purchaseSingle.getInt("phase_id"));
                    earnedPointsAverageList.add(earnedPointsAverage);
                }
            }
            MainActivity.selectedCoupon.setEarnedPointsAverages(earnedPointsAverageList);
            if (jsonObject.isNull("details")) {
                MainActivity.selectedCoupon.setCouponConditions(null);
            } else {
                JSONObject singleCouponCondition = jsonObject.getJSONObject("details");
                CouponConditions couponConditions = new CouponConditions();
                couponConditions.setPointsExpireDays(singleCouponCondition.getString("points_expire_days"));
                couponConditions.setDueDays(singleCouponCondition.getString("due_days"));
                couponConditions.setRedemptionThreshold(singleCouponCondition.getString("redemption_threshold"));
                couponConditions.setEarnedPoints(singleCouponCondition.getString("earned_points"));
                couponConditions.setEarnedEquivalentCurrency(singleCouponCondition.getString("earned_equivilent_currency"));
                MainActivity.selectedCoupon.setCouponConditions(couponConditions);
            }

            if (jsonObject.has("points") && jsonObject.getJSONArray("points").length() > 0) {
                JSONArray eventRateJsonArray = jsonObject.getJSONArray("points");
                JSONObject singleEventRate;
                EventRate eventRate;
                ArrayList<EventRate> eventRatesArrayList = new ArrayList<>();
                for (int i = 0; i < eventRateJsonArray.length(); i++) {
                    singleEventRate = eventRateJsonArray.getJSONObject(i);
                    eventRate = new EventRate(singleEventRate.getJSONObject("event_type").getString("name"), singleEventRate.getString("points"));
                    eventRatesArrayList.add(eventRate);
                }
                MainActivity.selectedCoupon.setEventRates(eventRatesArrayList);


            }

            if (!jsonObject.isNull("offersConditions") && jsonObject.getJSONArray("offersConditions").length() > 0) {
                ArrayList<String> offerConditions = new ArrayList<>();
                for (int i = 0; i < jsonObject.getJSONArray("offersConditions").length(); i++) {
                    offerConditions.add(jsonObject.getJSONArray("offersConditions").getJSONObject(i).getString("text"));
                }
                MainActivity.selectedCoupon.setOfferConditions(offerConditions);
            }

            JSONObject couponJsonObject = jsonObject.getJSONArray("comments").getJSONObject(0);
            MainActivity.selectedCoupon.setCouponID(couponJsonObject.getInt("coupon_id"));
            MainActivity.selectedCoupon.setCouponSlug(couponJsonObject.getString("slug"));
            if (couponJsonObject.getString("feed_w_estafeed_id") != null)
                MainActivity.selectedCoupon.setAffiliate(true);
            else
                MainActivity.selectedCoupon.setAffiliate(false);
            CouponProgram couponProgram = new CouponProgram();
            couponProgram.setProgramName(couponJsonObject.getJSONObject("coupon_type").getString("label1"));
            couponProgram.setProgramDesc(couponJsonObject.getString("description"));
            couponProgram.setProgramType(MainActivity.selectedCoupon.getCouponType());
            String programTypeUsers = " ";
            if (couponJsonObject.getJSONArray("customer_types").length() > 0) {
                for (int y = 0; y < couponJsonObject.getJSONArray("customer_types").length(); y++) {
                    programTypeUsers += couponJsonObject.getJSONArray("customer_types").getJSONObject(y).getString("name") + " - ";
                }
                programTypeUsers = programTypeUsers.substring(0, programTypeUsers.lastIndexOf("-"));
            }
            couponProgram.setProgramUsers(programTypeUsers);
            MainActivity.selectedCoupon.setCouponProgram(couponProgram);

            MainActivity.selectedCoupon.setCouponStartDate(couponJsonObject.getString("available_from"));
            MainActivity.selectedCoupon.setCouponEndDate(couponJsonObject.getString("available_to"));
            MainActivity.selectedCoupon.setCouponImage(couponJsonObject.getString("main_image"));
            if (jsonObject.getInt("inMyCoupon") == 1)
                MainActivity.selectedCoupon.setHasSaved(true);
            else
                MainActivity.selectedCoupon.setHasSaved(false);

            MainActivity.selectedCoupon.setCouponSavedByCount(jsonObject.getJSONArray("comments").getJSONObject(0).getJSONArray("users_added_to_my_coupons").length());

            MainActivity.selectedCoupon.setCouponInfo(couponJsonObject.getString("description"));
            MainActivity.selectedCoupon.setCouponName(couponJsonObject.getString("title"));


            JSONArray commentsArray = jsonObject.getJSONArray("comments").getJSONObject(0).getJSONArray("comments");
            JSONObject singleComment;
            Comment comment;
            ArrayList<Comment> commentsArrayList = new ArrayList<>();
            Fan commentWriter;
            for (int i = 0; i < commentsArray.length(); i++) {
                singleComment = commentsArray.getJSONObject(i);
                comment = new Comment();
                comment.setCommentBody(singleComment.getString("comment"));
                comment.setCommentID(singleComment.getInt("comment_id"));
                commentWriter = new Fan();
                commentWriter.setFanID(singleComment.getJSONObject("user").getInt("id"));
                commentWriter.setFanFirstName(singleComment.getJSONObject("user").getString("firstname"));
                commentWriter.setFanLastName(singleComment.getJSONObject("user").getString("lastname"));
                commentWriter.setFanImage(singleComment.getJSONObject("user").getString("picture"));

                comment.setCommentWriter(commentWriter);
                commentsArrayList.add(comment);
            }
            MainActivity.selectedCoupon.setCouponComments(commentsArrayList);


            if (jsonObject.getJSONArray("comments").getJSONObject(0).getJSONArray("users_added_to_my_coupons") != null &&
                    jsonObject.getJSONArray("comments").getJSONObject(0).getJSONArray("users_added_to_my_coupons").length() > 0) {
                ArrayList<Fan> couponSavedByFans = new ArrayList<>();
                Fan couponSavedByFan;
                JSONArray couponSavedByFansArray = jsonObject.getJSONArray("comments").getJSONObject(0).getJSONArray("users_added_to_my_coupons");
                JSONObject couponSingleSavedByFan;
                for (int j = 0; j < couponSavedByFansArray.length(); j++) {
                    couponSingleSavedByFan = couponSavedByFansArray.getJSONObject(j);
                    couponSavedByFan = new Fan();
                    couponSavedByFan.setFanImage(couponSingleSavedByFan.getString("picture"));
                    couponSavedByFan.setFanID(Integer.parseInt(couponSingleSavedByFan.getString("id")));
                    couponSavedByFans.add(couponSavedByFan);
                }
                MainActivity.selectedCoupon.setCouponSavedBy(couponSavedByFans);
            }

            JSONArray customerTypesArray = jsonObject.getJSONArray("comments").getJSONObject(0).getJSONArray("customer_types");
            ArrayList<CustomerType> couponCustomerTypes = new ArrayList<>();
            CustomerType customerType;
            for (int k = 0; k < customerTypesArray.length(); k++) {
                customerType = new CustomerType();
                customerType.setCustomerTypeName(customerTypesArray.getJSONObject(k).getString("name"));
                customerType.setCustomerTypeImage(customerTypesArray.getJSONObject(k).getString("image"));
                couponCustomerTypes.add(customerType);
            }
            MainActivity.selectedCoupon.setCouponCustomerTypes(couponCustomerTypes);


            Brand brand = new Brand();

            JSONArray branches = jsonObject.getJSONArray("branches");
            JSONObject singleBranch;
            BrandBranch brandBranch;
            ArrayList<BrandBranch> brandBranchesArrayList = new ArrayList<>();
            for (int i = 0; i < branches.length(); i++) {
                singleBranch = branches.getJSONObject(i);
                brandBranch = new BrandBranch();
                brandBranch.setBranchLatitude(Double.parseDouble(singleBranch.getString("latitude")));
                brandBranch.setBranchLongitude(Double.parseDouble(singleBranch.getString("longitude")));
//                city
                if (singleBranch.getString("main_branch").equals("1"))
                    brandBranch.setMainBranch(true);
                else
                    brandBranch.setMainBranch(false);

                if (singleBranch.getString("available_for_customer").equals("1"))
                    brandBranch.setAvailable(true);
                else
                    brandBranch.setAvailable(false);

                brandBranch.setBranchName(singleBranch.getString("branch_name"));
                brandBranch.setBranchAddress(singleBranch.getString("branch_address"));
                brandBranch.setBranchCity(singleBranch.getJSONObject("city").getString("city_name"));
                brandBranchesArrayList.add(brandBranch);
            }
            brand.setBrandBranches(brandBranchesArrayList);


            JSONObject brandJsonObject = jsonObject.getJSONArray("comments").getJSONObject(0).getJSONObject("brand");
            brand.setBrandID(brandJsonObject.getInt("brand_id"));
            brand.setBrandImage(brandJsonObject.getString("logo"));
            brand.setLikedByCurrentUser(brandJsonObject.getBoolean("userIsFan"));
            brand.setBrandName(brandJsonObject.getString("brand_name"));

            ArrayList<SocialNetwork> brandSocialNetworks = new ArrayList<>();
            SocialNetwork socialNetwork;
            JSONArray brandSocialNetworksArray = brandJsonObject.getJSONArray("social_networks");
            JSONObject brandSingleSocialNetwork;
            for (int j = 0; j < brandSocialNetworksArray.length(); j++) {
                brandSingleSocialNetwork = brandSocialNetworksArray.getJSONObject(j);
                socialNetwork = new SocialNetwork();
                if (!brandSingleSocialNetwork.isNull("brand_account_id"))
                    socialNetwork.setBrandAccountID(brandSingleSocialNetwork.getString("brand_account_id"));
                socialNetwork.setBrandID(brandSingleSocialNetwork.getString("brand_id"));
                socialNetwork.setBrandAccountName(brandSingleSocialNetwork.getString("brand_account_name"));
                socialNetwork.setBrandAccountType(brandSingleSocialNetwork.getString("brand_account_type"));
                brandSocialNetworks.add(socialNetwork);
            }
            brand.setBrandSocialNetworks(brandSocialNetworks);


            ArrayList<Brand> brandAlliancesArrayList = new ArrayList<>();
            if (brandJsonObject.getJSONArray("alliences_one").length() > 0) {
                Brand brandAlliance;
                JSONArray brandAlliancesArray = brandJsonObject.getJSONArray("alliences_one");
                JSONObject brandAllianceObject;
                for (int j = 0; j < brandAlliancesArray.length(); j++) {
                    brandAllianceObject = brandAlliancesArray.getJSONObject(j);
                    brandAlliance = new Brand();
                    brandAlliance.setBrandID(brandAllianceObject.getInt("brand_id"));
                    brandAlliance.setBrandImage(brandAllianceObject.getString("logo"));
                    brandAlliancesArrayList.add(brandAlliance);
                }
            }
            if (brandJsonObject.getJSONArray("alliences_two").length() > 0) {
                Brand brandAlliance2;
                JSONArray brandAlliancesArray2 = brandJsonObject.getJSONArray("alliences_one");
                JSONObject brandAllianceObject2;
                for (int j = 0; j < brandAlliancesArray2.length(); j++) {
                    brandAllianceObject2 = brandAlliancesArray2.getJSONObject(j);
                    brandAlliance2 = new Brand();
                    brandAlliance2.setBrandID(brandAllianceObject2.getInt("brand_id"));
                    brandAlliance2.setBrandImage(brandAllianceObject2.getString("logo"));
                    brandAlliance2.setBrandName(brandAllianceObject2.getString("brand_name"));
                    brandAlliance2.setBrandSlug(brandAllianceObject2.getString("slug"));
                    brandAlliancesArrayList.add(brandAlliance2);
                }
            }
            brand.setBrandAlliances(brandAlliancesArrayList);
            if (MainActivity.selectedCoupon.getCouponBrand() != null)
                brand.setBrandRatingStarsCount(MainActivity.selectedCoupon.getCouponBrand().getBrandRatingStarsCount());
            MainActivity.selectedCoupon.setCouponBrand(brand);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("parse getSingleCoupon", e.getMessage());
            return false;
        }
    }

    private static void parseRestCouponData(JSONObject jsonObject) {
        try {

            JSONObject couponJsonObject, brandJsonObject;
            couponJsonObject = jsonObject.getJSONObject("home_data").getJSONObject("coupon_data");
            MainActivity.selectedCoupon.setCouponID(couponJsonObject.getInt("coupon_id"));
            MainActivity.selectedCoupon.setCouponName(couponJsonObject.getString("title"));
            MainActivity.selectedCoupon.setCouponInfo(couponJsonObject.getString("description"));
            MainActivity.selectedCoupon.setCouponImage(couponJsonObject.getString("image"));
            MainActivity.selectedCoupon.setCouponSlug(couponJsonObject.getString("slug"));
//            MainActivity.selectedCoupon.setCouponType(couponJsonObject.getString("type"));
//            MainActivity.selectedCoupon.setCouponType("challenges");

            try {
                MainActivity.selectedCoupon.setHasSaved(couponJsonObject.getBoolean("in_wallet"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            MainActivity.selectedCoupon.setChallengeDue(couponJsonObject.optBoolean("mission_status"));
            MainActivity.selectedCoupon.setCouponSavedByCount(couponJsonObject.getInt("no_of_users_in_wallet"));


            JSONArray customerTypesArray = couponJsonObject.getJSONArray("users_type");
            ArrayList<CustomerType> couponCustomerTypes = new ArrayList<>();
            CustomerType customerType;
            for (int k = 0; k < customerTypesArray.length(); k++) {
                customerType = new CustomerType();
                customerType.setCustomerTypeName(customerTypesArray.getJSONObject(k).getString("name"));
                customerType.setCustomerTypeImage(customerTypesArray.getJSONObject(k).getString("image"));
                couponCustomerTypes.add(customerType);
            }
            MainActivity.selectedCoupon.setCouponCustomerTypes(couponCustomerTypes);

            MainActivity.selectedCoupon.setCouponStartDate(couponJsonObject.getJSONObject("available_from").getString("date"));
            Challenge challenge;

//            if ((coupType.equals("") && MainActivity.selectedCoupType.equals("challenges"))
//                    || (coupType.equals("forYou") && MainActivity.selectedCoupTypeOnlyYou.equals("challenges"))) {
            if (MainActivity.selectedCoupon.getCouponType().equals("challenges")) {
                try {
                    JSONObject couponChallenge = couponJsonObject.getJSONObject("challenges");
                    challenge = new Challenge();
                    challenge.setOfferID(couponChallenge.getInt("offer_id"));
                    challenge.setChallengeTitle(couponChallenge.getString("title"));
                    challenge.setChallengeName(couponChallenge.getString("name"));
                    challenge.setChallengeDesc(couponChallenge.getString("offer_desc"));
                    challenge.setMissionStatus(couponJsonObject.getBoolean("mission_status"));
//                    challenge.setMissionStatus(false);
                    MainActivity.selectedCoupon.setCouponChallenge(challenge);

                    Log.e("parse challenge type", MainActivity.selectedCoupon.getCouponChallenges().getChallengeName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else// di ana 3amelha 3shan el swich elly gwa el adapter  lazem ykon el variable mawgood
            {
                challenge = new Challenge();
                challenge.setChallengeName("");
                challenge.setChallengeTitle("");
                MainActivity.selectedCoupon.setCouponChallenge(challenge);
            }

//            ArrayList<BrandBranch> brandBranches = new ArrayList<>();
//            BrandBranch singleBranch;
//            JSONArray branchesJsonArray = couponJsonObject.getJSONArray("branchs");
//            JSONObject branchJsonObject;
//            for (int j = 0; j < branchesJsonArray.length(); j++) {
//                branchJsonObject = branchesJsonArray.getJSONObject(j);
//                singleBranch = new BrandBranch();
//                singleBranch.setBranchName(branchJsonObject.getString("name"));
//                singleBranch.setBranchAddress(branchJsonObject.getString("address"));
//                singleBranch.setBranchCity(branchJsonObject.getString("city"));
//                singleBranch.setBranchLatitude(branchJsonObject.getDouble("latitude"));
//
//                singleBranch.setBranchLongitude(branchJsonObject.getDouble("longitude"));
//
//                if (branchJsonObject.getJSONArray("phones").length() > 0)
//                    singleBranch.setBranchPhone(branchJsonObject.getJSONArray("phones").getJSONObject(0).getString("tel"));
//                brandBranches.add(singleBranch);
//            }

            ///////////////////////////////   brand data

            brandJsonObject = jsonObject.getJSONObject("home_data").getJSONObject("brand_data");
            Brand brand = new Brand();

//            brand.setBrandBranches(brandBranches);

            brand.setBrandID(brandJsonObject.getInt("id"));
            brand.setBrandSlug(brandJsonObject.getString("slug"));
            brand.setBrandName(brandJsonObject.getString("name"));
            brand.setBrandRatingStarsCount(brandJsonObject.getString("rate"));
            brand.setBrandImage(brandJsonObject.getString("logo"));

            ArrayList<SocialNetwork> brandSocialNetworks = new ArrayList<>();
            SocialNetwork socialNetwork;
            JSONArray brandSocialNetworksArray = brandJsonObject.getJSONArray("social_networks");
            JSONObject brandSingleSocialNetwork;
            for (int j = 0; j < brandSocialNetworksArray.length(); j++) {
                brandSingleSocialNetwork = brandSocialNetworksArray.getJSONObject(j);
                socialNetwork = new SocialNetwork();
                if (!brandSingleSocialNetwork.isNull("brand_account_id"))
                    socialNetwork.setBrandAccountID(brandSingleSocialNetwork.getString("brand_account_id"));
                socialNetwork.setBrandAccountName(brandSingleSocialNetwork.getString("brand_account_name"));
                socialNetwork.setBrandAccountType(brandSingleSocialNetwork.getString("brand_account_type"));
                brandSocialNetworks.add(socialNetwork);
            }
            brand.setBrandSocialNetworks(brandSocialNetworks);

            if (couponJsonObject.getJSONArray("coupon_users") != null && couponJsonObject.getJSONArray("coupon_users").length() > 0) {
                ArrayList<Fan> brandFans = new ArrayList<>();
                Fan brandFan;
                JSONArray brandFansArray = couponJsonObject.getJSONArray("coupon_users");
                JSONObject brandFanJsonObject;
                for (int j = 0; j < brandFansArray.length(); j++) {
                    brandFanJsonObject = brandFansArray.getJSONObject(j);
                    brandFan = new Fan();
                    brandFan.setFanImage(brandFanJsonObject.getString("picture"));
                    brandFan.setFanID(Integer.parseInt(brandFanJsonObject.getString("id")));
                    brandFans.add(brandFan);
                }
                MainActivity.selectedCoupon.setCouponSavedBy(brandFans);
            }

            MainActivity.selectedCoupon.setCouponBrand(brand);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Parse all coupons request
     *
     * @param result
     * @return
     */
    public static ArrayList<Coupon> NEW_ParseGetCoupons(JSONTokener result, String coupType) {
        try {
            ArrayList<Coupon> couponArrayList = new ArrayList<>();
            Coupon coupon;
            JSONObject responseJsonObject = new JSONObject(result);
            JSONArray jsonArray;
            if (coupType.equals(""))
                jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupType).getJSONArray("coupons");
            else
                jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupTypeOnlyYou).getJSONArray("coupons");

            JSONObject jsonObject, couponJsonObject, brandJsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                couponJsonObject = jsonObject.getJSONObject("coupon_data");
                coupon = new Coupon();
                coupon.setCouponID(couponJsonObject.getInt("coupon_id"));
                coupon.setCouponName(couponJsonObject.getString("title"));
                coupon.setCouponInfo(couponJsonObject.getString("description"));
                coupon.setCouponImage(couponJsonObject.getString("image"));
                coupon.setCouponSlug(couponJsonObject.getString("slug"));
                coupon.setCouponType(couponJsonObject.getString("type"));
                try {
                    coupon.setHasSaved(couponJsonObject.getBoolean("in_wallet"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coupon.setChallengeDue(couponJsonObject.optBoolean("mission_status"));
                coupon.setCouponSavedByCount(couponJsonObject.getInt("no_of_users_in_wallet"));


                JSONArray customerTypesArray = couponJsonObject.getJSONArray("users_type");
                ArrayList<CustomerType> couponCustomerTypes = new ArrayList<>();
                CustomerType customerType;
                for (int k = 0; k < customerTypesArray.length(); k++) {
                    customerType = new CustomerType();
                    customerType.setCustomerTypeName(customerTypesArray.getJSONObject(k).getString("name"));
                    customerType.setCustomerTypeImage(customerTypesArray.getJSONObject(k).getString("image"));
                    couponCustomerTypes.add(customerType);
                }
                coupon.setCouponCustomerTypes(couponCustomerTypes);

                coupon.setCouponStartDate(couponJsonObject.getJSONObject("available_from").getString("date"));
                Challenge challenge;

                if ((coupType.equals("") && CouponTab.selectedCoupType.equals("challenges"))
                        || (coupType.equals("forYou") && CouponTab.selectedCoupTypeOnlyYou.equals("challenges"))) {
                    try {
                        JSONObject couponChallenge = couponJsonObject.getJSONObject("challenges");
                        challenge = new Challenge();
                        challenge.setOfferID(couponChallenge.getInt("offer_id"));
                        challenge.setChallengeTitle(couponChallenge.getString("title"));
                        challenge.setChallengeName(couponChallenge.getString("name"));
                        challenge.setChallengeDesc(couponChallenge.getString("offer_desc"));
                        challenge.setMissionStatus(couponJsonObject.getBoolean("mission_status"));
                        coupon.setCouponChallenge(challenge);

                        Log.e("parse challenge type", coupon.getCouponChallenges().getChallengeName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else// di ana 3amelha 3shan el swich elly gwa el adapter  lazem ykon el variable mawgood
                {
                    challenge = new Challenge();
                    challenge.setChallengeName("");
                    challenge.setChallengeTitle("");
                    coupon.setCouponChallenge(challenge);
                }

                ArrayList<BrandBranch> brandBranches = new ArrayList<>();
                BrandBranch singleBranch;
                JSONArray branchesJsonArray = couponJsonObject.getJSONArray("branchs");
                JSONObject branchJsonObject;
                for (int j = 0; j < branchesJsonArray.length(); j++) {
                    branchJsonObject = branchesJsonArray.getJSONObject(j);
                    singleBranch = new BrandBranch();
                    singleBranch.setBranchName(branchJsonObject.getString("name"));
                    singleBranch.setBranchAddress(branchJsonObject.getString("address"));
                    singleBranch.setBranchCity(branchJsonObject.getString("city"));
                    singleBranch.setBranchLatitude(branchJsonObject.getDouble("latitude"));

                    singleBranch.setBranchLongitude(branchJsonObject.getDouble("longitude"));

                    if (branchJsonObject.getJSONArray("phones").length() > 0)
                        singleBranch.setBranchPhone(branchJsonObject.getJSONArray("phones").getJSONObject(0).getString("tel"));
                    brandBranches.add(singleBranch);
                }

                ///////////////////////////////   brand data

                brandJsonObject = jsonObject.getJSONObject("brand_data");
                Brand brand = new Brand();

                brand.setBrandBranches(brandBranches);

                brand.setBrandID(brandJsonObject.getInt("id"));
                brand.setBrandSlug(brandJsonObject.getString("slug"));
                brand.setBrandName(brandJsonObject.getString("name"));
                brand.setBrandRatingStarsCount(brandJsonObject.getString("rate"));
                brand.setBrandImage(brandJsonObject.getString("logo"));

                ArrayList<SocialNetwork> brandSocialNetworks = new ArrayList<>();
                SocialNetwork socialNetwork;
                JSONArray brandSocialNetworksArray = brandJsonObject.getJSONArray("social_networks");
                JSONObject brandSingleSocialNetwork;
                for (int j = 0; j < brandSocialNetworksArray.length(); j++) {
                    brandSingleSocialNetwork = brandSocialNetworksArray.getJSONObject(j);
                    socialNetwork = new SocialNetwork();
                    if (!brandSingleSocialNetwork.isNull("brand_account_id"))
                        socialNetwork.setBrandAccountID(brandSingleSocialNetwork.getString("brand_account_id"));
                    socialNetwork.setBrandAccountName(brandSingleSocialNetwork.getString("brand_account_name"));
                    socialNetwork.setBrandAccountType(brandSingleSocialNetwork.getString("brand_account_type"));
                    brandSocialNetworks.add(socialNetwork);
                }
                brand.setBrandSocialNetworks(brandSocialNetworks);

                if (couponJsonObject.getJSONArray("coupon_users") != null && couponJsonObject.getJSONArray("coupon_users").length() > 0) {
                    ArrayList<Fan> brandFans = new ArrayList<>();
                    Fan brandFan;
                    JSONArray brandFansArray = couponJsonObject.getJSONArray("coupon_users");
                    JSONObject brandFanJsonObject;
                    for (int j = 0; j < brandFansArray.length(); j++) {
                        brandFanJsonObject = brandFansArray.getJSONObject(j);
                        brandFan = new Fan();
                        brandFan.setFanImage(brandFanJsonObject.getString("picture"));
                        brandFan.setFanID(Integer.parseInt(brandFanJsonObject.getString("user_id")));
                        brandFans.add(brandFan);
                    }
                    coupon.setCouponSavedBy(brandFans);
                }

                coupon.setCouponBrand(brand);
                couponArrayList.add(coupon);

            }

            return couponArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("parse getCoupons", e.getMessage());
            return null;
        }
    }

    public static ArrayList<ArrayList<Coupon>> NEW_ParseGetCoupons2(JSONTokener result, String coupType) {
        try {
            ArrayList<ArrayList<Coupon>> allCoupons = new ArrayList<>();
            ArrayList<Coupon> couponArrayList = new ArrayList<>();
            Coupon coupon;
            JSONObject responseJsonObject = new JSONObject(result);
            JSONArray jsonArray;
            JSONObject jsonObject, couponJsonObject, brandJsonObject;

            CouponTab.selectedCoupType = "loyalty";
            CouponTab.selectedCoupTypeOnlyYou = "loyalty";
            if (responseJsonObject.has(CouponTab.selectedCoupType)) {

                if (coupType.equals(""))
                    jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupType).getJSONArray("coupons");
                else
                    jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupTypeOnlyYou).getJSONArray("coupons");

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    couponJsonObject = jsonObject.getJSONObject("coupon_data");
                    coupon = new Coupon();
                    coupon.setCouponID(couponJsonObject.getInt("coupon_id"));
                    coupon.setCouponName(couponJsonObject.getString("title"));
                    coupon.setCouponInfo(couponJsonObject.getString("description"));
                    coupon.setCouponImage(couponJsonObject.getString("image"));
                    coupon.setCouponSlug(couponJsonObject.getString("slug"));
                    coupon.setCouponType(couponJsonObject.getString("type"));
                    coupon.setCouponTypeID(95);
                    try {
                        coupon.setHasSaved(couponJsonObject.getBoolean("in_wallet"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    coupon.setChallengeDue(couponJsonObject.optBoolean("mission_status"));
                    coupon.setCouponSavedByCount(couponJsonObject.getInt("no_of_users_in_wallet"));


                    JSONArray customerTypesArray = couponJsonObject.getJSONArray("users_type");
                    ArrayList<CustomerType> couponCustomerTypes = new ArrayList<>();
                    CustomerType customerType;
                    for (int k = 0; k < customerTypesArray.length(); k++) {
                        customerType = new CustomerType();
                        customerType.setCustomerTypeName(customerTypesArray.getJSONObject(k).getString("name"));
                        customerType.setCustomerTypeImage(customerTypesArray.getJSONObject(k).getString("image"));
                        couponCustomerTypes.add(customerType);
                    }
                    coupon.setCouponCustomerTypes(couponCustomerTypes);

                    coupon.setCouponStartDate(couponJsonObject.getJSONObject("available_from").getString("date"));
                    Challenge challenge;

                    if ((coupType.equals("") && CouponTab.selectedCoupType.equals("challenges"))
                            || (coupType.equals("forYou") && CouponTab.selectedCoupTypeOnlyYou.equals("challenges"))) {
                        try {
                            JSONObject couponChallenge = couponJsonObject.getJSONObject("challenges");
                            challenge = new Challenge();
                            challenge.setOfferID(couponChallenge.getInt("offer_id"));
                            challenge.setChallengeTitle(couponChallenge.getString("title"));
                            challenge.setChallengeName(couponChallenge.getString("name"));
                            challenge.setChallengeDesc(couponChallenge.getString("offer_desc"));
                            challenge.setMissionStatus(couponJsonObject.getBoolean("mission_status"));
                            coupon.setCouponChallenge(challenge);

                            Log.e("parse challenge type", coupon.getCouponChallenges().getChallengeName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else// di ana 3amelha 3shan el swich elly gwa el adapter  lazem ykon el variable mawgood
                    {
                        challenge = new Challenge();
                        challenge.setChallengeName("");
                        challenge.setChallengeTitle("");
                        coupon.setCouponChallenge(challenge);
                    }

                    ArrayList<BrandBranch> brandBranches = new ArrayList<>();
                    BrandBranch singleBranch;
                    JSONArray branchesJsonArray = couponJsonObject.getJSONArray("branchs");
                    JSONObject branchJsonObject;
                    for (int j = 0; j < branchesJsonArray.length(); j++) {
                        branchJsonObject = branchesJsonArray.getJSONObject(j);
                        singleBranch = new BrandBranch();
                        singleBranch.setBranchName(branchJsonObject.getString("name"));
                        singleBranch.setBranchAddress(branchJsonObject.getString("address"));
                        singleBranch.setBranchCity(branchJsonObject.getString("city"));
                        singleBranch.setBranchLatitude(branchJsonObject.getDouble("latitude"));

                        singleBranch.setBranchLongitude(branchJsonObject.getDouble("longitude"));

                        if (branchJsonObject.getJSONArray("phones").length() > 0)
                            singleBranch.setBranchPhone(branchJsonObject.getJSONArray("phones").getJSONObject(0).getString("tel"));
                        brandBranches.add(singleBranch);
                    }

                    ///////////////////////////////   brand data

                    brandJsonObject = jsonObject.getJSONObject("brand_data");
                    Brand brand = new Brand();

                    brand.setBrandBranches(brandBranches);

                    brand.setBrandID(brandJsonObject.getInt("id"));
                    brand.setBrandSlug(brandJsonObject.getString("slug"));
                    brand.setBrandName(brandJsonObject.getString("name"));
                    brand.setBrandRatingStarsCount(brandJsonObject.getString("rate"));
                    brand.setBrandImage(brandJsonObject.getString("logo"));

                    ArrayList<SocialNetwork> brandSocialNetworks = new ArrayList<>();
                    SocialNetwork socialNetwork;
                    JSONArray brandSocialNetworksArray = brandJsonObject.getJSONArray("social_networks");
                    JSONObject brandSingleSocialNetwork;
                    for (int j = 0; j < brandSocialNetworksArray.length(); j++) {
                        brandSingleSocialNetwork = brandSocialNetworksArray.getJSONObject(j);
                        socialNetwork = new SocialNetwork();
                        if (!brandSingleSocialNetwork.isNull("brand_account_id"))
                            socialNetwork.setBrandAccountID(brandSingleSocialNetwork.getString("brand_account_id"));
                        socialNetwork.setBrandAccountName(brandSingleSocialNetwork.getString("brand_account_name"));
                        socialNetwork.setBrandAccountType(brandSingleSocialNetwork.getString("brand_account_type"));
                        brandSocialNetworks.add(socialNetwork);
                    }
                    brand.setBrandSocialNetworks(brandSocialNetworks);

                    if (couponJsonObject.getJSONArray("coupon_users") != null && couponJsonObject.getJSONArray("coupon_users").length() > 0) {
                        ArrayList<Fan> brandFans = new ArrayList<>();
                        Fan brandFan;
                        JSONArray brandFansArray = couponJsonObject.getJSONArray("coupon_users");
                        JSONObject brandFanJsonObject;
                        for (int j = 0; j < brandFansArray.length(); j++) {
                            brandFanJsonObject = brandFansArray.getJSONObject(j);
                            brandFan = new Fan();
                            brandFan.setFanImage(brandFanJsonObject.getString("picture"));
                            brandFan.setFanID(Integer.parseInt(brandFanJsonObject.getString("user_id")));
                            brandFans.add(brandFan);
                        }
                        coupon.setCouponSavedBy(brandFans);
                    }

                    coupon.setCouponBrand(brand);
                    couponArrayList.add(coupon);

                }
            }
            allCoupons.add(couponArrayList);

            ////////////////////////////////////////////////////////////

            CouponTab.selectedCoupType = "specialOffers";
            CouponTab.selectedCoupTypeOnlyYou = "specialOffers";
            couponArrayList = new ArrayList<>();
            if (responseJsonObject.has(CouponTab.selectedCoupType)) {

                if (coupType.equals(""))
                    jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupType).getJSONArray("coupons");
                else
                    jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupTypeOnlyYou).getJSONArray("coupons");

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    couponJsonObject = jsonObject.getJSONObject("coupon_data");
                    coupon = new Coupon();
                    coupon.setCouponID(couponJsonObject.getInt("coupon_id"));
                    coupon.setCouponName(couponJsonObject.getString("title"));
                    coupon.setCouponInfo(couponJsonObject.getString("description"));
                    coupon.setCouponImage(couponJsonObject.getString("image"));
                    coupon.setCouponSlug(couponJsonObject.getString("slug"));
                    coupon.setCouponType(couponJsonObject.getString("type"));
                    coupon.setCouponTypeID(93);
                    try {
                        coupon.setHasSaved(couponJsonObject.getBoolean("in_wallet"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    coupon.setChallengeDue(couponJsonObject.optBoolean("mission_status"));
                    coupon.setCouponSavedByCount(couponJsonObject.getInt("no_of_users_in_wallet"));


                    JSONArray customerTypesArray = couponJsonObject.getJSONArray("users_type");
                    ArrayList<CustomerType> couponCustomerTypes = new ArrayList<>();
                    CustomerType customerType;
                    for (int k = 0; k < customerTypesArray.length(); k++) {
                        customerType = new CustomerType();
                        customerType.setCustomerTypeName(customerTypesArray.getJSONObject(k).getString("name"));
                        customerType.setCustomerTypeImage(customerTypesArray.getJSONObject(k).getString("image"));
                        couponCustomerTypes.add(customerType);
                    }
                    coupon.setCouponCustomerTypes(couponCustomerTypes);

                    coupon.setCouponStartDate(couponJsonObject.getJSONObject("available_from").getString("date"));
                    Challenge challenge;

                    if ((coupType.equals("") && CouponTab.selectedCoupType.equals("challenges"))
                            || (coupType.equals("forYou") && CouponTab.selectedCoupTypeOnlyYou.equals("challenges"))) {
                        try {
                            JSONObject couponChallenge = couponJsonObject.getJSONObject("challenges");
                            challenge = new Challenge();
                            challenge.setOfferID(couponChallenge.getInt("offer_id"));
                            challenge.setChallengeTitle(couponChallenge.getString("title"));
                            challenge.setChallengeName(couponChallenge.getString("name"));
                            challenge.setChallengeDesc(couponChallenge.getString("offer_desc"));
                            challenge.setMissionStatus(couponJsonObject.getBoolean("mission_status"));
                            coupon.setCouponChallenge(challenge);

                            Log.e("parse challenge type", coupon.getCouponChallenges().getChallengeName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else// di ana 3amelha 3shan el swich elly gwa el adapter  lazem ykon el variable mawgood
                    {
                        challenge = new Challenge();
                        challenge.setChallengeName("");
                        challenge.setChallengeTitle("");
                        coupon.setCouponChallenge(challenge);
                    }

                    ArrayList<BrandBranch> brandBranches = new ArrayList<>();
                    BrandBranch singleBranch;
                    JSONArray branchesJsonArray = couponJsonObject.getJSONArray("branchs");
                    JSONObject branchJsonObject;
                    for (int j = 0; j < branchesJsonArray.length(); j++) {
                        branchJsonObject = branchesJsonArray.getJSONObject(j);
                        singleBranch = new BrandBranch();
                        singleBranch.setBranchName(branchJsonObject.getString("name"));
                        singleBranch.setBranchAddress(branchJsonObject.getString("address"));
                        singleBranch.setBranchCity(branchJsonObject.getString("city"));
                        singleBranch.setBranchLatitude(branchJsonObject.getDouble("latitude"));

                        singleBranch.setBranchLongitude(branchJsonObject.getDouble("longitude"));

                        if (branchJsonObject.getJSONArray("phones").length() > 0)
                            singleBranch.setBranchPhone(branchJsonObject.getJSONArray("phones").getJSONObject(0).getString("tel"));
                        brandBranches.add(singleBranch);
                    }

                    ///////////////////////////////   brand data

                    brandJsonObject = jsonObject.getJSONObject("brand_data");
                    Brand brand = new Brand();

                    brand.setBrandBranches(brandBranches);

                    brand.setBrandID(brandJsonObject.getInt("id"));
                    brand.setBrandSlug(brandJsonObject.getString("slug"));
                    brand.setBrandName(brandJsonObject.getString("name"));
                    brand.setBrandRatingStarsCount(brandJsonObject.getString("rate"));
                    brand.setBrandImage(brandJsonObject.getString("logo"));

                    ArrayList<SocialNetwork> brandSocialNetworks = new ArrayList<>();
                    SocialNetwork socialNetwork;
                    JSONArray brandSocialNetworksArray = brandJsonObject.getJSONArray("social_networks");
                    JSONObject brandSingleSocialNetwork;
                    for (int j = 0; j < brandSocialNetworksArray.length(); j++) {
                        brandSingleSocialNetwork = brandSocialNetworksArray.getJSONObject(j);
                        socialNetwork = new SocialNetwork();
                        if (!brandSingleSocialNetwork.isNull("brand_account_id"))
                            socialNetwork.setBrandAccountID(brandSingleSocialNetwork.getString("brand_account_id"));
                        socialNetwork.setBrandAccountName(brandSingleSocialNetwork.getString("brand_account_name"));
                        socialNetwork.setBrandAccountType(brandSingleSocialNetwork.getString("brand_account_type"));
                        brandSocialNetworks.add(socialNetwork);
                    }
                    brand.setBrandSocialNetworks(brandSocialNetworks);

                    if (couponJsonObject.getJSONArray("coupon_users") != null && couponJsonObject.getJSONArray("coupon_users").length() > 0) {
                        ArrayList<Fan> brandFans = new ArrayList<>();
                        Fan brandFan;
                        JSONArray brandFansArray = couponJsonObject.getJSONArray("coupon_users");
                        JSONObject brandFanJsonObject;
                        for (int j = 0; j < brandFansArray.length(); j++) {
                            brandFanJsonObject = brandFansArray.getJSONObject(j);
                            brandFan = new Fan();
                            brandFan.setFanImage(brandFanJsonObject.getString("picture"));
                            brandFan.setFanID(Integer.parseInt(brandFanJsonObject.getString("user_id")));
                            brandFans.add(brandFan);
                        }
                        coupon.setCouponSavedBy(brandFans);
                    }

                    coupon.setCouponBrand(brand);
                    couponArrayList.add(coupon);

                }
            }
            allCoupons.add(couponArrayList);

            ////////////////////////////////////////////////////////////

            CouponTab.selectedCoupType = "challenges";
            CouponTab.selectedCoupTypeOnlyYou = "challenges";
            couponArrayList = new ArrayList<>();
            if (responseJsonObject.has(CouponTab.selectedCoupType)) {
                if (coupType.equals(""))
                    jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupType).getJSONArray("coupons");
                else
                    jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupTypeOnlyYou).getJSONArray("coupons");

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    couponJsonObject = jsonObject.getJSONObject("coupon_data");
                    coupon = new Coupon();
                    coupon.setCouponID(couponJsonObject.getInt("coupon_id"));
                    coupon.setCouponName(couponJsonObject.getString("title"));
                    coupon.setCouponInfo(couponJsonObject.getString("description"));
                    coupon.setCouponImage(couponJsonObject.getString("image"));
                    coupon.setCouponSlug(couponJsonObject.getString("slug"));
                    coupon.setCouponType(couponJsonObject.getString("type"));
                    coupon.setCouponTypeID(232);
                    try {
                        coupon.setHasSaved(couponJsonObject.getBoolean("in_wallet"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    coupon.setChallengeDue(couponJsonObject.optBoolean("mission_status"));
                    coupon.setCouponSavedByCount(couponJsonObject.getInt("no_of_users_in_wallet"));


                    JSONArray customerTypesArray = couponJsonObject.getJSONArray("users_type");
                    ArrayList<CustomerType> couponCustomerTypes = new ArrayList<>();
                    CustomerType customerType;
                    for (int k = 0; k < customerTypesArray.length(); k++) {
                        customerType = new CustomerType();
                        customerType.setCustomerTypeName(customerTypesArray.getJSONObject(k).getString("name"));
                        customerType.setCustomerTypeImage(customerTypesArray.getJSONObject(k).getString("image"));
                        couponCustomerTypes.add(customerType);
                    }
                    coupon.setCouponCustomerTypes(couponCustomerTypes);

                    coupon.setCouponStartDate(couponJsonObject.getJSONObject("available_from").getString("date"));
                    Challenge challenge;

                    if ((coupType.equals("") && CouponTab.selectedCoupType.equals("challenges"))
                            || (coupType.equals("forYou") && CouponTab.selectedCoupTypeOnlyYou.equals("challenges"))) {
                        try {
                            JSONObject couponChallenge = couponJsonObject.getJSONObject("challenges");
                            challenge = new Challenge();
                            challenge.setOfferID(couponChallenge.getInt("offer_id"));
                            challenge.setChallengeTitle(couponChallenge.getString("title"));
                            challenge.setChallengeName(couponChallenge.getString("name"));
                            challenge.setChallengeDesc(couponChallenge.getString("offer_desc"));
                            challenge.setMissionStatus(couponJsonObject.getBoolean("mission_status"));
                            coupon.setCouponChallenge(challenge);

                            Log.e("parse challenge type", coupon.getCouponChallenges().getChallengeName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else// di ana 3amelha 3shan el swich elly gwa el adapter  lazem ykon el variable mawgood
                    {
                        challenge = new Challenge();
                        challenge.setChallengeName("");
                        challenge.setChallengeTitle("");
                        coupon.setCouponChallenge(challenge);
                    }

                    ArrayList<BrandBranch> brandBranches = new ArrayList<>();
                    BrandBranch singleBranch;
                    JSONArray branchesJsonArray = couponJsonObject.getJSONArray("branchs");
                    JSONObject branchJsonObject;
                    for (int j = 0; j < branchesJsonArray.length(); j++) {
                        branchJsonObject = branchesJsonArray.getJSONObject(j);
                        singleBranch = new BrandBranch();
                        singleBranch.setBranchName(branchJsonObject.getString("name"));
                        singleBranch.setBranchAddress(branchJsonObject.getString("address"));
                        singleBranch.setBranchCity(branchJsonObject.getString("city"));
                        singleBranch.setBranchLatitude(branchJsonObject.getDouble("latitude"));

                        singleBranch.setBranchLongitude(branchJsonObject.getDouble("longitude"));

                        if (branchJsonObject.getJSONArray("phones").length() > 0)
                            singleBranch.setBranchPhone(branchJsonObject.getJSONArray("phones").getJSONObject(0).getString("tel"));
                        brandBranches.add(singleBranch);
                    }

                    ///////////////////////////////   brand data

                    brandJsonObject = jsonObject.getJSONObject("brand_data");
                    Brand brand = new Brand();

                    brand.setBrandBranches(brandBranches);

                    brand.setBrandID(brandJsonObject.getInt("id"));
                    brand.setBrandSlug(brandJsonObject.getString("slug"));
                    brand.setBrandName(brandJsonObject.getString("name"));
                    brand.setBrandRatingStarsCount(brandJsonObject.getString("rate"));
                    brand.setBrandImage(brandJsonObject.getString("logo"));

                    ArrayList<SocialNetwork> brandSocialNetworks = new ArrayList<>();
                    SocialNetwork socialNetwork;
                    JSONArray brandSocialNetworksArray = brandJsonObject.getJSONArray("social_networks");
                    JSONObject brandSingleSocialNetwork;
                    for (int j = 0; j < brandSocialNetworksArray.length(); j++) {
                        brandSingleSocialNetwork = brandSocialNetworksArray.getJSONObject(j);
                        socialNetwork = new SocialNetwork();
                        if (!brandSingleSocialNetwork.isNull("brand_account_id"))
                            socialNetwork.setBrandAccountID(brandSingleSocialNetwork.getString("brand_account_id"));
                        socialNetwork.setBrandAccountName(brandSingleSocialNetwork.getString("brand_account_name"));
                        socialNetwork.setBrandAccountType(brandSingleSocialNetwork.getString("brand_account_type"));
                        brandSocialNetworks.add(socialNetwork);
                    }
                    brand.setBrandSocialNetworks(brandSocialNetworks);

                    if (couponJsonObject.getJSONArray("coupon_users") != null && couponJsonObject.getJSONArray("coupon_users").length() > 0) {
                        ArrayList<Fan> brandFans = new ArrayList<>();
                        Fan brandFan;
                        JSONArray brandFansArray = couponJsonObject.getJSONArray("coupon_users");
                        JSONObject brandFanJsonObject;
                        for (int j = 0; j < brandFansArray.length(); j++) {
                            brandFanJsonObject = brandFansArray.getJSONObject(j);
                            brandFan = new Fan();
                            brandFan.setFanImage(brandFanJsonObject.getString("picture"));
                            brandFan.setFanID(Integer.parseInt(brandFanJsonObject.getString("user_id")));
                            brandFans.add(brandFan);
                        }
                        coupon.setCouponSavedBy(brandFans);
                    }

                    coupon.setCouponBrand(brand);
                    couponArrayList.add(coupon);

                }
            }
            allCoupons.add(couponArrayList);

            ////////////////////////////////////////////////////////////

            CouponTab.selectedCoupType = "luckyHour";
            CouponTab.selectedCoupTypeOnlyYou = "luckyHour";
            couponArrayList = new ArrayList<>();
            if (responseJsonObject.has(CouponTab.selectedCoupType)) {

                if (coupType.equals(""))
                    jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupType).getJSONArray("coupons");
                else
                    jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupTypeOnlyYou).getJSONArray("coupons");

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    couponJsonObject = jsonObject.getJSONObject("coupon_data");
                    coupon = new Coupon();
                    coupon.setCouponID(couponJsonObject.getInt("coupon_id"));
                    coupon.setCouponName(couponJsonObject.getString("title"));
                    coupon.setCouponInfo(couponJsonObject.getString("description"));
                    coupon.setCouponImage(couponJsonObject.getString("image"));
                    coupon.setCouponSlug(couponJsonObject.getString("slug"));
                    coupon.setCouponType(couponJsonObject.getString("type"));
                    coupon.setCouponTypeID(238);
                    try {
                        coupon.setHasSaved(couponJsonObject.getBoolean("in_wallet"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    coupon.setChallengeDue(couponJsonObject.optBoolean("mission_status"));
                    coupon.setCouponSavedByCount(couponJsonObject.getInt("no_of_users_in_wallet"));


                    JSONArray customerTypesArray = couponJsonObject.getJSONArray("users_type");
                    ArrayList<CustomerType> couponCustomerTypes = new ArrayList<>();
                    CustomerType customerType;
                    for (int k = 0; k < customerTypesArray.length(); k++) {
                        customerType = new CustomerType();
                        customerType.setCustomerTypeName(customerTypesArray.getJSONObject(k).getString("name"));
                        customerType.setCustomerTypeImage(customerTypesArray.getJSONObject(k).getString("image"));
                        couponCustomerTypes.add(customerType);
                    }
                    coupon.setCouponCustomerTypes(couponCustomerTypes);

                    coupon.setCouponStartDate(couponJsonObject.getJSONObject("available_from").getString("date"));
                    Challenge challenge;

                    if ((coupType.equals("") && CouponTab.selectedCoupType.equals("challenges"))
                            || (coupType.equals("forYou") && CouponTab.selectedCoupTypeOnlyYou.equals("challenges"))) {
                        try {
                            JSONObject couponChallenge = couponJsonObject.getJSONObject("challenges");
                            challenge = new Challenge();
                            challenge.setOfferID(couponChallenge.getInt("offer_id"));
                            challenge.setChallengeTitle(couponChallenge.getString("title"));
                            challenge.setChallengeName(couponChallenge.getString("name"));
                            challenge.setChallengeDesc(couponChallenge.getString("offer_desc"));
                            challenge.setMissionStatus(couponJsonObject.getBoolean("mission_status"));
                            coupon.setCouponChallenge(challenge);

                            Log.e("parse challenge type", coupon.getCouponChallenges().getChallengeName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else// di ana 3amelha 3shan el swich elly gwa el adapter  lazem ykon el variable mawgood
                    {
                        challenge = new Challenge();
                        challenge.setChallengeName("");
                        challenge.setChallengeTitle("");
                        coupon.setCouponChallenge(challenge);
                    }

                    ArrayList<BrandBranch> brandBranches = new ArrayList<>();
                    BrandBranch singleBranch;
                    JSONArray branchesJsonArray = couponJsonObject.getJSONArray("branchs");
                    JSONObject branchJsonObject;
                    for (int j = 0; j < branchesJsonArray.length(); j++) {
                        branchJsonObject = branchesJsonArray.getJSONObject(j);
                        singleBranch = new BrandBranch();
                        singleBranch.setBranchName(branchJsonObject.getString("name"));
                        singleBranch.setBranchAddress(branchJsonObject.getString("address"));
                        singleBranch.setBranchCity(branchJsonObject.getString("city"));
                        singleBranch.setBranchLatitude(branchJsonObject.getDouble("latitude"));

                        singleBranch.setBranchLongitude(branchJsonObject.getDouble("longitude"));

                        if (branchJsonObject.getJSONArray("phones").length() > 0)
                            singleBranch.setBranchPhone(branchJsonObject.getJSONArray("phones").getJSONObject(0).getString("tel"));
                        brandBranches.add(singleBranch);
                    }

                    ///////////////////////////////   brand data

                    brandJsonObject = jsonObject.getJSONObject("brand_data");
                    Brand brand = new Brand();

                    brand.setBrandBranches(brandBranches);

                    brand.setBrandID(brandJsonObject.getInt("id"));
                    brand.setBrandSlug(brandJsonObject.getString("slug"));
                    brand.setBrandName(brandJsonObject.getString("name"));
                    brand.setBrandRatingStarsCount(brandJsonObject.getString("rate"));
                    brand.setBrandImage(brandJsonObject.getString("logo"));

                    ArrayList<SocialNetwork> brandSocialNetworks = new ArrayList<>();
                    SocialNetwork socialNetwork;
                    JSONArray brandSocialNetworksArray = brandJsonObject.getJSONArray("social_networks");
                    JSONObject brandSingleSocialNetwork;
                    for (int j = 0; j < brandSocialNetworksArray.length(); j++) {
                        brandSingleSocialNetwork = brandSocialNetworksArray.getJSONObject(j);
                        socialNetwork = new SocialNetwork();
                        if (!brandSingleSocialNetwork.isNull("brand_account_id"))
                            socialNetwork.setBrandAccountID(brandSingleSocialNetwork.getString("brand_account_id"));
                        socialNetwork.setBrandAccountName(brandSingleSocialNetwork.getString("brand_account_name"));
                        socialNetwork.setBrandAccountType(brandSingleSocialNetwork.getString("brand_account_type"));
                        brandSocialNetworks.add(socialNetwork);
                    }
                    brand.setBrandSocialNetworks(brandSocialNetworks);

                    if (couponJsonObject.getJSONArray("coupon_users") != null && couponJsonObject.getJSONArray("coupon_users").length() > 0) {
                        ArrayList<Fan> brandFans = new ArrayList<>();
                        Fan brandFan;
                        JSONArray brandFansArray = couponJsonObject.getJSONArray("coupon_users");
                        JSONObject brandFanJsonObject;
                        for (int j = 0; j < brandFansArray.length(); j++) {
                            brandFanJsonObject = brandFansArray.getJSONObject(j);
                            brandFan = new Fan();
                            brandFan.setFanImage(brandFanJsonObject.getString("picture"));
                            brandFan.setFanID(Integer.parseInt(brandFanJsonObject.getString("user_id")));
                            brandFans.add(brandFan);
                        }
                        coupon.setCouponSavedBy(brandFans);
                    }

                    coupon.setCouponBrand(brand);
                    couponArrayList.add(coupon);

                }
            }
            allCoupons.add(couponArrayList);

            ////////////////////////////////////////////////////////////

            CouponTab.selectedCoupType = "specialOccasion";
            CouponTab.selectedCoupTypeOnlyYou = "specialOccasion";
            couponArrayList = new ArrayList<>();

            try {
                if (responseJsonObject.has(CouponTab.selectedCoupType)) {

                    if (coupType.equals("")) {
                        jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupType).
                                getJSONObject("coupons").getJSONObject("1").getJSONArray("coupons");
                    } else {
                        jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupTypeOnlyYou).
                                getJSONObject("coupons").getJSONObject("1").getJSONArray("coupons");
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        couponJsonObject = jsonObject.getJSONObject("coupon_data");
                        coupon = new Coupon();
                        coupon.setCouponID(couponJsonObject.getInt("coupon_id"));
                        coupon.setCouponName(couponJsonObject.getString("title"));
                        coupon.setCouponInfo(couponJsonObject.getString("description"));
                        coupon.setCouponImage(couponJsonObject.getString("image"));
                        coupon.setCouponSlug(couponJsonObject.getString("slug"));
                        coupon.setCouponType(couponJsonObject.getString("type"));
                        coupon.setCouponTypeID(240);
                        try {
                            coupon.setHasSaved(couponJsonObject.getBoolean("in_wallet"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        coupon.setChallengeDue(couponJsonObject.optBoolean("mission_status"));
                        coupon.setCouponSavedByCount(couponJsonObject.getInt("no_of_users_in_wallet"));


                        JSONArray customerTypesArray = couponJsonObject.getJSONArray("users_type");
                        ArrayList<CustomerType> couponCustomerTypes = new ArrayList<>();
                        CustomerType customerType;
                        for (int k = 0; k < customerTypesArray.length(); k++) {
                            customerType = new CustomerType();
                            customerType.setCustomerTypeName(customerTypesArray.getJSONObject(k).getString("name"));
                            customerType.setCustomerTypeImage(customerTypesArray.getJSONObject(k).getString("image"));
                            couponCustomerTypes.add(customerType);
                        }
                        coupon.setCouponCustomerTypes(couponCustomerTypes);

                        coupon.setCouponStartDate(couponJsonObject.getJSONObject("available_from").getString("date"));
                        Challenge challenge;

                        if ((coupType.equals("") && CouponTab.selectedCoupType.equals("challenges"))
                                || (coupType.equals("forYou") && CouponTab.selectedCoupTypeOnlyYou.equals("challenges"))) {
                            try {
                                JSONObject couponChallenge = couponJsonObject.getJSONObject("challenges");
                                challenge = new Challenge();
                                challenge.setOfferID(couponChallenge.getInt("offer_id"));
                                challenge.setChallengeTitle(couponChallenge.getString("title"));
                                challenge.setChallengeName(couponChallenge.getString("name"));
                                challenge.setChallengeDesc(couponChallenge.getString("offer_desc"));
                                challenge.setMissionStatus(couponJsonObject.getBoolean("mission_status"));
                                coupon.setCouponChallenge(challenge);

                                Log.e("parse challenge type", coupon.getCouponChallenges().getChallengeName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else// di ana 3amelha 3shan el swich elly gwa el adapter  lazem ykon el variable mawgood
                        {
                            challenge = new Challenge();
                            challenge.setChallengeName("");
                            challenge.setChallengeTitle("");
                            coupon.setCouponChallenge(challenge);
                        }

                        ArrayList<BrandBranch> brandBranches = new ArrayList<>();
                        BrandBranch singleBranch;
                        JSONArray branchesJsonArray = couponJsonObject.getJSONArray("branchs");
                        JSONObject branchJsonObject;
                        for (int j = 0; j < branchesJsonArray.length(); j++) {
                            branchJsonObject = branchesJsonArray.getJSONObject(j);
                            singleBranch = new BrandBranch();
                            singleBranch.setBranchName(branchJsonObject.getString("name"));
                            singleBranch.setBranchAddress(branchJsonObject.getString("address"));
                            singleBranch.setBranchCity(branchJsonObject.getString("city"));
                            singleBranch.setBranchLatitude(branchJsonObject.getDouble("latitude"));

                            singleBranch.setBranchLongitude(branchJsonObject.getDouble("longitude"));

                            if (branchJsonObject.getJSONArray("phones").length() > 0)
                                singleBranch.setBranchPhone(branchJsonObject.getJSONArray("phones").getJSONObject(0).getString("tel"));
                            brandBranches.add(singleBranch);
                        }

                        ///////////////////////////////   brand data

                        brandJsonObject = jsonObject.getJSONObject("brand_data");
                        Brand brand = new Brand();

                        brand.setBrandBranches(brandBranches);

                        brand.setBrandID(brandJsonObject.getInt("id"));
                        brand.setBrandSlug(brandJsonObject.getString("slug"));
                        brand.setBrandName(brandJsonObject.getString("name"));
                        brand.setBrandRatingStarsCount(brandJsonObject.getString("rate"));
                        brand.setBrandImage(brandJsonObject.getString("logo"));

                        ArrayList<SocialNetwork> brandSocialNetworks = new ArrayList<>();
                        SocialNetwork socialNetwork;
                        JSONArray brandSocialNetworksArray = brandJsonObject.getJSONArray("social_networks");
                        JSONObject brandSingleSocialNetwork;
                        for (int j = 0; j < brandSocialNetworksArray.length(); j++) {
                            brandSingleSocialNetwork = brandSocialNetworksArray.getJSONObject(j);
                            socialNetwork = new SocialNetwork();
                            if (!brandSingleSocialNetwork.isNull("brand_account_id"))
                                socialNetwork.setBrandAccountID(brandSingleSocialNetwork.getString("brand_account_id"));
                            socialNetwork.setBrandAccountName(brandSingleSocialNetwork.getString("brand_account_name"));
                            socialNetwork.setBrandAccountType(brandSingleSocialNetwork.getString("brand_account_type"));
                            brandSocialNetworks.add(socialNetwork);
                        }
                        brand.setBrandSocialNetworks(brandSocialNetworks);

                        if (couponJsonObject.getJSONArray("coupon_users") != null && couponJsonObject.getJSONArray("coupon_users").length() > 0) {
                            ArrayList<Fan> brandFans = new ArrayList<>();
                            Fan brandFan;
                            JSONArray brandFansArray = couponJsonObject.getJSONArray("coupon_users");
                            JSONObject brandFanJsonObject;
                            for (int j = 0; j < brandFansArray.length(); j++) {
                                brandFanJsonObject = brandFansArray.getJSONObject(j);
                                brandFan = new Fan();
                                brandFan.setFanImage(brandFanJsonObject.getString("picture"));
                                brandFan.setFanID(Integer.parseInt(brandFanJsonObject.getString("user_id")));
                                brandFans.add(brandFan);
                            }
                            coupon.setCouponSavedBy(brandFans);
                        }

                        coupon.setCouponBrand(brand);
                        couponArrayList.add(coupon);

                    }
                }
            } catch (Exception e) {

            }
            allCoupons.add(couponArrayList);

            ////////////////////////////////////////////////////////////

            CouponTab.selectedCoupType = "forYou";
            CouponTab.selectedCoupTypeOnlyYou = "forYou";
            couponArrayList = new ArrayList<>();
            if (responseJsonObject.has(CouponTab.selectedCoupType)) {

                if (coupType.equals(""))
                    jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupType).getJSONArray("coupons");
                else
                    jsonArray = responseJsonObject.getJSONObject(CouponTab.selectedCoupTypeOnlyYou).getJSONArray("coupons");

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    couponJsonObject = jsonObject.getJSONObject("coupon_data");
                    coupon = new Coupon();
                    coupon.setCouponID(couponJsonObject.getInt("coupon_id"));
                    coupon.setCouponName(couponJsonObject.getString("title"));
                    coupon.setCouponInfo(couponJsonObject.getString("description"));
                    coupon.setCouponImage(couponJsonObject.getString("image"));
                    coupon.setCouponSlug(couponJsonObject.getString("slug"));
                    coupon.setCouponType(couponJsonObject.getString("type"));
                    coupon.setCouponTypeID(242);
                    try {
                        coupon.setHasSaved(couponJsonObject.getBoolean("in_wallet"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    coupon.setChallengeDue(couponJsonObject.optBoolean("mission_status"));
                    coupon.setCouponSavedByCount(couponJsonObject.getInt("no_of_users_in_wallet"));


                    JSONArray customerTypesArray = couponJsonObject.getJSONArray("users_type");
                    ArrayList<CustomerType> couponCustomerTypes = new ArrayList<>();
                    CustomerType customerType;
                    for (int k = 0; k < customerTypesArray.length(); k++) {
                        customerType = new CustomerType();
                        customerType.setCustomerTypeName(customerTypesArray.getJSONObject(k).getString("name"));
                        customerType.setCustomerTypeImage(customerTypesArray.getJSONObject(k).getString("image"));
                        couponCustomerTypes.add(customerType);
                    }
                    coupon.setCouponCustomerTypes(couponCustomerTypes);

                    coupon.setCouponStartDate(couponJsonObject.getJSONObject("available_from").getString("date"));
                    Challenge challenge;

                    if ((coupType.equals("") && CouponTab.selectedCoupType.equals("challenges"))
                            || (coupType.equals("forYou") && CouponTab.selectedCoupTypeOnlyYou.equals("challenges"))) {
                        try {
                            JSONObject couponChallenge = couponJsonObject.getJSONObject("challenges");
                            challenge = new Challenge();
                            challenge.setOfferID(couponChallenge.getInt("offer_id"));
                            challenge.setChallengeTitle(couponChallenge.getString("title"));
                            challenge.setChallengeName(couponChallenge.getString("name"));
                            challenge.setChallengeDesc(couponChallenge.getString("offer_desc"));
                            challenge.setMissionStatus(couponJsonObject.getBoolean("mission_status"));
                            coupon.setCouponChallenge(challenge);

                            Log.e("parse challenge type", coupon.getCouponChallenges().getChallengeName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else// di ana 3amelha 3shan el swich elly gwa el adapter  lazem ykon el variable mawgood
                    {
                        challenge = new Challenge();
                        challenge.setChallengeName("");
                        challenge.setChallengeTitle("");
                        coupon.setCouponChallenge(challenge);
                    }

                    ArrayList<BrandBranch> brandBranches = new ArrayList<>();
                    BrandBranch singleBranch;
                    JSONArray branchesJsonArray = couponJsonObject.getJSONArray("branchs");
                    JSONObject branchJsonObject;
                    for (int j = 0; j < branchesJsonArray.length(); j++) {
                        branchJsonObject = branchesJsonArray.getJSONObject(j);
                        singleBranch = new BrandBranch();
                        singleBranch.setBranchName(branchJsonObject.getString("name"));
                        singleBranch.setBranchAddress(branchJsonObject.getString("address"));
                        singleBranch.setBranchCity(branchJsonObject.getString("city"));
                        singleBranch.setBranchLatitude(branchJsonObject.getDouble("latitude"));

                        singleBranch.setBranchLongitude(branchJsonObject.getDouble("longitude"));

                        if (branchJsonObject.getJSONArray("phones").length() > 0)
                            singleBranch.setBranchPhone(branchJsonObject.getJSONArray("phones").getJSONObject(0).getString("tel"));
                        brandBranches.add(singleBranch);
                    }

                    ///////////////////////////////   brand data

                    brandJsonObject = jsonObject.getJSONObject("brand_data");
                    Brand brand = new Brand();

                    brand.setBrandBranches(brandBranches);

                    brand.setBrandID(brandJsonObject.getInt("id"));
                    brand.setBrandSlug(brandJsonObject.getString("slug"));
                    brand.setBrandName(brandJsonObject.getString("name"));
                    brand.setBrandRatingStarsCount(brandJsonObject.getString("rate"));
                    brand.setBrandImage(brandJsonObject.getString("logo"));

                    ArrayList<SocialNetwork> brandSocialNetworks = new ArrayList<>();
                    SocialNetwork socialNetwork;
                    JSONArray brandSocialNetworksArray = brandJsonObject.getJSONArray("social_networks");
                    JSONObject brandSingleSocialNetwork;
                    for (int j = 0; j < brandSocialNetworksArray.length(); j++) {
                        brandSingleSocialNetwork = brandSocialNetworksArray.getJSONObject(j);
                        socialNetwork = new SocialNetwork();
                        if (!brandSingleSocialNetwork.isNull("brand_account_id"))
                            socialNetwork.setBrandAccountID(brandSingleSocialNetwork.getString("brand_account_id"));
                        socialNetwork.setBrandAccountName(brandSingleSocialNetwork.getString("brand_account_name"));
                        socialNetwork.setBrandAccountType(brandSingleSocialNetwork.getString("brand_account_type"));
                        brandSocialNetworks.add(socialNetwork);
                    }
                    brand.setBrandSocialNetworks(brandSocialNetworks);

                    if (couponJsonObject.getJSONArray("coupon_users") != null && couponJsonObject.getJSONArray("coupon_users").length() > 0) {
                        ArrayList<Fan> brandFans = new ArrayList<>();
                        Fan brandFan;
                        JSONArray brandFansArray = couponJsonObject.getJSONArray("coupon_users");
                        JSONObject brandFanJsonObject;
                        for (int j = 0; j < brandFansArray.length(); j++) {
                            brandFanJsonObject = brandFansArray.getJSONObject(j);
                            brandFan = new Fan();
                            brandFan.setFanImage(brandFanJsonObject.getString("picture"));
                            brandFan.setFanID(Integer.parseInt(brandFanJsonObject.getString("user_id")));
                            brandFans.add(brandFan);
                        }
                        coupon.setCouponSavedBy(brandFans);
                    }

                    coupon.setCouponBrand(brand);
                    couponArrayList.add(coupon);

                }
            }
            allCoupons.add(couponArrayList);

            ////////////////////////////////////////////////////////////


            return allCoupons;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("parse getCoupons", e.getMessage());
            return null;
        }
    }


    /**
     * Parse soft event request
     *
     * @param result
     * @return
     */
    public static ArrayList<String> parseEventResponse(JSONTokener result) {
        Log.e("EventResponse", result.toString());
//        {"status":"error","message":"This Coupon is for ( gamed gedan ) and you are not a ( gamed gedan ) of this brand","how_to_achieve":{"25":"Ask the brand to make you gamed gedan"}}
        ArrayList<String> response = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            response.add(jsonObject.getString("status"));
            if (jsonObject.getString("status").equals("error")) {
                if (!jsonObject.isNull("offer") && jsonObject.getJSONObject("offer").getInt("done") == 0)
                    response.add(jsonObject.getJSONObject("offer").getInt("offer_id") + "");
                else
                    response.add("-1");
                for (int i = 0; i < jsonObject.getJSONArray("message").length(); i++) {
                    response.add(jsonObject.getJSONArray("message").getString(i));
                }
            }
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

//    /**
//     * Parse like event request
//     *
//     * @param result
//     * @return
//     */
//    public static boolean parseLikeEventResponse(JSONTokener result) {
//        Log.e(" like EventResponse", result.toString());
//        try {
//            JSONObject jsonObject = new JSONObject(result);
//            return jsonObject.getString("status").equals("success");
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    /**
     * Parse filters request
     *
     * @param result
     * @return
     */
    public static ArrayList<ArrayList<Filter>> NEW_parseFilters(JSONTokener result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray;
            Filter filter;
            Filter childrenFilterHelper;
            ArrayList<Filter> oneFilterArrayList = new ArrayList<>();
            ArrayList<Filter> childrenCouponsTypesFiltersArrayListHelper;

            ArrayList<ArrayList<Filter>> allFilters = new ArrayList<>();

            jsonArray = jsonObject.getJSONArray("coupons_types");
            for (int j = 0; j < jsonArray.length(); j++) {
                childrenCouponsTypesFiltersArrayListHelper = new ArrayList<>();
                filter = NEW_getFilterData(jsonArray.getJSONObject(j));
                for (int k = 0; k < jsonArray.getJSONObject(j).getJSONArray("children").length(); k++) {
                    childrenFilterHelper = NEW_getFilterData(jsonArray.getJSONObject(j).getJSONArray("children").getJSONObject(k));
                    childrenCouponsTypesFiltersArrayListHelper.add(childrenFilterHelper);
                }
                filter.setFilterChildren(childrenCouponsTypesFiltersArrayListHelper);
                oneFilterArrayList.add(filter);

            }
            allFilters.add(oneFilterArrayList);

            jsonArray = jsonObject.getJSONArray("customer_types");

            oneFilterArrayList = new ArrayList<>();
            for (int j = 0; j < jsonArray.length(); j++) {
                filter = new Filter();
                filter.setFilterID(jsonArray.getJSONObject(j).getInt("customer_type_id"));
                filter.setFilterName(jsonArray.getJSONObject(j).getString("name"));
                oneFilterArrayList.add(filter);
            }

            allFilters.add(oneFilterArrayList);

            oneFilterArrayList = new ArrayList<>();
            if (jsonObject.has("favorite_brand")) {
                jsonArray = jsonObject.getJSONArray("favorite_brand");

                for (int j = 0; j < jsonArray.length(); j++) {
                    filter = new Filter();

                    filter.setFilterID(jsonArray.getJSONObject(j).getInt("brand_id"));
                    filter.setFilterName(jsonArray.getJSONObject(j).getString("brand_name"));
                    oneFilterArrayList.add(filter);
                }
            }
            allFilters.add(oneFilterArrayList);

            jsonArray = jsonObject.getJSONArray("categories");
            oneFilterArrayList = new ArrayList<>();
            for (int j = 0; j < jsonArray.length(); j++) {
                filter = new Filter();

                filter.setFilterID(jsonArray.getJSONObject(j).getInt("category_id"));
                filter.setFilterName(jsonArray.getJSONObject(j).getString("name"));
                oneFilterArrayList.add(filter);
            }
            allFilters.add(oneFilterArrayList);

            jsonArray = jsonObject.getJSONArray("coupons_categories");
            oneFilterArrayList = new ArrayList<>();
            for (int j = 0; j < jsonArray.length(); j++) {
                filter = new Filter();

                filter.setFilterID(jsonArray.getJSONObject(j).getInt("filter_id"));
                filter.setFilterName(jsonArray.getJSONObject(j).getString("filter_name"));
                oneFilterArrayList.add(filter);
            }
            allFilters.add(oneFilterArrayList);

            return allFilters;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Parse brand filters request
     *
     * @param result
     * @return
     */
    public static ArrayList<ArrayList<Filter>> parseBrandFilters(JSONTokener result) {
        try {
            JSONObject filterJsonObject, jsonObject = new JSONObject(result);
            JSONArray jsonArray;
            Filter filter;
            ArrayList<Filter> oneFilterArrayList = new ArrayList<>();
            ArrayList<ArrayList<Filter>> allFilters = new ArrayList<>();

            jsonArray = jsonObject.getJSONArray("categories");
            for (int j = 0; j < jsonArray.length(); j++) {
                filter = new Filter();
                filterJsonObject = jsonArray.getJSONObject(j);
                filter.setFilterName(filterJsonObject.getString("name"));
                filter.setFilterID(filterJsonObject.getInt("category_id"));
                oneFilterArrayList.add(filter);
            }
            allFilters.add(oneFilterArrayList);

            jsonArray = jsonObject.getJSONArray("cities");
            oneFilterArrayList = new ArrayList<>();
            for (int j = 0; j < jsonArray.length(); j++) {
                filter = new Filter();
                filterJsonObject = jsonArray.getJSONObject(j);
                filter.setFilterName(filterJsonObject.getString("city_name"));
                filter.setFilterID(filterJsonObject.getInt("city_id"));
                oneFilterArrayList.add(filter);
            }

            allFilters.add(oneFilterArrayList);

            return allFilters;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method in parsing filter request
     *
     * @param jsonObject
     * @return
     * @throws JSONException
     */

    private static Filter NEW_getFilterData(JSONObject jsonObject) throws JSONException {
        Filter filter = new Filter();
        filter.setFilterID(jsonObject.getInt("id"));
        filter.setFilterName(jsonObject.getString("name"));
        return filter;
    }

    /**
     * parse my coupons request response
     *
     * @param result
     * @return
     */
    public static ArrayList<Coupon> NEW_parseMyCoupons(JSONTokener result) {
        ArrayList<Coupon> coupons = new ArrayList<>();
        Coupon couponSingle;
        try {
            JSONObject couponJsonObject;
            JSONArray dataJsonArray = new JSONArray(result);
            for (int i = 0; i < dataJsonArray.length(); i++) {
                couponSingle = new Coupon();
                couponJsonObject = dataJsonArray.getJSONObject(i).getJSONObject("coupon");
                couponSingle.setCouponID(couponJsonObject.getInt("coupon_id"));
                couponSingle.setCouponSlug(couponJsonObject.getString("slug"));
                try {
                    couponSingle.setCouponName(couponJsonObject.getJSONArray("translations").getJSONObject(0).getString("title"));

                } catch (Exception e) {
                    couponSingle.setCouponName("error");

                }

                couponSingle.setCouponImage(couponJsonObject.getString("main_image"));
                couponSingle.setCouponEndDate(couponJsonObject.getString("available_to"));
                couponSingle.setCouponType(couponJsonObject.getJSONObject("coupon_type").getJSONArray("translations").getJSONObject(0).getString("label1"));

                coupons.add(couponSingle);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return coupons;
    }

    public static boolean accessTokenCheck(JSONTokener result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            return !(jsonObject.getString("error").equals("access_denied") && jsonObject.getString("error_description").equals("The resource owner or authorization server denied the request."));

        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }
    }


    /**
     * Parse contest request
     *
     * @param result
     * @return
     */
    public static Contest parseContest(JSONTokener result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject contestObject = jsonObject.getJSONObject("additional_settings");
            Contest contest = new Contest();
            contest.setContestId(contestObject.getInt("contest_id"));
            contest.setContestTitle(contestObject.getString("contest_name"));
            contest.setAvailableFrom(contestObject.getString("available_from"));
            contest.setAvailableTo(contestObject.getString("available_to"));
            contest.setBrandID(contestObject.getInt("brand_id"));
            contest.setMinPassContestDegree(contestObject.getInt("min_pass_degree"));
            contest.setAvailableTrialsNumber(contestObject.getInt("no_available_trials"));
            JSONArray questionsJsonArray = contestObject.getJSONArray("questions");
            Question question;
            ArrayList<Question> questions = new ArrayList<>();
            JSONArray choicesJsonArray;
            Choice choice;
            ArrayList<Choice> choices;
            for (int i = 0; i < questionsJsonArray.length(); i++) {
                question = new Question();
                question.setQuestionTitle(questionsJsonArray.getJSONObject(i).getString("question_title"));
                question.setQuestionID(questionsJsonArray.getJSONObject(i).getInt("question_id"));
                question.setQuestionDegree(questionsJsonArray.getJSONObject(i).getInt("question_degree"));
                question.setQuestionType(questionsJsonArray.getJSONObject(i).getInt("question_type"));
                choicesJsonArray = questionsJsonArray.getJSONObject(i).getJSONArray("choices");
                choices = new ArrayList<>();
                for (int j = 0; j < choicesJsonArray.length(); j++) {
                    choice = new Choice();
                    choice.setChoiceText(choicesJsonArray.getJSONObject(j).getString("choice_text"));
                    if (!choicesJsonArray.getJSONObject(j).isNull("correct_choice")) {
                        choice.setIsTrue(choicesJsonArray.getJSONObject(j).getInt("correct_choice"));
                    }
                    choices.add(choice);
                }
                question.setQuestionChoices(choices);
                questions.add(question);
            }
            contest.setContestQuestions(questions);

            contest = (Contest) setChallengesInfo(jsonObject, contest);

            return contest;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Parse survey request
     *
     * @param result
     * @return
     */
    public static Survey parseSurvey(JSONTokener result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject contestObject = jsonObject.getJSONObject("additional_settings");
            Survey survey = new Survey();
            survey.setSurveyID(contestObject.getInt("survey_id"));
            survey.setContestTitle(contestObject.getString("title"));
            survey.setAvailableFrom(contestObject.getString("start_date"));
            survey.setAvailableTo(contestObject.getString("end_date"));
            survey.setBrandID(contestObject.getInt("brand_id"));
            JSONArray questionsJsonArray = contestObject.getJSONArray("questions");
            Question question;
            ArrayList<Question> questions = new ArrayList<>();
            JSONArray choicesJsonArray;
            Choice choice;
            ArrayList<Choice> choices;
            for (int i = 0; i < questionsJsonArray.length(); i++) {
                question = new Question();
                question.setQuestionTitle(questionsJsonArray.getJSONObject(i).getString("question_text"));
                question.setQuestionID(questionsJsonArray.getJSONObject(i).getInt("question_id"));
                question.setQuestionType(questionsJsonArray.getJSONObject(i).getInt("question_type"));
                choicesJsonArray = questionsJsonArray.getJSONObject(i).getJSONArray("options");
                choices = new ArrayList<>();
                for (int j = 0; j < choicesJsonArray.length(); j++) {
                    choice = new Choice();
                    choice.setChoiceText(choicesJsonArray.getJSONObject(j).getString("option_label"));
                    choice.setOptionID(choicesJsonArray.getJSONObject(j).getInt("option_id"));
                    choices.add(choice);
                }
                question.setQuestionChoices(choices);
                questions.add(question);
            }
            survey.setContestQuestions(questions);
            survey = (Survey) setChallengesInfo(jsonObject, survey);

            return survey;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Parse purchase request
     *
     * @param result
     * @return
     */
    public static PurchaseCoupon parsePurchase(JSONTokener result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject purchaseObject = jsonObject.getJSONObject("coupon_additional_conditions").getJSONObject("buy");
            PurchaseCoupon purchaseCoupon = new PurchaseCoupon();
            purchaseCoupon.setFromDate(purchaseObject.getString("from"));
            purchaseCoupon.setToDate(purchaseObject.getString("to"));
            purchaseCoupon.setPurchaseAmountMoreThan(purchaseObject.getString("bigger_than"));
            purchaseCoupon = (PurchaseCoupon) setChallengesInfo(jsonObject, purchaseCoupon);
            return purchaseCoupon;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Alpha request
     *
     * @param result
     * @return
     */
    public static Alpha alphaRequest(JSONTokener result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            Alpha alpha = new Alpha();
            alpha.setUsersNumber(jsonObject.getJSONObject("coupon_additional_conditions").getInt("early_birds_users"));
            alpha = (Alpha) setChallengesInfo(jsonObject, alpha);
            return alpha;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Parse group buying request
     *
     * @param result
     * @return
     */
    public static GroupBuyingCoupon parseGroupBuying(JSONTokener result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject groupBuyingObject = jsonObject.getJSONObject("coupon_additional_conditions");
            GroupBuyingCoupon groupBuyingCoupon = new GroupBuyingCoupon();
            groupBuyingCoupon.setFrontEndCouponURL(jsonObject.getString("frontEnd_url"));
            groupBuyingCoupon.setMinNumberToActivate(groupBuyingObject.getInt("min_no_wallet_additions"));
            groupBuyingCoupon = (GroupBuyingCoupon) setChallengesInfo(jsonObject, groupBuyingCoupon);

            return groupBuyingCoupon;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Parse interactions request
     *
     * @param result
     * @return
     */
    public static ArrayList<ArrayList<Interaction>> parseInteractions(JSONTokener result) {
        try {
            ArrayList<ArrayList<Interaction>> allInteractions = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(result);
            Interaction interaction;
            JSONArray eventsJsonArray;
            JSONObject eventJsonObject;

            //-----------------Facebook

            ArrayList<Interaction> facebookInteractions = new ArrayList<>();
            if (jsonObject.getJSONObject("social_interactions").has("Facebook")) {
                JSONObject facebookInteractionsObject = jsonObject.getJSONObject("social_interactions").getJSONObject("Facebook");
                eventsJsonArray = facebookInteractionsObject.getJSONArray("events");
                for (int i = 0; i < eventsJsonArray.length(); i++) {
                    eventJsonObject = eventsJsonArray.getJSONObject(i);
                    interaction = new Interaction();
                    interaction.setSocialNetworkName("Facebook");
                    interaction.setAllowed(facebookInteractionsObject.getBoolean("user_allowed"));
                    interaction.setMissionName(eventJsonObject.getJSONObject("type").getString("name"));
                    interaction.setLink(eventJsonObject.getString("link"));
                    interaction.setDone(eventJsonObject.getBoolean("done"));
                    interaction = (Interaction) setChallengesInfo(jsonObject, interaction);
                    facebookInteractions.add(interaction);
                }
            }
            allInteractions.add(facebookInteractions);

            //----------------- twitter

            ArrayList<Interaction> twitterInteractions = new ArrayList<>();
            if (jsonObject.getJSONObject("social_interactions").has("Twitter")) {
                JSONObject twitterInteractionsObject = jsonObject.getJSONObject("social_interactions").getJSONObject("Twitter");
                eventsJsonArray = twitterInteractionsObject.getJSONArray("events");
                for (int i = 0; i < eventsJsonArray.length(); i++) {
                    eventJsonObject = eventsJsonArray.getJSONObject(i);
                    interaction = new Interaction();
                    interaction.setSocialNetworkName("Twitter");
                    interaction.setAllowed(twitterInteractionsObject.getBoolean("user_allowed"));

                    interaction.setMissionName(eventJsonObject.getJSONObject("type").getString("name"));
                    interaction.setLink(eventJsonObject.getString("link"));
                    interaction.setDone(eventJsonObject.getBoolean("done"));
                    interaction = (Interaction) setChallengesInfo(jsonObject, interaction);
                    twitterInteractions.add(interaction);
                }
            }
            allInteractions.add(twitterInteractions);

            //----------------- youtube

            ArrayList<Interaction> youtubeInteractions = new ArrayList<>();
            if (jsonObject.getJSONObject("social_interactions").has("Youtube")) {
                JSONObject youtubeInteractionsObject = jsonObject.getJSONObject("social_interactions").getJSONObject("Youtube");
                eventsJsonArray = youtubeInteractionsObject.getJSONArray("events");
                for (int i = 0; i < eventsJsonArray.length(); i++) {
                    eventJsonObject = eventsJsonArray.getJSONObject(i);
                    interaction = new Interaction();
                    interaction.setSocialNetworkName("Youtube");
                    interaction.setAllowed(youtubeInteractionsObject.getBoolean("user_allowed"));

                    interaction.setMissionName(eventJsonObject.getJSONObject("type").getString("name"));
                    interaction.setLink(eventJsonObject.getString("link"));
                    interaction.setDone(eventJsonObject.getBoolean("done"));
                    interaction = (Interaction) setChallengesInfo(jsonObject, interaction);
                    youtubeInteractions.add(interaction);
                }
            }
            allInteractions.add(youtubeInteractions);

            //----------------- twitter

            ArrayList<Interaction> instgramInteractions = new ArrayList<>();
            if (jsonObject.getJSONObject("social_interactions").has("Instagram")) {
                JSONObject instagramInteractionsObject = jsonObject.getJSONObject("social_interactions").getJSONObject("Instagram");
                eventsJsonArray = instagramInteractionsObject.getJSONArray("events");
                for (int i = 0; i < eventsJsonArray.length(); i++) {
                    eventJsonObject = eventsJsonArray.getJSONObject(i);
                    interaction = new Interaction();
                    interaction.setSocialNetworkName("Instagram");
                    interaction.setAllowed(instagramInteractionsObject.getBoolean("user_allowed"));

                    interaction.setMissionName(eventJsonObject.getJSONObject("type").getString("name"));
                    interaction.setLink(eventJsonObject.getString("link"));
                    interaction.setDone(eventJsonObject.getBoolean("done"));
                    interaction = (Interaction) setChallengesInfo(jsonObject, interaction);
                    instgramInteractions.add(interaction);
                }
            }
            allInteractions.add(instgramInteractions);

            return allInteractions;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Parse interactions request
     *
     * @param result
     * @return
     */
    public static WhoDare parseWhoDare(JSONTokener result) {
        try {
            WhoDare whoDare = new WhoDare();
            JSONObject jsonObject = new JSONObject(result);
            JSONObject whoDareObject = jsonObject.getJSONObject("additional_settings");
            whoDare.setMissionID(whoDareObject.getInt("who_dare_id"));
            whoDare.setMissionName(whoDareObject.getString("who_dare_name"));
            whoDare.setMissionStartDate(whoDareObject.getString("available_from"));
            whoDare.setMissionEndDate(whoDareObject.getString("available_to"));
            whoDare.setMissionDesc(whoDareObject.getString("who_dare_description"));
//            whoDare.setMissionURL(whoDareObject.getString("upload_url"));
            whoDare.setMissionBrandID(whoDareObject.getInt("brand_id"));
            whoDare.setMissionSlug(whoDareObject.getString("slug"));
            whoDare = (WhoDare) setChallengesInfo(jsonObject, whoDare);

            return whoDare;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Parse win and collect request
     *
     * @param result
     * @return
     */
    public static CollectAndWin parseCollectAndWin(JSONTokener result) {
        try {
            CollectAndWin collectAndWin = new CollectAndWin();
            JSONObject jsonObject = new JSONObject(result);
            collectAndWin.setPunchCardID(jsonObject.getJSONObject("coupon_additional_conditions").getInt("mission_id"));

            JSONObject collectAndWinObject = jsonObject.getJSONObject("additional_settings");
            collectAndWin.setCodeName(collectAndWinObject.getString("code_name"));
            collectAndWin.setTotalQRCodes(collectAndWinObject.getInt("total_codes"));
            collectAndWin.setScannedQRCodes(collectAndWinObject.getInt("userCodeCount"));
            collectAndWin = (CollectAndWin) setChallengesInfo(jsonObject, collectAndWin);

            return collectAndWin;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parse action request
     *
     * @param result
     * @return
     */
    public static boolean parseActionsResult(JSONTokener result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            return jsonObject.getString("status").equals("success");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<String> parseUpdateProfileResult(JSONTokener result) {
        ArrayList<String> response = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(result);

            if (jsonObject.has("status"))
                response.add("success");
            else {
                response.add("failed");

                if (jsonObject.has("username")) {
                    response.add(jsonObject.getJSONArray("username").getString(0));
                }
                if (jsonObject.has("firstname")) {
                    response.add(jsonObject.getJSONArray("firstname").getString(0));
                }
                if (jsonObject.has("lastname")) {
                    response.add(jsonObject.getJSONArray("lastname").getString(0));
                }
                if (jsonObject.has("email")) {
                    response.add(jsonObject.getJSONArray("email").getString(0));
                }
                if (jsonObject.has("birthday")) {
                    response.add(jsonObject.getJSONArray("birthday").getString(0));
                }
                if (jsonObject.has("telephone")) {
                    response.add(jsonObject.getJSONArray("telephone").getString(0));
                }
                if (jsonObject.has("mobile")) {
                    response.add(jsonObject.getJSONArray("mobile").getString(0));
                }
                if (jsonObject.has("address")) {
                    response.add(jsonObject.getJSONArray("address").getString(0));
                }
                if (jsonObject.has("password")) {
                    response.add(jsonObject.getJSONArray("address").getString(0));
                }
                if (jsonObject.has("country")) {
                    response.add(jsonObject.getJSONArray("address").getString(0));
                }


                return response;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return response;
        }
        return response;
    }

    public static ArrayList<String> parseRegistrationResult(JSONTokener result) {
        ArrayList<String> response = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(result);
//            if (jsonObject.getString("status").equals("error")) {
////                utility.showMessage(jsonObject.getString("message"), true);
//                RegistrationFragment.registrationError = jsonObject.getString("message");
//                return null;
//            }

            if (jsonObject.has("status")) {
                if (jsonObject.getString("status").equals("error")) {
                    response.add("failed");
                    response.add(jsonObject.getString("message"));
                } else
                    response.add("success");
            } else {
                response.add("failed");

                if (jsonObject.has("username")) {
                    response.add(jsonObject.getJSONArray("username").getString(0));
                }
                if (jsonObject.has("firstname")) {
                    response.add(jsonObject.getJSONArray("firstname").getString(0));
                }
                if (jsonObject.has("lastname")) {
                    response.add(jsonObject.getJSONArray("lastname").getString(0));
                }
                if (jsonObject.has("email")) {
                    response.add(jsonObject.getJSONArray("email").getString(0));
                }
                if (jsonObject.has("birthday")) {
                    response.add(jsonObject.getJSONArray("birthday").getString(0));
                }
                if (jsonObject.has("telephone")) {
                    response.add(jsonObject.getJSONArray("telephone").getString(0));
                }
                if (jsonObject.has("mobile")) {
                    response.add(jsonObject.getJSONArray("mobile").getString(0));
                }
                if (jsonObject.has("address")) {
                    response.add(jsonObject.getJSONArray("address").getString(0));
                }
                if (jsonObject.has("password")) {
                    response.add(jsonObject.getJSONArray("address").getString(0));
                }
                if (jsonObject.has("country")) {
                    response.add(jsonObject.getJSONArray("address").getString(0));
                }


                return response;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return response;
        }
        return response;
    }

    /**
     * Parse win and collect request
     *
     * @param result
     * @return
     */
    public static ArrayList<ArrayList<Filter>> parseSettings(JSONTokener result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray;
            Filter childrenFilterHelper;
            ArrayList<Filter> oneFilterArrayList = new ArrayList<>();
            ArrayList<ArrayList<Filter>> allFilters = new ArrayList<>();
            JSONArray selected = jsonObject.getJSONArray("selectedSettings");
            jsonArray = jsonObject.getJSONArray("facebookSettings");
            JSONObject jsonFilter;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonFilter = jsonArray.getJSONObject(i);
                childrenFilterHelper = new Filter();
                childrenFilterHelper.setFilterID(jsonFilter.getInt("id"));
                childrenFilterHelper.setFilterName(jsonFilter.getString("label1"));
                for (int j = 0; j < selected.length(); j++) {
                    if (childrenFilterHelper.getFilterID() == selected.getJSONObject(j).getInt("id")) {
                        childrenFilterHelper.setChecked(true);
                        break;
                    }
                }
                oneFilterArrayList.add(childrenFilterHelper);
            }
            allFilters.add(oneFilterArrayList);

            oneFilterArrayList = new ArrayList<>();
            jsonArray = jsonObject.getJSONArray("mobileSettings");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonFilter = jsonArray.getJSONObject(i);
                childrenFilterHelper = new Filter();
                childrenFilterHelper.setFilterID(jsonFilter.getInt("id"));
                childrenFilterHelper.setFilterName(jsonFilter.getString("label1"));
                for (int j = 0; j < selected.length(); j++) {
                    if (childrenFilterHelper.getFilterID() == selected.getJSONObject(j).getInt("id")) {
                        childrenFilterHelper.setChecked(true);
                        break;
                    }
                }
                oneFilterArrayList.add(childrenFilterHelper);
            }
            allFilters.add(oneFilterArrayList);


            oneFilterArrayList = new ArrayList<>();
            jsonFilter = jsonObject.getJSONObject("mobileNotification");
            childrenFilterHelper = new Filter();
            childrenFilterHelper.setFilterID(jsonFilter.getInt("id"));
            childrenFilterHelper.setFilterName(jsonFilter.getString("label1"));
            for (int j = 0; j < selected.length(); j++) {
                if (childrenFilterHelper.getFilterID() == selected.getJSONObject(j).getInt("id")) {
                    childrenFilterHelper.setChecked(true);
                    break;
                }
            }
            oneFilterArrayList.add(childrenFilterHelper);
            allFilters.add(oneFilterArrayList);


            return allFilters;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Fan parseMyProfile(JSONTokener result) {
        Fan fan;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray fanPages = jsonObject.getJSONArray("pages");

            ArrayList<FaceBookPage> fbPageArrayList = new ArrayList<>();
            FaceBookPage fbPage;
            JSONObject fb_Page;
            for (int i = 0; i < fanPages.length(); i++) {
                fb_Page = fanPages.getJSONObject(i);
                fbPage = new FaceBookPage();
                fbPage.setPageName(fb_Page.getString("Page_name"));
                fbPage.setPagePath(fb_Page.getString("Page_path"));
                fbPage.setPageFbId(fb_Page.getString("fb_page_id"));
                fbPageArrayList.add(fbPage);
            }

            ArrayList<SocialAccount> socialAccountArrayList = new ArrayList<>();
            JSONArray socialAccounts = jsonObject.getJSONArray("social_accounts");
            SocialAccount socialAccount;
            JSONObject social_accountJson;
            for (int i = 0; i < socialAccounts.length(); i++) {
                social_accountJson = socialAccounts.getJSONObject(i);
                socialAccount = new SocialAccount();
                socialAccount.setAccountID(social_accountJson.getInt("account_id"));
                socialAccount.setAccountName(social_accountJson.getString("account_name"));
                socialAccount.setSocialNetworkAccountID(social_accountJson.getString("social_network_account_id"));
                socialAccount.setSocialNetworkType(social_accountJson.getInt("social_network_type"));
                socialAccountArrayList.add(socialAccount);
            }

            fan = new Fan();
            fan.setFanID(Integer.parseInt(jsonObject.getString("id")));
            fan.setFanUserName(jsonObject.optString("username", "NA"));
            fan.setFanFirstName(jsonObject.getString("firstname"));
            fan.setFanLastName(jsonObject.getString("lastname"));
            fan.setFanEMAil(jsonObject.getString("email"));
            fan.setFanImage(jsonObject.optString("picture", ""));
            fan.setFanBirthDate(jsonObject.optString("birthday", "NA"));
            fan.setFanPhone(jsonObject.optString("telephone", "NA"));
            fan.setFanMob(jsonObject.optString("mobile", "NA"));
            fan.setFanGender(jsonObject.getString("gender"));
            fan.setFanAddress(jsonObject.optString("address", "NA"));
            fan.setCouponCount(jsonObject.optInt("available_coupons"));
            fan.setFanCity(jsonObject.optString("city_name", "NA"));
            fan.setFanCityID(jsonObject.getInt("city"));
            fan.setFanCountry(jsonObject.getString("country_name"));
            fan.setFanFbPages(fbPageArrayList);
            fan.setSocialAccounts(socialAccountArrayList);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return fan;
    }

    public static Coupon parseQualifiedFragment(JSONTokener result) {
        Coupon coupon = new Coupon();
        try {
            JSONObject jsonObject = new JSONObject(result);

            if (jsonObject.getJSONObject("is_available").getInt("available") == 1) {
                jsonObject = jsonObject.getJSONObject("coupon_condition");
                coupon = parseQualifiedCouponInfo(new JSONTokener(jsonObject.toString()));
                if (coupon == null)
                    return null;
                coupon.setQualifiedUser(true);

            } else {
                coupon.setQualifiedUser(false);
                ArrayList<String> conditions = new ArrayList<>();
                JSONArray conditionsJsonArray = jsonObject.getJSONObject("is_available").getJSONArray("failure_condition");
                for (int i = 0; i < conditionsJsonArray.length(); i++) {
                    conditions.add(conditionsJsonArray.getString(i));
                }
                coupon.setDisqualificationReasons(conditions);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return coupon;
    }


    public static Coupon parseQualifiedCouponInfo(JSONTokener jsonTokener) {
        Coupon coupon = new Coupon();
        try {
            JSONObject jsonObject = new JSONObject(jsonTokener);

            coupon.setCouponID(jsonObject.getInt("coupon_id"));
            coupon.setCouponName(jsonObject.getString("title"));
            coupon.setCouponInfo(jsonObject.getString("description"));
            coupon.setCouponEndDate(jsonObject.getString("available_to"));
            coupon.setMaxFansUse(jsonObject.optInt("max_number", 0));
            coupon.setMaxUsePerFan(jsonObject.optInt("max_number_per_client", 0));
            coupon.setMaxUsePerFanPerDay(jsonObject.optInt("max_number_per_day", 0));

            JSONArray branchesJsonArray = jsonObject.getJSONArray("branches");
            BrandBranch brandBranch;
            ArrayList<BrandBranch> brandBranches = new ArrayList<>();
            JSONObject jsonBranch, workingDayJsonObject;
            ArrayList<DayWorkingHour> dayWorkingHours;
            DayWorkingHour dayWorkingHour;
            JSONArray workingDaysJsonArray;

            for (int i = 0; i < branchesJsonArray.length(); i++) {
                brandBranch = new BrandBranch();
                jsonBranch = branchesJsonArray.getJSONObject(i);
                brandBranch.setBranchName(jsonBranch.getString("branch_name"));
                brandBranch.setBranchAddress(jsonBranch.getString("branch_address"));

                if (!jsonBranch.isNull("branch_phones") && jsonBranch.getJSONArray("branch_phones").length() > 0)
                    brandBranch.setBranchPhone(jsonBranch.getJSONArray("branch_phones").getJSONObject(0).getString("tel"));
                workingDaysJsonArray = jsonBranch.getJSONArray("working_hours");
                dayWorkingHours = new ArrayList<>();

                for (int j = 0; j < workingDaysJsonArray.length(); j++) {
                    dayWorkingHour = new DayWorkingHour();
                    workingDayJsonObject = workingDaysJsonArray.getJSONObject(j);
                    dayWorkingHour.setDayName(workingDayJsonObject.getJSONObject("working_days").getString("label1"));
                    dayWorkingHour.setFromHour(workingDayJsonObject.optString("starting_time"));
                    dayWorkingHour.setToHour(workingDayJsonObject.optString("ending_time"));

                    dayWorkingHours.add(dayWorkingHour);
                }
                brandBranch.setDayWorkingHours(dayWorkingHours);
                brandBranches.add(brandBranch);
            }
            try {
                JSONObject condition = jsonObject.optJSONObject("condition");
                if (condition != null) {

                    if (!condition.isNull("days")) {
                        JSONObject days = condition.getJSONObject("days");
                        if (!days.isNull("available")) {
                            JSONArray availableDays = days.getJSONArray("available");
                            dayWorkingHours = new ArrayList<>();
                            for (int i = 0; i < availableDays.length(); i++) {
                                dayWorkingHour = new DayWorkingHour();
                                workingDayJsonObject = availableDays.getJSONObject(i);
                                dayWorkingHour.setDayName(workingDayJsonObject.getString("day"));
                                dayWorkingHour.setFromHour(workingDayJsonObject.getString("from"));
                                dayWorkingHour.setToHour(workingDayJsonObject.getString("to"));

                                dayWorkingHours.add(dayWorkingHour);
                            }
                            coupon.setOtherInstruction(dayWorkingHours);
                        }
                    }
                }

            } catch (Exception e) {
                //no instructions case
            }
            Brand brand = new Brand();
            brand.setBrandBranches(brandBranches);
            coupon.setCouponBrand(brand);


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return coupon;
    }


    private static ChallengeBaseClass setChallengesInfo(JSONObject jsonObject, ChallengeBaseClass challengeBaseClass) {
        try {
            challengeBaseClass.setCouponID(jsonObject.getInt("coupon_id"));
            challengeBaseClass.setCouponName(jsonObject.getString("title"));
            challengeBaseClass.setBrandName(jsonObject.getJSONObject("brand").getString("brand_name"));
            challengeBaseClass.setBrandLogo(jsonObject.getJSONObject("brand").getString("logo"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return challengeBaseClass;
    }

    /**
     * Parse all brands request
     *
     * @param result
     * @return
     */
    public static ArrayList<Brand> parseGetBrands(JSONTokener result) {
        try {
            ArrayList<Brand> brandArrayList = new ArrayList<>();
            Brand brand;
            JSONObject responseJsonObject = new JSONObject(result);
            JSONArray jsonArray = responseJsonObject.getJSONArray("data");

            JSONObject brandJsonObject;
            ArrayList<SocialNetwork> socialNetworks;
            SocialNetwork socialNetwork;
            for (int i = 0; i < jsonArray.length(); i++) {
                brandJsonObject = jsonArray.getJSONObject(i);
                brand = new Brand();
                brand.setBrandSlug(brandJsonObject.getString("slug"));
                brand.setBrandImage(brandJsonObject.getString("logo"));
                brand.setBrandCover(brandJsonObject.getString("cover"));
                brand.setBrandCouponsCount(brandJsonObject.optInt("couponcount"));
                brand.setBrandName(brandJsonObject.getString("brand_name"));
                brand.setBrandID(brandJsonObject.getInt("brand_id"));
                brand.setBrandRatingStarsCount(brandJsonObject.optString("evaluations", "0"));
                brand.setBrandFansCount(brandJsonObject.optInt("fansCount"));
                brand.setHasLoyalty(brandJsonObject.getBoolean("hasLoyalty"));


                socialNetworks = new ArrayList<>();
                //fb
                socialNetwork = new SocialNetwork();
                socialNetwork.setBrandAccountID(brandJsonObject.getJSONObject("socialNetworks").optString("facebook"));
                socialNetworks.add(socialNetwork);
                //twitter
                socialNetwork = new SocialNetwork();
                socialNetwork.setBrandAccountID(brandJsonObject.getJSONObject("socialNetworks").optString("twitter"));
                socialNetworks.add(socialNetwork);
                //youtube
                socialNetwork = new SocialNetwork();
                socialNetwork.setBrandAccountID(brandJsonObject.getJSONObject("socialNetworks").optString("youtube"));
                socialNetworks.add(socialNetwork);

                brand.setBrandSocialNetworks(socialNetworks);
                brandArrayList.add(brand);

            }

            return brandArrayList;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("parse getBrandss", e.getMessage());
            return null;
        }
    }

    /**
     * Parse all single brand request
     *
     * @param result
     * @return
     */
    public static boolean parseSingleBrand(JSONTokener result) {
        try {
            JSONObject brandJsonObject = new JSONObject(result);
            JSONObject jsonObject;
            //brand desc if exist
            BrandsTab.selectedBrand.setBrandDesc(brandJsonObject.optString("brand_desc"));
            BrandsTab.selectedBrand.setBrandName(brandJsonObject.optString("brand_name"));

            JSONArray jsonArray = brandJsonObject.getJSONArray("branches");
            ArrayList<BrandBranch> brandBranches = new ArrayList<>();
            BrandBranch brandBranch;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                brandBranch = new BrandBranch();
                brandBranch.setBranchLatitude(jsonObject.getDouble("lat"));
                brandBranch.setBranchLongitude(jsonObject.getDouble("long"));
                brandBranch.setBranchName(jsonObject.getString("branch_name"));
                brandBranch.setBranchAddress(jsonObject.getString("branch_address"));
                brandBranch.setBranchCity(jsonObject.getString("city"));
                if (jsonObject.getJSONArray("branchPhones").length() != 0)
                    brandBranch.setBranchPhone(jsonObject.getJSONArray("branchPhones").getString(0));
                brandBranches.add(brandBranch);
            }
            BrandsTab.selectedBrand.setBrandBranches(brandBranches);

            jsonArray = brandJsonObject.getJSONObject("loyalty").getJSONArray("data");
            Coupon coupon;
            //loyalty coupons
            ArrayList<Coupon> coupons = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                coupon = new Coupon();
                coupon.setCouponID(jsonObject.getInt("coupon_id"));
                coupon.setCouponName(jsonObject.optString("title", "null"));
                coupon.setCouponImage(jsonObject.getString("main_image"));
                coupon.setCouponSlug(jsonObject.getString("slug"));
                coupon.setCouponType(jsonObject.getString("coupon_type"));
                coupon.setCouponEndDate(jsonObject.getJSONObject("available_to").getString("date"));
                coupons.add(coupon);
            }
            BrandsTab.selectedBrand.setLoyaltyCoupons(coupons);

            //special offer coupons
            jsonArray = brandJsonObject.getJSONObject("special").getJSONArray("data");
            coupons = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                coupon = new Coupon();
                coupon.setCouponID(jsonObject.getInt("coupon_id"));
                coupon.setCouponName(jsonObject.getString("title"));
                coupon.setCouponImage(jsonObject.getString("main_image"));
                coupon.setCouponSlug(jsonObject.getString("slug"));
                coupon.setCouponType(jsonObject.getString("coupon_type"));
                coupon.setCouponEndDate(jsonObject.getJSONObject("available_to").getString("date"));
                coupons.add(coupon);
            }
            BrandsTab.selectedBrand.setSpecialOfferCoupons(coupons);

            ArrayList<CustomerType> customerTypes = new ArrayList<>();
            CustomerType customerType;
            jsonArray = brandJsonObject.getJSONArray("customer_types");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                customerType = new CustomerType();
                customerType.setCustomerTypeImage(jsonObject.getString("image"));
                customerType.setCustomerTypeName(jsonObject.getString("name"));
                customerTypes.add(customerType);
            }
            BrandsTab.selectedBrand.setBrandCustomerTypes(customerTypes);

            ArrayList<Comment> evaluations = new ArrayList<>();
            Comment comment;
            Fan commentWriter;
            jsonArray = brandJsonObject.getJSONObject("evaluations").getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                comment = new Comment();
                commentWriter = new Fan();
                comment.setCommentBody(jsonObject.getString("comment"));
                comment.setRate(jsonObject.getInt("rate"));
                commentWriter.setFanImage(jsonObject.getString("picture"));
                commentWriter.setFanFirstName(jsonObject.getString("firstname"));
                commentWriter.setFanLastName(jsonObject.getString("lastname"));
                comment.setCommentWriter(commentWriter);
                evaluations.add(comment);
            }
            BrandsTab.selectedBrand.setBrandEvaluations(evaluations);
            if (MainActivity.currentFan != null && !brandJsonObject.isNull("user_eval")) {
                comment = new Comment();
                jsonObject = brandJsonObject.getJSONObject("user_eval");
                comment.setCommentID(jsonObject.optInt("comment_id"));
                comment.setCommentBody(jsonObject.optString("comment"));
                comment.setRate(jsonObject.optInt("rate"));

                BrandsTab.selectedBrand.setCurrentFanComment(comment);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("parse single brand", e.getMessage());
            return false;
        }
    }

    /**
     * Parse all credit request
     *
     * @param result
     * @return
     */
    public static Credit parseAllCredit(JSONTokener result) {
        try {
            Credit credit = new Credit();
            JSONObject jsonObject, creditJsonObject = new JSONObject(result);
            JSONArray jsonArray = creditJsonObject.getJSONArray("statistics");
            ArrayList<CustomerType> customerTypes = new ArrayList<>();
            CustomerType customerType;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                customerType = new CustomerType();
                customerType.setCustomerTypeImage(jsonObject.getString("image"));
                customerType.setCustomerTypeCount(jsonObject.getInt("count"));
                customerTypes.add(customerType);
            }
            credit.setTotalCustomerTypes(customerTypes);

            jsonArray = creditJsonObject.getJSONArray("badges");
            customerTypes = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                customerType = new CustomerType();
                customerType.setCustomerTypeImage(jsonObject.getString("badge_pic"));
                customerType.setCustomerTypeCount(jsonObject.getInt("badgesCount"));
                customerTypes.add(customerType);
            }
            credit.setTotalBadges(customerTypes);


            jsonObject = creditJsonObject.getJSONObject("total-credit");
            credit.setTotalPoints(jsonObject.optString("totalPoints", "0"));
            credit.setUsedCoupons(jsonObject.optInt("usedCoupons", 0));
            credit.setAvailableCoupons(jsonObject.optInt("availableCoupon", 0));

            ArrayList<CreditPerBrand> brands = new ArrayList<>();
            jsonObject = creditJsonObject.getJSONObject("brands");
            jsonArray = jsonObject.getJSONArray("data");
            JSONObject brandJsonObject;
            CreditPerBrand brand;
            for (int i = 0; i < jsonArray.length(); i++) {
                brandJsonObject = jsonArray.getJSONObject(i);
                brand = new CreditPerBrand();
                brand.setBrandImage(brandJsonObject.getString("logo"));
                brand.setBrandCover(brandJsonObject.getString("cover"));
                brand.setBrandName(brandJsonObject.getString("brand_name"));
                customerType = new CustomerType();
                if (brandJsonObject.getJSONArray("customer_type").length() == 0)
                    customerType.setCustomerTypeImage("");
                else
                    customerType.setCustomerTypeImage(brandJsonObject.getJSONArray("customer_type").getJSONObject(0).getString("image"));
                brand.setCustomerType(customerType);
                brand.setTotalPoints(brandJsonObject.optInt("total_points", 0));
                brand.setExchangedPoints(brandJsonObject.optInt("exchanged_points", 0));
                brand.setUsedCoupons(brandJsonObject.optInt("usedCoupons", 0));
                brand.setAvailableCoupons(brandJsonObject.optInt("availableCoupons", 0));

                brands.add(brand);
            }
            credit.setBrands(brands);


            return credit;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("parse credit", e.getMessage());
            return null;
        }
    }

    /**
     * Parse my friends request
     *
     * @param result
     * @return
     */
    public static ArrayList<Fan> parseAllFriends(JSONTokener result) {
        try {
            ArrayList<Fan> friends = new ArrayList<>();
            Fan fan;
            JSONObject helperJsonObject, fanJsonObject, jsonObject = new JSONObject(result);
            JSONArray helperJsonArray, friendsJsonArray = jsonObject.getJSONArray("data");
            ArrayList<String> friendBrands;
            ArrayList<CustomerType> customerTypes;
            CustomerType customerType;

            for (int i = 0; i < friendsJsonArray.length(); i++) {
                fanJsonObject = friendsJsonArray.getJSONObject(i);
                fan = new Fan();
                fan.setFanUserName(fanJsonObject.getString("name"));
                fan.setFanImage(fanJsonObject.getString("image"));

                helperJsonArray = fanJsonObject.getJSONArray("brands");
                friendBrands = new ArrayList<>();
                for (int j = 0; j < helperJsonArray.length(); j++) {
                    helperJsonObject = helperJsonArray.getJSONObject(j);
                    friendBrands.add(helperJsonObject.getString("logo"));
                }
                fan.setFanBrandsLogos(friendBrands);
// temporary
                fan.setTotalBadges(new ArrayList<CustomerType>());

                helperJsonArray = fanJsonObject.getJSONArray("customer_types_counts");
                customerTypes = new ArrayList<>();

                for (int k = 0; k < helperJsonArray.length(); k++) {
                    helperJsonObject = helperJsonArray.getJSONObject(k);
                    customerType = new CustomerType();
                    customerType.setCustomerTypeImage(helperJsonObject.getString("image"));
                    customerType.setCustomerTypeCount(helperJsonObject.optInt("count"));
                    customerTypes.add(customerType);
                }
                fan.setTotalCustomerTypes(customerTypes);


                friends.add(fan);
            }

            return friends;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("parse getBrandss", e.getMessage());
            return null;
        }
    }

    public static ArrayList<ArrayList<Experience>> parseAllExperiences(JSONTokener result) {
        try {
            ArrayList<ArrayList<Experience>> allExperiences = new ArrayList<>();
            ArrayList<Experience> goodExp = new ArrayList<>(), badExp = new ArrayList<>(), funExp = new ArrayList<>();

            JSONObject tagJsonObject, responseJsonObject = new JSONObject(result);
            JSONArray tagsJsonArray, jsonArray = responseJsonObject.getJSONArray("data");

            JSONObject expJsonObject;
            Experience experience;
            ArrayList<TagsChip> tagsChips;
            TagsChip tagsChip;
            for (int i = 0; i < jsonArray.length(); i++) {
                expJsonObject = jsonArray.getJSONObject(i);
                experience = new Experience();
                tagsChips = new ArrayList<>();
                tagsJsonArray = expJsonObject.getJSONArray("tags");
                for (int k = 0; k < tagsJsonArray.length(); k++) {
                    tagsChip = new TagsChip();
                    tagJsonObject = tagsJsonArray.getJSONObject(k);
                    tagsChip.setId(String.valueOf(tagJsonObject.optInt("tag_id")));
                    tagsChip.setName(tagJsonObject.getString("keyword"));
                    tagsChips.add(tagsChip);
                }
                experience.setTagsChips(tagsChips);
                experience.setExperienceID(expJsonObject.optInt("news_id"));
                experience.setExperienceTitle(expJsonObject.optString("title"));
                experience.setExperienceBody(expJsonObject.getString("description"));
                experience.setExperienceSlug(expJsonObject.getString("slug"));
                experience.setAuthorID(expJsonObject.optInt("author_id"));
                experience.setExperienceCategoryID(expJsonObject.optInt("category_id"));
                experience.setExperienceCategoryName(expJsonObject.getJSONObject("category").optString("name"));
                experience.setExperienceTime(expJsonObject.getJSONObject("created_at").optString("date"));
                experience.setAuthorName(new StringBuilder().append(expJsonObject.optString("user_firstname")).append(" ").append(expJsonObject.optString("user_lastname")).toString());
                experience.setAuthorImg(expJsonObject.optString("user_picture"));
                switch (experience.getExperienceCategoryID()) {
                    case 1:
                        badExp.add(experience);
                        break;
                    case 2:
                        goodExp.add(experience);
                        break;
                    case 3:
                        funExp.add(experience);
                        break;

                }
            }

            allExperiences.add(goodExp);
            allExperiences.add(badExp);
            allExperiences.add(funExp);

            return allExperiences;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("parse getExp", e.getMessage());
            return null;
        }
    }

    /**
     * Parse create or edit experience
     *
     * @param result
     * @return
     */
    public static boolean parseCreateEditExp(JSONTokener result, boolean create) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.isNull("status")) {
                if (jsonObject.getString("status").equals("success")) {
                    JSONObject tagJsonObject, expJsonObject = jsonObject.getJSONObject("experience");
                    Experience experience = new Experience();

                    ArrayList<TagsChip> tagsChips = new ArrayList<>();
                    TagsChip tagsChip;
                    JSONArray tagsJsonArray = expJsonObject.optJSONArray("tags");
                    if (tagsJsonArray != null)
                        for (int k = 0; k < tagsJsonArray.length(); k++) {
                            tagsChip = new TagsChip();
                            tagJsonObject = tagsJsonArray.getJSONObject(k);
                            tagsChip.setId(String.valueOf(tagJsonObject.optInt("tag_id")));
                            tagsChip.setName(tagJsonObject.getString("keyword"));
                            Log.e("edited exp tag", tagJsonObject.getString("keyword"));
                            tagsChips.add(tagsChip);
                        }
                    experience.setTagsChips(tagsChips);

                    experience.setExperienceID(expJsonObject.optInt("news_id"));
                    experience.setExperienceTitle(expJsonObject.optString("title"));
                    experience.setExperienceBody(expJsonObject.getString("description"));
                    experience.setExperienceSlug(expJsonObject.getString("slug"));
                    experience.setAuthorID(expJsonObject.optInt("author_id"));
                    experience.setExperienceCategoryID(Integer.parseInt(expJsonObject.getString("category_id")));
//                    experience.setExperienceCategoryName(expJsonObject.getJSONObject("category").optString("name"));
                    experience.setExperienceTime(expJsonObject.optString("created_at"));
                    experience.setAuthorName(new StringBuilder().append(expJsonObject.getJSONObject("author").optString("firstname")).append(" ").append(expJsonObject.getJSONObject("author").optString("lastname")).toString());
                    experience.setAuthorImg(expJsonObject.getJSONObject("author").optString("picture"));
                    if (create)
                        switch (experience.getExperienceCategoryID()) {
                            case 1://bad
                                ExperienceTab.allExperiences.get(1).add(experience);
                                break;
                            case 2://good
                                ExperienceTab.allExperiences.get(0).add(experience);
                                break;
                            case 3://fun
                                ExperienceTab.allExperiences.get(2).add(experience);
                                break;
                        }
                    else {
                        switch (experience.getExperienceCategoryID()) {
                            case 1://bad
                                for (int i = 0; i < ExperienceTab.allExperiences.get(1).size(); i++) {
                                    if (ExperienceTab.allExperiences.get(1).get(i).getExperienceID()
                                            == experience.getExperienceID())
                                        ExperienceTab.allExperiences.get(1).set(i, experience);
                                }
                                break;
                            case 2://good
                                for (int i = 0; i < ExperienceTab.allExperiences.get(0).size(); i++) {
                                    if (ExperienceTab.allExperiences.get(0).get(i).getExperienceID()
                                            == experience.getExperienceID())
                                        ExperienceTab.allExperiences.get(0).set(i, experience);
                                }
                                break;
                            case 3://fun
                                for (int i = 0; i < ExperienceTab.allExperiences.get(2).size(); i++) {
                                    if (ExperienceTab.allExperiences.get(2).get(i).getExperienceID()
                                            == experience.getExperienceID())
                                        ExperienceTab.allExperiences.get(2).set(i, experience);
                                }
                                break;
                        }
                    }
                    return true;
                } else
                    return false;
            } else {
                ArrayList<String> validationErrors = new ArrayList<>();
                JSONArray jsonArray;
                if (!jsonObject.isNull("title")) {
                    jsonArray = jsonObject.getJSONArray("title");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        validationErrors.add(jsonArray.getString(i));
                    }
                }

                if (!jsonObject.isNull("description")) {
                    jsonArray = jsonObject.getJSONArray("description");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        validationErrors.add(jsonArray.getString(i));
                    }
                }
                if (!jsonObject.isNull("slug")) {
                    jsonArray = jsonObject.getJSONArray("slug");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        validationErrors.add(jsonArray.getString(i));
                    }
                }
                if (!jsonObject.isNull("new_tags_keyword")) {
                    jsonArray = jsonObject.getJSONArray("new_tags_keyword");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        validationErrors.add(jsonArray.getString(i));
                    }
                }
                WriteExperienceFragment.validationErrors = validationErrors;
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Parse get related tags
     *
     * @param result
     * @return
     */
    public static List<TagsChip> parseGetRelatedTags(JSONTokener result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonObject;
            TagsChip tagsChip;
            List<TagsChip> tagsChips = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                tagsChip = new TagsChip();
                tagsChip.setId(String.valueOf(jsonObject.getInt("tag_id")));
                tagsChip.setName(jsonObject.getString("keyword"));
                tagsChip.setUsage(String.valueOf(jsonObject.getInt("count")));
                tagsChips.add(tagsChip);
            }


            return tagsChips;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<TagsChip>();
        }
    }

    /**
     * Parse get user campaigns
     *
     * @param result
     * @return
     */
    public static LBC parseGetUserCampaigns(JSONTokener result) {
        try {
            JSONObject locationJsonObject, campaignJsonObject, jsonObject = new JSONObject(result);
            LBC lbc = new LBC();

            int campUpdateInterval;
            campUpdateInterval = jsonObject.getJSONObject("settings").getInt("mobile_update_range");
            lbc.setCampaignsUpdateInterval(campUpdateInterval);

            JSONArray locationsJsonArray, campaignsJsonArray = jsonObject.getJSONArray("campaigns");
            ArrayList<CampaignLocation> userCampaignLocations = new ArrayList<>();
            CampaignLocation campaignLocation;

            for (int i = 0; i < campaignsJsonArray.length(); i++) {
                jsonObject = campaignsJsonArray.getJSONObject(i);
                locationsJsonArray = jsonObject.getJSONArray("locations");
                campaignJsonObject = jsonObject.getJSONObject("campaign");

                for (int j = 0; j < locationsJsonArray.length(); j++) {
                    locationJsonObject = locationsJsonArray.getJSONObject(j);
                    campaignLocation = new CampaignLocation();
                    campaignLocation.setBranchNotifyRange(locationJsonObject.getInt("range"));
                    campaignLocation.setBranchId(locationJsonObject.getInt("branch_id"));
                    campaignLocation.setCampaignLat(locationJsonObject.getDouble("latitude"));
                    campaignLocation.setCampaignLong(locationJsonObject.getDouble("longitude"));
                    campaignLocation.setCampaignID(campaignJsonObject.getInt("id"));
                    campaignLocation.setCampaignCouponId(campaignJsonObject.getInt("coupon_id"));
                    campaignLocation.setCampaignName(campaignJsonObject.getString("name"));
                    campaignLocation.setFrequency(campaignJsonObject.optInt("frequency"));
                    campaignLocation.setTimeDifference(campaignJsonObject.optInt("time_difference"));
                    campaignLocation.setCampaignTitle(campaignJsonObject.getString("title"));
                    campaignLocation.setCampaignBody(campaignJsonObject.getString("body"));
                    campaignLocation.setCampaignCouponImg(campaignJsonObject.getJSONObject("coupon").getString("main_image"));
                    campaignLocation.setCampaignCouponSlug(campaignJsonObject.getJSONObject("coupon").getString("slug"));
                    campaignLocation.setCampaignCouponType(campaignJsonObject.getJSONObject("coupon").getJSONObject("offer").getString("type"));
                    campaignLocation.setCampaignEndDate(campaignJsonObject.getJSONObject("locations").getString("end_date"));
                    campaignLocation.setFrequencyLimit(campaignJsonObject.getJSONObject("locations").optInt("frequency_limit"));

                    userCampaignLocations.add(campaignLocation);
                }
            }
            lbc.setCampaignLocations(userCampaignLocations);

            return lbc;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean parseCouponType(JSONTokener result, Utility utility) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getString("type").equals("Loyalty Programs") && MainActivity.langResult.equals("ar"))
                MainActivity.selectedCoupon.setCouponType(utility.getCurrentActivity().getString(R.string.loyalty_programs));
            else
                MainActivity.selectedCoupon.setCouponType(jsonObject.getString("type"));

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean parseAddTwitterAccount(JSONTokener result, Utility utility) {
        try {
//            Log.e("response - twitter", result.toString());

            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getString("status").equals("error")) {

                FbMainFragment.requestError = jsonObject.getString("message");
                EditAccountFragment.addSocialAccountErr = jsonObject.getString("message");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static ArrayList<Country> parseActiveCountries(JSONTokener result) {
        ArrayList<Country> countries = new ArrayList<>();
        Country country;
        try {
            Log.e("countries respo", result.toString());

            JSONObject jsonObject;
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray.length() != 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    country = new Country();
                    jsonObject = jsonArray.getJSONObject(i);
                    country.setCountryID(jsonObject.getInt("country_id"));
                    country.setCountryName(jsonObject.getString("name"));
                    country.setCountryCode(jsonObject.getString("iso_code_2"));
                    countries.add(country);
                }

            }

            return countries;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String parseResendVerEmailResponse(JSONTokener result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            return jsonObject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
