package com.systemonline.fanscoupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.Model.Filter;

import java.util.ArrayList;

public class SettingsRowAdapter extends BaseAdapter {
    public ArrayList<Filter> values;
    Context con;
    Filter filter;
    String type;
    boolean status;

    SettingsRowAdapter(Context c, ArrayList<Filter> values, String type) {
        super(c, R.layout.social_activities_row, values);
        this.con = c;
        this.values = values;
        this.type = type;
    }

    @Override
    public View getView(final int position, View row, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        filter = values.get(position);

        row = inflater.inflate(R.layout.list_item, parent, false);
        final CheckedTextView checkedTextView = (CheckedTextView) row.findViewById(R.id.lblListItem);
        checkedTextView.setText(filter.getFilterName());
        checkedTextView.setChecked(filter.isChecked());

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedTextView.isChecked()) {
                    checkedTextView.setChecked(false);
                    try {
                        if (type.equals("FB")) {
                            SettingsFragment.allFilters.get(0).get(position).setChecked(false);
                            status = MainActivity.settingsUpdate.remove(SettingsFragment.allFilters.get(0).get(position).getFilterID());
                        } else {
                            SettingsFragment.allFilters.get(1).get(position).setChecked(false);
                            status = MainActivity.settingsUpdate.remove(SettingsFragment.allFilters.get(1).get(position).getFilterID());
                        }
                    } catch (Exception e) {

                    }
                } else {
                    checkedTextView.setChecked(true);
                    try {
                        if (type.equals("FB")) {
                            SettingsFragment.allFilters.get(0).get(position).setChecked(true);

                            status = MainActivity.settingsUpdate.add(SettingsFragment.allFilters.get(0).get(position).getFilterID());
                        } else {
                            SettingsFragment.allFilters.get(1).get(position).setChecked(true);

                            status = MainActivity.settingsUpdate.add(SettingsFragment.allFilters.get(1).get(position).getFilterID());
                        }
                    } catch (Exception e) {

                    }

                }
            }
        });


        return row;
    }


}