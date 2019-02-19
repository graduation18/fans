package com.systemonline.fanscoupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.FaceBookPage;

import java.util.ArrayList;

class FanPagesAdapter extends BaseAdapter {
    public ArrayList<FaceBookPage> values;
    Context con;
    Utility utility;
    private FaceBookPage faceBookPage;

    FanPagesAdapter(Context c, ArrayList<FaceBookPage> values) {
        super(c, R.layout.my_account, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        faceBookPage = values.get(position);

        row = inflater.inflate(R.layout.pages_row, parent, false);

        TextView page_name = (TextView) row.findViewById(R.id.page_name);
        page_name.setText(faceBookPage.getPageName());
        utility.setTextViewLines(page_name, faceBookPage.getPageName());

        return row;
    }


}