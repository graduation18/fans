package com.systemonline.fanscoupon;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.BrandBranch;

import java.util.ArrayList;

public class BranchesAvailabilityAdapter extends BaseAdapter {
    public ArrayList<BrandBranch> values;
    Context con;
    Utility utility;
    private SpannableStringBuilder builder;

    BranchesAvailabilityAdapter(Context c, ArrayList<BrandBranch> values) {
        super(c, R.layout.loyalty_single_coupon_fragment, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        BrandBranch branch = values.get(position);

        row = inflater.inflate(R.layout.branches_availability_row, parent, false);


        TextView time_available = (TextView) row.findViewById(R.id.tv_time_availibility);
        TextView tv_br_name = (TextView) row.findViewById(R.id.branch_name);
        tv_br_name.setText(branch.getBranchName());
        utility.setTextViewLines(tv_br_name, branch.getBranchName());
        TextView tv_br_address = (TextView) row.findViewById(R.id.branch_address);
        tv_br_address.setText(branch.getBranchAddress());
        utility.setTextViewLines(tv_br_address, branch.getBranchAddress());
        TextView tv_br_phone = (TextView) row.findViewById(R.id.branch_phone);
        tv_br_phone.setText(branch.getBranchPhone());
        utility.setTextViewLines(tv_br_phone, branch.getBranchPhone());

        ArrayList<LinearLayout> allLinear = new ArrayList<>();


        LinearLayout lin_day1 = (LinearLayout) row.findViewById(R.id.lin_day1);
        allLinear.add(lin_day1);
        LinearLayout lin_day2 = (LinearLayout) row.findViewById(R.id.lin_day2);
        allLinear.add(lin_day2);
        LinearLayout lin_day3 = (LinearLayout) row.findViewById(R.id.lin_day3);
        allLinear.add(lin_day3);
        LinearLayout lin_day4 = (LinearLayout) row.findViewById(R.id.lin_day4);
        allLinear.add(lin_day4);
        LinearLayout lin_day5 = (LinearLayout) row.findViewById(R.id.lin_day5);
        allLinear.add(lin_day5);
        LinearLayout lin_day6 = (LinearLayout) row.findViewById(R.id.lin_day6);
        allLinear.add(lin_day6);
        LinearLayout lin_day7 = (LinearLayout) row.findViewById(R.id.lin_day7);
        allLinear.add(lin_day7);

        ArrayList<TextView> allFields = new ArrayList<>();

        TextView day_1 = (TextView) row.findViewById(R.id.day_1);
        allFields.add(day_1);
        TextView day_2 = (TextView) row.findViewById(R.id.day_2);
        allFields.add(day_2);
        TextView day_3 = (TextView) row.findViewById(R.id.day_3);
        allFields.add(day_3);
        TextView day_4 = (TextView) row.findViewById(R.id.day_4);
        allFields.add(day_4);
        TextView day_5 = (TextView) row.findViewById(R.id.day_5);
        allFields.add(day_5);
        TextView day_6 = (TextView) row.findViewById(R.id.day_6);
        allFields.add(day_6);
        TextView day_7 = (TextView) row.findViewById(R.id.day_7);
        allFields.add(day_7);

        if (branch.getDayWorkingHours().isEmpty()) {
            time_available.setVisibility(View.GONE);
            for (int i = 0; i < allLinear.size(); i++) {
                allLinear.get(i).setVisibility(View.GONE);
            }
        } else
            for (int i = 0; i < branch.getDayWorkingHours().size(); i++) {
                if (branch.getDayWorkingHours().get(i).getFromHour() == null || branch.getDayWorkingHours().get(i).getFromHour().equals("null"))
                    allLinear.get(i).setVisibility(View.GONE);
                else {
                    SpannableStringBuilder builderTemp = new SpannableStringBuilder();
                    stringBuilder(branch.getDayWorkingHours().get(i).getDayName(), R.string.from);
                    builderTemp.append(builder);
                    builderTemp.append(" ");
                    stringBuilder(branch.getDayWorkingHours().get(i).getFromHour(), R.string.to);
                    builderTemp.append(builder);
                    builderTemp.append(" ");
                    stringBuilder(branch.getDayWorkingHours().get(i).getToHour(), R.string.empty);
                    builderTemp.append(builder);
                    allFields.get(i).setText(builderTemp, TextView.BufferType.SPANNABLE);
                    utility.setTextViewLines(allFields.get(i), builderTemp.toString());

                }
            }
        return row;
    }

    private void stringBuilder(String strVar, int strId) {
        builder = new SpannableStringBuilder();
        String strBuild = strVar;
        SpannableString strColor = new SpannableString(strBuild);
        strColor.setSpan(new ForegroundColorSpan(Color.RED), 0, strBuild.length(), 0);
        builder.append(strColor);
        builder.append(" ");
        strBuild = con.getResources().getString(strId);
        strColor = new SpannableString(strBuild);
        strColor.setSpan(new ForegroundColorSpan(Color.BLACK), 0, strBuild.length(), 0);
        builder.append(strColor);
    }
}