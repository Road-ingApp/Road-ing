package com.example.roading;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class myCouponAdapter extends BaseAdapter {
    private List<myCouponItemData> dataList;
    private Context context;
    public String coupon_id, user_id, coupon_Name;


    public myCouponAdapter(Context context, List<myCouponItemData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.mycoupon_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.coupon);
            holder.button = convertView.findViewById(R.id.use);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final myCouponItemData itemData = dataList.get(position);

        holder.imageView.setImageResource(itemData.getImageResource());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = new MainActivity();
                // 創建新Thread執行資料更改
                Thread UseCouponThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        user_id = mainActivity.getuNo();
                        coupon_Name = itemData.getCouponName();
                        // 連接資料庫
                        client_socket cs = new client_socket();
                        ArrayList<String> couponName_to_couponID = new ArrayList<String>();
                        couponName_to_couponID.add("couponName_to_couponID");
                        couponName_to_couponID.add(coupon_Name);
                        final ArrayList<ArrayList<String>> sql_result = cs.sql(couponName_to_couponID);
                        coupon_id = sql_result.get(0).get(0);
                        ArrayList<String> useCoupon = new ArrayList<String>();
                        useCoupon.add("useCoupon");
                        useCoupon.add(user_id);
                        useCoupon.add(coupon_id);

                        final ArrayList<ArrayList<String>> sql_result2 = cs.sql(useCoupon);

                        //切換回主Thread更新UI
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (Integer.parseInt(sql_result2.get(0).get(0)) > 0) {
                                    // 創建DialogFragment實例
                                    barcode barcodeFragment = new barcode();

                                    // 使用FragmentManager
                                    FragmentManager fragmentManager = ((mycoupon) context).getSupportFragmentManager();

                                    // 顯示對話框
                                    barcodeFragment.show(fragmentManager, "dialog");
                                    Toast.makeText(context, "使用成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "使用失敗", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                UseCouponThread.start();
            }
        });
        return convertView;
    }
//    private void toAnotherPage() {
//        Intent intent = new Intent(context, home_page.class);
//        intent.putExtra("selectedNavItem", "user");
//        context.startActivity(intent);
//    }
    static class ViewHolder {
        ImageView imageView;
        Button button;
    }
}