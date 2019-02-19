package com.systemonline.fanscoupon.coupon_tabs;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.FilterCheckboxStatus;
import com.systemonline.fanscoupon.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilterAdapter extends BaseExpandableListAdapter {

    public static ArrayList<FilterCheckboxStatus> ad_couponCategoriesCheckboxStatuses,
            ad_verySpecialsCheckboxStatuses, ad_favouriteBrandCheckboxStatuses,
            ad_interestedINCheckboxStatuses;
    boolean checkboxStatus;
    Utility utility;
    private Context con;
    private List<String> _listDataHeader;
    private HashMap<String, List<String>> _listDataChild;

    public FilterAdapter(Context context, List<String> listDataHeader,
                         HashMap<String, List<String>> listChildData) {
        utility = new Utility(context);
        ad_couponCategoriesCheckboxStatuses = new ArrayList<>();
        ad_verySpecialsCheckboxStatuses = new ArrayList<>();
        ad_favouriteBrandCheckboxStatuses = new ArrayList<>();
        ad_interestedINCheckboxStatuses = new ArrayList<>();

        Log.e("filter adapter", "constructor");

        this.con = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

        for (int i = 0; i < CouponTab.couponCategoriesCheckboxStatuses.size(); i++) {
            ad_couponCategoriesCheckboxStatuses.add(new FilterCheckboxStatus(CouponTab.couponCategoriesCheckboxStatuses.get(i).getFilterID(), CouponTab.couponCategoriesCheckboxStatuses.get(i).getFilterChecked(), CouponTab.couponCategoriesCheckboxStatuses.get(i).getFilterName()));
        }
        for (int i = 0; i < CouponTab.verySpecialsCheckboxStatuses.size(); i++) {
            ad_verySpecialsCheckboxStatuses.add(new FilterCheckboxStatus(CouponTab.verySpecialsCheckboxStatuses.get(i).getFilterID(), CouponTab.verySpecialsCheckboxStatuses.get(i).getFilterChecked(), CouponTab.verySpecialsCheckboxStatuses.get(i).getFilterName()));
        }
        for (int i = 0; i < CouponTab.favouriteBrandCheckboxStatuses.size(); i++) {
            ad_favouriteBrandCheckboxStatuses.add(new FilterCheckboxStatus(CouponTab.favouriteBrandCheckboxStatuses.get(i).getFilterID(), CouponTab.favouriteBrandCheckboxStatuses.get(i).getFilterChecked(), CouponTab.favouriteBrandCheckboxStatuses.get(i).getFilterName()));
        }
        for (int i = 0; i < CouponTab.interestedINCheckboxStatuses.size(); i++) {
            ad_interestedINCheckboxStatuses.add(new FilterCheckboxStatus(CouponTab.interestedINCheckboxStatuses.get(i).getFilterID(), CouponTab.interestedINCheckboxStatuses.get(i).getFilterChecked(), CouponTab.interestedINCheckboxStatuses.get(i).getFilterName()));
        }

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.con
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        final CheckedTextView txtListChild = (CheckedTextView) convertView
                .findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        utility.setTextViewLines(txtListChild, childText);
        txtListChild.setChecked(false);

        if (groupPosition == 0) {
            if (ad_verySpecialsCheckboxStatuses.get(childPosition).getFilterChecked())
                txtListChild.setChecked(true);
        } else if (groupPosition == 1) {
            if (ad_favouriteBrandCheckboxStatuses.get(childPosition).getFilterChecked())
                txtListChild.setChecked(true);
        } else if (groupPosition == 2) {
            if (ad_interestedINCheckboxStatuses.get(childPosition).getFilterChecked())
                txtListChild.setChecked(true);
        } else {
            if (ad_couponCategoriesCheckboxStatuses.get(childPosition).getFilterChecked())
                txtListChild.setChecked(true);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtListChild.isChecked()) {
                    checkboxStatus = false;
                } else {
                    checkboxStatus = true;
                }
                txtListChild.setChecked(checkboxStatus);


                if (groupPosition == 0)
                    ad_verySpecialsCheckboxStatuses.get(childPosition).setFilterChecked(checkboxStatus);
                else if (groupPosition == 1)
                    ad_favouriteBrandCheckboxStatuses.get(childPosition).setFilterChecked(checkboxStatus);
                else if (groupPosition == 2)
                    ad_interestedINCheckboxStatuses.get(childPosition).setFilterChecked(checkboxStatus);
                else
                    ad_couponCategoriesCheckboxStatuses.get(childPosition).setFilterChecked(checkboxStatus);

            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.con
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);
        utility.setTextViewLines(lblListHeader, headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
