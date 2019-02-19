package com.systemonline.fanscoupon.Model;

import java.util.ArrayList;

/**
 * Created by SystemOnline1 on 4/29/2017.
 */

public class Experience {
    private int experienceID, authorID, experienceCategoryID;
    private String authorImg, authorName, experienceTitle, experienceSlug, experienceBody, experienceCategoryName, experienceTime;
    private ArrayList<TagsChip> tagsChips = new ArrayList<>();

    public int getExperienceID() {
        return experienceID;
    }

    public void setExperienceID(int experienceID) {
        this.experienceID = experienceID;
    }

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public int getExperienceCategoryID() {
        return experienceCategoryID;
    }

    public void setExperienceCategoryID(int experienceCategoryID) {
        this.experienceCategoryID = experienceCategoryID;
    }

    public String getAuthorImg() {
        return authorImg;
    }

    public void setAuthorImg(String authorImg) {
        this.authorImg = authorImg;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getExperienceTitle() {
        return experienceTitle;
    }

    public void setExperienceTitle(String experienceTitle) {
        this.experienceTitle = experienceTitle;
    }

    public String getExperienceBody() {
        return experienceBody;
    }

    public void setExperienceBody(String experienceBody) {
        this.experienceBody = experienceBody;
    }

    public String getExperienceCategoryName() {
        return experienceCategoryName;
    }

    public void setExperienceCategoryName(String experienceCategoryName) {
        this.experienceCategoryName = experienceCategoryName;
    }

    public String getExperienceTime() {
        return experienceTime;
    }

    public void setExperienceTime(String experienceTime) {
        this.experienceTime = experienceTime;
    }

    public String getExperienceSlug() {
        return experienceSlug;
    }

    public void setExperienceSlug(String experienceSlug) {
        this.experienceSlug = experienceSlug;
    }

    public ArrayList<TagsChip> getTagsChips() {
        return tagsChips;
    }

    public void setTagsChips(ArrayList<TagsChip> tagsChips) {
        this.tagsChips = tagsChips;
    }
}
