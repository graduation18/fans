package com.systemonline.fanscoupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Model.DayWorkingHour;

import java.util.ArrayList;

class CouponDaysAdapter extends BaseAdapter {
    public ArrayList<DayWorkingHour> values;
    Context con;
    DayWorkingHour copDay;

    CouponDaysAdapter(Context c, ArrayList<DayWorkingHour> values) {
        super(c, R.layout.qualified_fragment, values);
        this.con = c;
        this.values = values;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        copDay = values.get(position);

        row = inflater.inflate(R.layout.coupon_days_row, parent, false);

        TextView day_name = (TextView) row.findViewById(R.id.day_name);
        TextView hour_from = (TextView) row.findViewById(R.id.hour_from);
        TextView hour_to = (TextView) row.findViewById(R.id.hour_to);

        day_name.setText(copDay.getDayName());
        hour_from.setText(copDay.getFromHour());
        hour_to.setText(copDay.getToHour());

        return row;
    }


}
