package com.systemonline.fanscoupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.EventRate;

import java.util.ArrayList;

public class EventRateAdapter extends BaseAdapter {
    public ArrayList<EventRate> values;
    Context con;
    Utility utility;
    private EventRate eventRate;

    EventRateAdapter(Context c, ArrayList<EventRate> values) {
        super(c, R.layout.loyalty_single_coupon_fragment, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);

    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        eventRate = values.get(position);

        row = inflater.inflate(R.layout.event_rate_row, parent, false);

        TextView tv_event_name = (TextView) row.findViewById(R.id.tv_event_name);
        TextView tv_points_earned = (TextView) row.findViewById(R.id.tv_points_earned);

        tv_event_name.setText(eventRate.getEventName());
        utility.setTextViewLines(tv_event_name, eventRate.getEventName());
        tv_points_earned.setText(eventRate.getPointsEarned());
        utility.setTextViewLines(tv_points_earned, eventRate.getPointsEarned());

        return row;
    }


}