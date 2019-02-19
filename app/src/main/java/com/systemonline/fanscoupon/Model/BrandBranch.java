package com.systemonline.fanscoupon.Model;


import com.systemonline.fanscoupon.MainActivity;
import com.systemonline.fanscoupon.R;

import java.util.ArrayList;

public class BrandBranch {
    private String branchName, branchAddress, branchCity = "", branchPhone, startTime, endTime, offDay, offName;
    private boolean mainBranch, available;
    private double branchLatitude, BranchLongitude;
    private ArrayList<DayWorkingHour> dayWorkingHours;

    public BrandBranch() {
        this.branchPhone = MainActivity._utility.getCurrentActivity().getString(R.string.no_phone);
    }

    public String getOffName() {
        return offName;
    }

    public void setOffName(String offName) {
        this.offName = offName;
    }

    public String getBranchPhone() {
        return branchPhone;
    }

    public void setBranchPhone(String branchPhone) {
        this.branchPhone = branchPhone;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public String getBranchCity() {
        return branchCity;
    }

    public void setBranchCity(String branchCity) {
        this.branchCity = branchCity;
    }

    public Double getBranchLatitude() {
        return branchLatitude;
    }

    public void setBranchLatitude(Double branchLatitude) {
        this.branchLatitude = branchLatitude;
    }

    public Double getBranchLongitude() {
        return BranchLongitude;
    }

    public void setBranchLongitude(Double branchLongitude) {
        this.BranchLongitude = branchLongitude;
    }

    public boolean getMainBranch() {
        return mainBranch;
    }

    public void setMainBranch(boolean mainBranch) {
        this.mainBranch = mainBranch;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOffDay() {
        return offDay;
    }

    public void setOffDay(String offDay) {
        this.offDay = offDay;
    }

    public ArrayList<DayWorkingHour> getDayWorkingHours() {
        return dayWorkingHours;
    }

    public void setDayWorkingHours(ArrayList<DayWorkingHour> dayWorkingHours) {
        this.dayWorkingHours = dayWorkingHours;
    }
}
