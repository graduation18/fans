package com.systemonline.fanscoupon.friends_tabs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.Fan;
import com.systemonline.fanscoupon.R;

import java.util.ArrayList;


public class MyFriendsAdapter extends BaseAdapter {
    public final ArrayList<Fan> values;

    Context con;
    Utility utility;

    public MyFriendsAdapter(Context c, ArrayList<Fan> values) {
        super(c, R.layout.cred_tab_my_credits, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
//        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            final LayoutInflater inflater = ((Activity) con).getLayoutInflater();
            row = inflater.inflate(R.layout.friends_tab_myfriends_row, parent, false);
        }
        final Fan fan = values.get(position);

//        final ListView lvCustomerTypesCount, lvBadgesCount;
//        final TextView tv_no_customerTypes, tv_no_badges, tv_fan_name;
//        final LinearLayout lin_brands;
//        final ImageView img_friend;

//        ListView lvCustomerTypesCount = (ListView) row.findViewById(R.id.lv_totalCredit);
//        ListView lvBadgesCount = (ListView) row.findViewById(R.id.lv_totalBadges);


        TextView tv_fan_name = (TextView) row.findViewById(R.id.tv_friendName);
        TextView tv_no_customerTypes = (TextView) row.findViewById(R.id.tv_no_customerTypes);
        tv_no_customerTypes.setVisibility(View.GONE);

        TextView tv_no_badges = (TextView) row.findViewById(R.id.tv_no_badges);
        tv_no_badges.setVisibility(View.GONE);

        if (fan.getTotalCustomerTypes().isEmpty())
            tv_no_customerTypes.setVisibility(View.VISIBLE);
        else {
            LinearLayout lin_customerTypes = (LinearLayout) row.findViewById(R.id.cred_customerLinear);
            utility.createDynamicImageViews(fan.getTotalCustomerTypes(), lin_customerTypes);
        }

        if (fan.getTotalBadges().isEmpty())
            tv_no_badges.setVisibility(View.VISIBLE);
        else {
            LinearLayout lin_badges = (LinearLayout) row.findViewById(R.id.badge_badgesLinear);
            utility.createDynamicImageViews(fan.getTotalBadges(), lin_badges);
        }

//        TextView tv_customerTypeCount = (TextView) row.findViewById(R.id.tv_customerTypeCount);
        ImageView img_friend = (ImageView) row.findViewById(R.id.imgv_friend);
//
        tv_fan_name.setText(fan.getFanUserName());
        utility.setTextViewLines(tv_fan_name, fan.getFanUserName());
        Picasso.with(con).load(Const.imagesURL + "users/75x75/" + fan.getFanImage()).placeholder(R.drawable.ph_user).into(img_friend);

        LinearLayout lin_brands = (LinearLayout) row.findViewById(R.id.brandsLinear);
        utility.createDynamicImageViews("brands", fan.getFanBrandsLogos(), lin_brands);
//        utility.createDynamicImageViews(fan.getTotalCustomerTypes(), lin_brands);

        return row;
    }
}