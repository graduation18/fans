package com.systemonline.fanscoupon.brands_tabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import com.systemonline.fanscoupon.Model.Brand;
import com.systemonline.fanscoupon.R;

import java.util.ArrayList;

/**
 * Created by SystemOnline1 on 4/16/2017.
 */

public class MyBrandsAdapter extends BaseAdapter {

    public ArrayList<Brand> values;
    private Context con;
    private Utility utility;

    MyBrandsAdapter(Context c, ArrayList<Brand> values) {
        super(c, R.layout.brand_tab_my_brands, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
    }

    @NonNull
    @Override
    public View getView(int position, View row, @NonNull ViewGroup parent) {


        if (row == null) {
            LayoutInflater inflater = ((Activity) con).getLayoutInflater();
            row = inflater.inflate(R.layout.brand_tab_my_brands_row, parent, false);
        }
        final Brand brand = values.get(position);

        ImageView brandCover = (ImageView) row.findViewById(R.id.brandImg);
        Picasso.with(con).load(Const.imagesURL + "brands/850x315/" + brand.getBrandCover()).placeholder(R.drawable.ph_brand_cover).into(brandCover);
        ImageView brandLogo = (ImageView) row.findViewById(R.id.brandImgCircle);
        Picasso.with(con).load(Const.imagesURL + "brands/50x50/" + brand.getBrandImage()).placeholder(R.drawable.ph_brand).into(brandLogo);

        LinearLayout brand_star = (LinearLayout) row.findViewById(R.id.brand_star);


        if (!brand.getBrandRatingStarsCount().equals("null")) {
            utility.createDynamicStars(Double.parseDouble(brand.getBrandRatingStarsCount()), brand_star);
        }

        ImageView hasLoyalty = (ImageView) row.findViewById(R.id.tv_hasLoyalty);
        TextView couponsCount = (TextView) row.findViewById(R.id.tv_couponsCount);
        TextView fansCount = (TextView) row.findViewById(R.id.tv_fansCount);

        hasLoyalty.setImageResource(brand.isHasLoyalty() ? R.drawable.right : R.drawable.wrong);
        couponsCount.setText(String.valueOf(brand.getBrandCouponsCount()));
        fansCount.setText(String.valueOf(brand.getBrandFansCount()));

        ImageView brandFacebook = (ImageView) row.findViewById(R.id.brandFacebook);
        brandFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!brand.getBrandSocialNetworks().get(0).getBrandAccountID().isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(brand.getBrandSocialNetworks().get(0).getBrandAccountID()));
                    con.startActivity(browserIntent);
                } else
                    utility.showMessage(con.getString(R.string.no_brand_page));
            }
        });
        ImageView brandGoogle = (ImageView) row.findViewById(R.id.brandGoogle);
        brandGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!brand.getBrandSocialNetworks().get(1).getBrandAccountID().isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(brand.getBrandSocialNetworks().get(2).getBrandAccountID()));
                    con.startActivity(browserIntent);
                } else
                    utility.showMessage(con.getString(R.string.no_brand_page));
            }
        });
        ImageView brandTwitter = (ImageView) row.findViewById(R.id.brandTwitter);
        brandTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!brand.getBrandSocialNetworks().get(2).getBrandAccountID().isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(brand.getBrandSocialNetworks().get(1).getBrandAccountID()));
                    con.startActivity(browserIntent);
                } else
                    utility.showMessage(con.getString(R.string.no_brand_page));
            }
        });

        return row;
    }


}
