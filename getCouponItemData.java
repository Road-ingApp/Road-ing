package com.example.roading;

import android.content.Context;

public class getCouponItemData {
    private int imageResource, point;
    private String needPoint, couponName;

    public getCouponItemData(Context context, String couponName, int couponPoint) {
        int resourceId = context.getResources().getIdentifier(couponName, "drawable", context.getPackageName());
        this.imageResource = resourceId;
        this.needPoint = couponPoint + "點";
        this.point = couponPoint;
        this.couponName = couponName;
    }

    public int getImageResource() {
        return imageResource;
    }
    public String getText() {
        return needPoint;
    }
    public int getPoint(){return  point;}
    public String getCouponName(){return couponName;}
}
