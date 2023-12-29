package com.example.roading;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
public class getCoupon_fragment extends Fragment {
    private ListView getCoupon;
    private List<getCouponItemData> dataList;
    private getCouponAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.getcoupon, container, false);
        getCoupon = view.findViewById(R.id.getcoupon_list);
        // 啟動異步任務以獲取資料庫資料
        performAllCoupon();
        return view;
    }
    private void performAllCoupon() {
        new Thread(() -> {
            // 連接資料庫
            client_socket cs = new client_socket();
            ArrayList<String> perform_AllCoupon = new ArrayList<String>();
            perform_AllCoupon.add("perform_AllCoupon");

            final ArrayList<ArrayList<String>> sql_result = cs.sql(perform_AllCoupon);
            requireActivity().runOnUiThread(() -> updateUI(sql_result));
        }).start();
    }
    private void updateUI(ArrayList<ArrayList<String>> result) {
        Context context = getActivity();
        dataList = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            getCouponItemData itemData = new getCouponItemData(context, result.get(i).get(1), Integer.parseInt(result.get(i).get(2)));
            dataList.add(itemData);
        }
        adapter = new getCouponAdapter(getActivity(), dataList);
        getCoupon.setAdapter(adapter);
    }
}