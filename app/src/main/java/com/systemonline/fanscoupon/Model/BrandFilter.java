package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 4/18/2017.
 */

public class BrandFilter {
    private String key, value;

    public BrandFilter(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
