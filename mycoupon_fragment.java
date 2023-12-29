package com.example.roading;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class mycoupon_fragment extends Fragment {
    private ListView mycoupon;
    public List<myCouponItemData> dataList;
    public myCouponAdapter adapter;
    public String uNo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.mycoupon, container, false);
        mycoupon = view.findViewById(R.id.mycoupon_list);
        performMyCoupon();
        return view;
    }
    private void performMyCoupon() {
        MainActivity mainActivity = new MainActivity();
        new Thread(() -> {
            uNo = mainActivity.getuNo();
            ArrayList<String> coupon_TypeID = new ArrayList<>();
            ArrayList<ArrayList<String>> sql_result3 = new ArrayList<>();
            // 連接資料庫
            client_socket cs = new client_socket();
            ArrayList<String> perform_MyCoupon = new ArrayList<String>();
            perform_MyCoupon.add("perform_MyCoupon");
            perform_MyCoupon.add(uNo);
            final ArrayList<ArrayList<String>> sql_result = cs.sql(perform_MyCoupon);

            for(int i = 0; i < sql_result.size(); i++) {
                coupon_TypeID.add(sql_result.get(i).get(2));
                ArrayList<String> couponID_to_couponName = new ArrayList<String>();
                couponID_to_couponName.add("couponID_to_couponName");
                couponID_to_couponName.add(sql_result.get(i).get(2));
                ArrayList<ArrayList<String>> sql_result2 = cs.sql(couponID_to_couponName);
                sql_result3.add(sql_result2.get(0));
            }

            requireActivity().runOnUiThread(() -> updateUI(sql_result3));
        }).start();
    }
    private void updateUI(ArrayList<ArrayList<String>> result) {
        Context context = getActivity();
        dataList = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            myCouponItemData itemData = new myCouponItemData(context, result.get(i).get(0));
            dataList.add(itemData);
        }
        adapter = new myCouponAdapter(getActivity(), dataList);
        mycoupon.setAdapter(adapter);
    }
}
