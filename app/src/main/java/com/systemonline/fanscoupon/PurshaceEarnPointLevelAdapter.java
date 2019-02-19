package com.systemonline.fanscoupon;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.EarnedPointsAverage;

import java.util.ArrayList;

public class PurshaceEarnPointLevelAdapter extends BaseAdapter {
    public ArrayList<EarnedPointsAverage> values;
    Context con;
    Utility utility;
    SpannableStringBuilder builder, builderTemp;
    String strBuild;
    SpannableString strColor;
    private EarnedPointsAverage epa;

    public PurshaceEarnPointLevelAdapter(Context c, ArrayList<EarnedPointsAverage> values) {
        super(c, R.layout.loyalty_single_coupon_fragment, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        epa = values.get(position);

        row = inflater.inflate(R.layout.purshace_earn_point_level_row, parent, false);

        TextView lvl_num = (TextView) row.findViewById(R.id.lvl_num);
        TextView tv_lvl = (TextView) row.findViewById(R.id.tv_lvl);


        lvl_num.setText(new StringBuilder().append(con.getResources().getString(R.string.level))
                .append(" ").append(epa.getPhase()).toString());
        utility.setTextViewLines(lvl_num,
                new StringBuilder().append(con.getResources().getString(R.string.level))
                        .append(" ").append(epa.getPhase()).toString());

        builderTemp = new SpannableStringBuilder();
        stringBuilder(R.string.from, epa.getPurchaseFrom());
        builderTemp.append(builder);
        builderTemp.append(" ");
        stringBuilder(R.string.to, epa.getPurchaseTo());
        builderTemp.append(builder);
        builderTemp.append(" ");
        stringBuilder(R.string.equal, "1");
        builderTemp.append(builder);
        builderTemp.append(" ");
        stringBuilder(R.string.poin_, "1 " + con.getResources().getString(R.string.le));
        builderTemp.append(builder);
        tv_lvl.setText(builderTemp, TextView.BufferType.SPANNABLE);

        utility.setTextViewLines(tv_lvl, builderTemp.toString());


        return row;
    }

    void stringBuilder(int strId, String strVar) {
        builder = new SpannableStringBuilder();
        strBuild = con.getResources().getString(strId);
        strColor = new SpannableString(strBuild);
        strColor.setSpan(new ForegroundColorSpan(Color.BLACK), 0, strBuild.length(), 0);
        builder.append(strColor);
        builder.append(" ");
        strBuild = strVar;
        strColor = new SpannableString(strBuild);
        strColor.setSpan(new ForegroundColorSpan(con.getResources().getColor(R.color.red)), 0, strBuild.length(), 0);
        builder.append(strColor);
    }
}