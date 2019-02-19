package com.systemonline.fanscoupon.Model;


public class FilterCheckboxStatus {
    private int filterID;
    private boolean filterChecked;
    private String filterName;


    public FilterCheckboxStatus(Integer filterID, boolean filterChecked, String filterName) {
        this.filterID = filterID;
        this.filterChecked = filterChecked;
        this.filterName = filterName;
    }

    public Integer getFilterID() {
        return filterID;
    }

    public void setFilterID(Integer filterID) {
        this.filterID = filterID;
    }

    public boolean getFilterChecked() {
        return filterChecked;
    }

    public void setFilterChecked(boolean filterChecked) {
        this.filterChecked = filterChecked;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
}
