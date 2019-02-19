package com.systemonline.fanscoupon.Model;

import java.util.ArrayList;


public class Filter {
    private String filterName;
    private int filterID;
    private ArrayList<Filter> filterChildren;
    private boolean checked = false;

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public Integer getFilterID() {
        return filterID;
    }

    public void setFilterID(Integer filterID) {
        this.filterID = filterID;
    }

    public ArrayList<Filter> getFilterChildren() {
        return filterChildren;
    }

    public void setFilterChildren(ArrayList<Filter> filterChildren) {
        this.filterChildren = filterChildren;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
