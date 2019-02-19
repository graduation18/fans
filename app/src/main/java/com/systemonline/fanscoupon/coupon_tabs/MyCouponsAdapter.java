package com.systemonline.fanscoupon.coupon_tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.Coupon;
import com.systemonline.fanscoupon.R;

import java.util.ArrayList;


public class MyCouponsAdapter extends BaseAdapter {
    public ArrayList<Coupon> values;
    Context con;
    Coupon coupon;
    Utility utility;

    public MyCouponsAdapter(Context c, ArrayList<Coupon> values) {
        super(c, R.layout.cop_tab_my_coupons, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        coupon = values.get(position);
        row = inflater.inflate(R.layout.cop_tab_my_coupons_row, parent, false);
        TextView tv_mycop_name = (TextView) row.findViewById(R.id.tv_mycop_name);
        TextView tv_mycop_enddate = (TextView) row.findViewById(R.id.tv_mycop_enddate);
        TextView tv_mycop_type = (TextView) row.findViewById(R.id.tv_coupType);
        ImageView img_mycop = (ImageView) row.findViewById(R.id.img_mycop);

        tv_mycop_type.setText(coupon.getCouponType());
        utility.setTextViewLines(tv_mycop_type, coupon.getCouponType());
        tv_mycop_name.setText(coupon.getCouponName());
        utility.setTextViewLines(tv_mycop_name, coupon.getCouponName());
        tv_mycop_enddate.setText(coupon.getCouponEndDate().split(" ")[0]);
        utility.setTextViewLines(tv_mycop_enddate, coupon.getCouponEndDate().split(" ")[0]);
        Picasso.with(con).load(Const.imagesURL + "coupons/320x172/" + coupon.getCouponImage()).placeholder(R.drawable.ph_coupon).into(img_mycop);

        return row;
    }
}