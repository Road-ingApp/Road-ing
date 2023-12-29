package com.example.roading;

import android.content.Context;

public class myCouponItemData {
    private int imageResource;
    private String coupon_Name;

    public myCouponItemData(Context context, String couponName) {
        int resourceId = context.getResources().getIdentifier(couponName, "drawable", context.getPackageName());
        this.imageResource = resourceId;
        this.coupon_Name = couponName;
    }

    public int getImageResource() {
        return imageResource;
    }
    public String getCouponName(){return coupon_Name;}
}
