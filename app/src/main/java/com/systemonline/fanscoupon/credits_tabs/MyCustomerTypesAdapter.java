package com.systemonline.fanscoupon.credits_tabs;

import android.app.Activity;
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
import com.systemonline.fanscoupon.Model.CustomerType;
import com.systemonline.fanscoupon.R;

import java.util.ArrayList;


public class MyCustomerTypesAdapter extends BaseAdapter {
    public ArrayList<CustomerType> values;
    Context con;
    CustomerType customerType;
    Utility utility;

    public MyCustomerTypesAdapter(Context c, ArrayList<CustomerType> values) {
        super(c, R.layout.cred_tab_my_credits, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
//        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            LayoutInflater inflater = ((Activity) con).getLayoutInflater();
            row = inflater.inflate(R.layout.credit_tab_total_credit_row, parent, false);
        }
        customerType = values.get(position);

        TextView tv_customerTypeCount = (TextView) row.findViewById(R.id.tv_customerTypeCount);
        ImageView img_customerType = (ImageView) row.findViewById(R.id.img_customerType);

        tv_customerTypeCount.setText(String.valueOf(customerType.getCustomerTypeCount()));
        utility.setTextViewLines(tv_customerTypeCount, String.valueOf(customerType.getCustomerTypeCount()));
        Picasso.with(con).load(Const.imagesURL + "badges/" + customerType.getCustomerTypeImage()).placeholder(R.drawable.ph_badge).into(img_customerType);

        return row;
    }
}