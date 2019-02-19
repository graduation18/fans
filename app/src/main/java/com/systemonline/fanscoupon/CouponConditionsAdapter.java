package com.systemonline.fanscoupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Helpers.Utility;

import java.util.ArrayList;

class CouponConditionsAdapter extends BaseAdapter {
    public ArrayList<String> values;
    Context con;
    Utility utility;
    private String condition;

    CouponConditionsAdapter(Context c, ArrayList<String> values) {
        super(c, R.layout.my_account, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        condition = values.get(position);

        row = inflater.inflate(R.layout.coupon_conditions_row, parent, false);

        TextView singleCondition = (TextView) row.findViewById(R.id.singleCondition);
        singleCondition.setText(condition);
        utility.setTextViewLines(singleCondition, condition);
        return row;
    }


}