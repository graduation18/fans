package com.systemonline.fanscoupon.Model;

/**
 * Created by SystemOnline1 on 7/27/2016.
 */
public class CustomerType {
    private String customerTypeName, customerTypeImage;
    private int customerTypeCount;

    public String getCustomerTypeName() {
        return customerTypeName;
    }

    public void setCustomerTypeName(String customerTypeName) {
        this.customerTypeName = customerTypeName;
    }

    public String getCustomerTypeImage() {
        return customerTypeImage;
    }

    public void setCustomerTypeImage(String customerTypeImage) {
        this.customerTypeImage = customerTypeImage;
    }

    public int getCustomerTypeCount() {
        return customerTypeCount;
    }

    public void setCustomerTypeCount(int customerTypeCount) {
        this.customerTypeCount = customerTypeCount;
    }
}
