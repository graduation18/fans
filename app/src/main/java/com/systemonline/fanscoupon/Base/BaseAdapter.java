package com.systemonline.fanscoupon.Base;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.systemonline.fanscoupon.Interfaces.IServiceCallBack;

import org.json.JSONTokener;

import java.util.List;

public class BaseAdapter extends ArrayAdapter implements IServiceCallBack {

    public BaseAdapter(Context context, int resource) {
        super(context, resource);
    }

    public BaseAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public BaseAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
    }

    public BaseAdapter(Context context, int resource, int textViewResourceId, Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public BaseAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    public BaseAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {

    }
}
