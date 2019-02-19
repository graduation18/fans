package com.systemonline.fanscoupon.credits_tabs;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CreditPerBrand;
import com.systemonline.fanscoupon.R;

import java.util.ArrayList;

/**
 * Created by SystemOnline1 on 4/16/2017.
 */

public class CreditBrandsAdapter extends BaseAdapter {

    public ArrayList<CreditPerBrand> values;
    private Context con;
    private Utility utility;

    CreditBrandsAdapter(Context c, ArrayList<CreditPerBrand> values) {
        super(c, R.layout.cred_tab_my_credits, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
    }

    @NonNull
    @Override
    public View getView(int position, View row, @NonNull ViewGroup parent) {


        if (row == null) {
            LayoutInflater inflater = ((Activity) con).getLayoutInflater();
            row = inflater.inflate(R.layout.credit_brands_row, parent, false);
        }
        final CreditPerBrand brand = values.get(position);

        ImageView brandCover = (ImageView) row.findViewById(R.id.brandImg);
//        Picasso.with(con).load(Const.imagesURL + "brands/160x160/" + brand.getBrandCover()).into(brandCover);
        Picasso.with(con).load(Const.imagesURL + "brands/850x315/" + brand.getBrandCover()).placeholder(R.drawable.ph_brand_cover).into(brandCover);
        ImageView brandLogo = (ImageView) row.findViewById(R.id.brandImgCircle);
        Picasso.with(con).load(Const.imagesURL + "brands/50x50/" + brand.getBrandImage()).placeholder(R.drawable.ph_brand).into(brandLogo);

        ImageView fanCustomerType = (ImageView) row.findViewById(R.id.fanCustomerType);
        Picasso.with(con).load(Const.imagesURL + "customer-types/50x50/" + brand.getCustomerType().getCustomerTypeImage()).placeholder(R.drawable.ph_badge).into(fanCustomerType);

        TextView totalPoints = (TextView) row.findViewById(R.id.tv_totalPoints);
        TextView usedPoints = (TextView) row.findViewById(R.id.tv_exchangedPoints);
        TextView exchangedPoints = (TextView) row.findViewById(R.id.tv_usedCoupons);
        TextView availableCoupons = (TextView) row.findViewById(R.id.tv_availableCoupons);


        totalPoints.setText(utility.formatNumber(brand.getTotalPoints()));
        exchangedPoints.setText(utility.formatNumber(brand.getExchangedPoints()));
        usedPoints.setText(utility.formatNumber(brand.getUsedCoupons()));
        availableCoupons.setText(utility.formatNumber(brand.getAvailableCoupons()));


        return row;
    }


}
