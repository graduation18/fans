package com.systemonline.fanscoupon.Model;

import java.util.ArrayList;


public class Brand implements Cloneable {
    private String brandName, brandDesc, brandSlug, brandImage, brandCover, brandRatingStarsCount = "null";
    private int brandID, brandFansCount, brandCouponsCount;
    private ArrayList<Brand> brandAlliances;
    private ArrayList<BrandBranch> brandBranches;
    private ArrayList<SocialNetwork> brandSocialNetworks;
    private ArrayList<Fan> brandFans;
    private boolean likedByCurrentUser = false, hasLoyalty;
    private ArrayList<CustomerType> brandCustomerTypes;
    private ArrayList<Coupon> loyaltyCoupons, specialOfferCoupons;
    private ArrayList<Comment> brandEvaluations;
    private Comment currentFanComment;


    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandSlug() {
        return brandSlug;
    }

    public void setBrandSlug(String brandSlug) {
        this.brandSlug = brandSlug;
    }

    public String getBrandImage() {
        return brandImage;
    }

    public void setBrandImage(String brandImage) {
        this.brandImage = brandImage;
    }

    public String getBrandCover() {
        return brandCover;
    }

    public void setBrandCover(String brandCover) {
        this.brandCover = brandCover;
    }

    public int getBrandCouponsCount() {
        return brandCouponsCount;
    }

    public void setBrandCouponsCount(int brandCouponsCount) {
        this.brandCouponsCount = brandCouponsCount;
    }

    public Integer getBrandID() {
        return brandID;
    }

    public void setBrandID(Integer brandID) {
        this.brandID = brandID;
    }

    public int getBrandFansCount() {
        return brandFansCount;
    }

    public void setBrandFansCount(int brandFansCount) {
        this.brandFansCount = brandFansCount;
    }

    public String getBrandRatingStarsCount() {
        return brandRatingStarsCount;
    }

    public void setBrandRatingStarsCount(String brandRatingStarsCount) {
        this.brandRatingStarsCount = brandRatingStarsCount;
    }

    public ArrayList<Brand> getBrandAlliances() {
        return brandAlliances;
    }

    public void setBrandAlliances(ArrayList<Brand> brandAlliances) {
        this.brandAlliances = brandAlliances;
    }

    public ArrayList<BrandBranch> getBrandBranches() {
        return brandBranches;
    }

    public void setBrandBranches(ArrayList<BrandBranch> brandBranches) {
        this.brandBranches = brandBranches;
    }

    public ArrayList<SocialNetwork> getBrandSocialNetworks() {
        return brandSocialNetworks;
    }

    public void setBrandSocialNetworks(ArrayList<SocialNetwork> brandSocialNetworks) {
        this.brandSocialNetworks = brandSocialNetworks;
    }

    public boolean getLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public ArrayList<Fan> getBrandFans() {
        return brandFans;
    }

    public void setBrandFans(ArrayList<Fan> brandFans) {
        this.brandFans = brandFans;
    }

    public boolean isHasLoyalty() {
        return hasLoyalty;
    }

    public void setHasLoyalty(boolean hasLoyalty) {
        this.hasLoyalty = hasLoyalty;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ArrayList<CustomerType> getBrandCustomerTypes() {
        return brandCustomerTypes;
    }

    public void setBrandCustomerTypes(ArrayList<CustomerType> brandCustomerTypes) {
        this.brandCustomerTypes = brandCustomerTypes;
    }

    public ArrayList<Coupon> getLoyaltyCoupons() {
        return loyaltyCoupons;
    }

    public void setLoyaltyCoupons(ArrayList<Coupon> loyaltyCoupons) {
        this.loyaltyCoupons = loyaltyCoupons;
    }

    public ArrayList<Coupon> getSpecialOfferCoupons() {
        return specialOfferCoupons;
    }

    public void setSpecialOfferCoupons(ArrayList<Coupon> specialOfferCoupons) {
        this.specialOfferCoupons = specialOfferCoupons;
    }

    public ArrayList<Comment> getBrandEvaluations() {
        return brandEvaluations;
    }

    public void setBrandEvaluations(ArrayList<Comment> brandEvaluations) {
        this.brandEvaluations = brandEvaluations;
    }

    public String getBrandDesc() {
        return brandDesc;
    }

    public void setBrandDesc(String brandDesc) {
        this.brandDesc = brandDesc;
    }

    public Comment getCurrentFanComment() {
        return currentFanComment;
    }

    public void setCurrentFanComment(Comment currentFanComment) {
        this.currentFanComment = currentFanComment;
    }
}
