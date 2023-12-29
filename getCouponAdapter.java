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

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class getCouponAdapter extends BaseAdapter {
    private List<getCouponItemData> dataList;
    private Context context;
    public String couponName, user_id, coins, points;

    public getCouponAdapter(Context context, List<getCouponItemData> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.getcoupon_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.coupon);
            holder.textView = convertView.findViewById(R.id.points);
            holder.button = convertView.findViewById(R.id.exchange);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final getCouponItemData itemData = dataList.get(position);
        System.out.println(itemData.getCouponName());
        holder.imageView.setImageResource(itemData.getImageResource());
        holder.textView.setText(itemData.getText());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = new MainActivity();
                // 創建新Thread執行資料更改
                Thread ExchangeCouponThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        user_id = mainActivity.getuNo();
                        couponName = itemData.getCouponName();
                        points = Integer.toString(itemData.getPoint());
                        // 連接資料庫
                        client_socket cs = new client_socket();
                        ArrayList<String> get_userinfo = new ArrayList<String>();
                        get_userinfo.add("get_userinfo");
                        get_userinfo.add(user_id);
                        final ArrayList<ArrayList<String>> sql_result = cs.sql(get_userinfo);
                        coins = sql_result.get(0).get(4);

                        ArrayList<String> couponName_to_couponID = new ArrayList<String>();
                        couponName_to_couponID.add("couponName_to_couponID");
                        couponName_to_couponID.add(couponName);
                        final ArrayList<ArrayList<String>> sql_result2 = cs.sql(couponName_to_couponID);

                        ArrayList<String> exchangeCoupon = new ArrayList<String>();
                        exchangeCoupon.add("exchangeCoupon");
                        exchangeCoupon.add(user_id);
                        exchangeCoupon.add(sql_result2.get(0).get(0));
                        exchangeCoupon.add(points);
                        exchangeCoupon.add(user_id);
                        exchangeCoupon.add(coins);

                        final ArrayList<ArrayList<String>> sql_result3 = cs.sql(exchangeCoupon);

                         //切換回主Thread更新UI
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (Integer.parseInt(sql_result3.get(0).get(0)) > 0 && Integer.parseInt(sql_result3.get(0).get(1)) > 0) {
                                    Toast.makeText(context, "兌換成功", Toast.LENGTH_SHORT).show();
                                    toAnotherPage();
                                } else {
                                    int coin_need = Integer.parseInt(points) - Integer.parseInt(coins);
                                    Toast.makeText(context, "路營幣不足，差" + coin_need + "點", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                ExchangeCouponThread.start();
            }
        });
        return convertView;
    }
    private void toAnotherPage() {
        Intent loginIntent = new Intent(context, home_page.class);
        loginIntent.putExtra("selectedNavItem", "user");
        context.startActivity(loginIntent);
    }

    // ViewHolder 類
    static class ViewHolder {
        ImageView imageView;
        TextView textView;
        Button button;
    }
}
