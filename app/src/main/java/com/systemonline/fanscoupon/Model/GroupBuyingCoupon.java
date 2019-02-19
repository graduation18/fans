package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 10/9/2016.
 */

public class GroupBuyingCoupon extends ChallengeBaseClass {
    private String frontEndCouponURL;
    private int minNumberToActivate;

    public String getFrontEndCouponURL() {
        return frontEndCouponURL;
    }

    public void setFrontEndCouponURL(String frontEndCouponURL) {
        this.frontEndCouponURL = frontEndCouponURL;
    }

    public int getMinNumberToActivate() {
        return minNumberToActivate;
    }

    public void setMinNumberToActivate(int minNumberToActivate) {
        this.minNumberToActivate = minNumberToActivate;
    }
}
