package com.systemonline.fanscoupon;

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
import com.systemonline.fanscoupon.Model.Country;

import java.util.ArrayList;

public class CountriesAdapter extends BaseAdapter {
    public ArrayList<Country> values;
    Context con;
    Utility utility;
    private Country country;

    CountriesAdapter(Context c, ArrayList<Country> values) {
        super(c, R.layout.splash_screen, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);

    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        country = values.get(position);

        row = inflater.inflate(R.layout.country_row, parent, false);

        ImageView countryImg = (ImageView) row.findViewById(R.id.country_img);
        Picasso.with(con).load(Const.imagesURL + "country/" + country.getCountryCode() + "_flag.png")
                .placeholder(R.drawable.ph_badge).into(countryImg);

        TextView countryName = (TextView) row.findViewById(R.id.tv_country_name);

        countryName.setText(country.getCountryName());
        utility.setTextViewLines(countryName, country.getCountryName());

        return row;
    }


}