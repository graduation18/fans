package com.systemonline.fanscoupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.BrandBranch;

import java.util.ArrayList;

public class BranchesAdapter extends BaseAdapter {
    public ArrayList<BrandBranch> values;
    Context con;
    Utility utility;
    private BrandBranch branch;

    public BranchesAdapter(Context c, ArrayList<BrandBranch> values) {
        super(c, R.layout.loyalty_single_coupon_fragment, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
    }


    @Override
    public View getView(int position, View row, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        branch = values.get(position);

        row = inflater.inflate(R.layout.branches_row, parent, false);

        TextView tv_br_name = (TextView) row.findViewById(R.id.tv_br_name);
        TextView tv_br_address = (TextView) row.findViewById(R.id.tv_br_address);
        TextView tv_br_city = (TextView) row.findViewById(R.id.tv_br_city);

        tv_br_name.setText(branch.getBranchName());
        utility.setTextViewLines(tv_br_name, branch.getBranchName());
        tv_br_address.setText(branch.getBranchAddress());
        utility.setTextViewLines(tv_br_address, branch.getBranchAddress());
        tv_br_city.setText(branch.getBranchCity());
        utility.setTextViewLines(tv_br_city, branch.getBranchCity());
        return row;
    }


}
