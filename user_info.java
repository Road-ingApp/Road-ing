package com.example.roading;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class user_info extends Fragment implements editName.EditNameListener {
    public View view;
    private Button logout_btn, myCoupon, getCoupon, sign, development, info_change;
    private ImageButton editName;
    private static TextView Username, Coins;
    public static String uName, coins;
    private SharedPreferences sharedPreferences;
    private static final String SharedPreferences_NAME = "UserSign";
    public boolean buttonReset = false;
    MainActivity mainActivity = new MainActivity();
    String user_ID = mainActivity.getuNo();
    String lastClickDateKey = "lastClickDate_" + user_ID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Toast.makeText(requireActivity(),"會員", Toast.LENGTH_SHORT).show();

        sharedPreferences = getActivity().getSharedPreferences(SharedPreferences_NAME, Context.MODE_PRIVATE);

        new Thread(new getUserInfoTask()).start();
        view = inflater.inflate(R.layout.user_info, container, false);
        Username = view.findViewById(R.id.username);
        Coins = view.findViewById(R.id.coins);

        logout_btn = view.findViewById(R.id.logout);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
            }
        });

        editName = view.findViewById(R.id.editName);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editName edit = new editName();
                edit.show(getChildFragmentManager(), "EditName");
            }
        });

        myCoupon = view.findViewById(R.id.myCoupon);
        myCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), mycoupon.class);
                startActivity(intent);
            }
        });

        getCoupon = view.findViewById(R.id.getCoupon);
        getCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),getCoupon.class);
                startActivity(intent);
            }
        });

        info_change = view.findViewById(R.id.info_change);
        info_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),info_change.class);
                startActivity(intent);
            }
        });

        development = view.findViewById(R.id.development);
        development.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), developement.class);
                startActivity(intent);
            }
        });

        sign = view.findViewById(R.id.sign);
        //if (!buttonReset) {
            if (isButtonClickable()) {
                sign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //if (isButtonClickable()) {
                        Thread UserSignThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 連接資料庫
                                client_socket cs = new client_socket();
                                ArrayList<String> user_sign = new ArrayList<String>();
                                user_sign.add("user_sign");
                                user_sign.add(Integer.toString(10));
                                user_sign.add(user_ID);
                                final ArrayList<ArrayList<String>> sql_result = cs.sql(user_sign);

                                ArrayList<String> get_userinfo = new ArrayList<String>();
                                get_userinfo.add("get_userinfo");
                                get_userinfo.add(user_ID);

                                final ArrayList<ArrayList<String>> sql_result2 = cs.sql(get_userinfo);
                                coins = sql_result2.get(0).get(4);

                                //切換回主Thread更新UI
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println(sql_result);
                                        if (Integer.parseInt(sql_result.get(0).get(0)) > 0) {
                                            sign.setEnabled(false);
                                            sign.setTextColor(Color.GRAY);
                                            sign.setBackgroundColor(Color.LTGRAY);
                                            sign.setText("今日已簽到");
                                            Toast.makeText(getContext(), "簽到成功，獲得10點路營幣！", Toast.LENGTH_SHORT).show();
                                            saveLastClickDate();
                                            Coins.setText(coins);
                                            buttonReset = false;
                                        } else {
                                            Toast.makeText(getContext(), "簽到失敗", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                        UserSignThread.start();
                    }
                });
            } else {
                sign.setEnabled(false);
                sign.setTextColor(Color.GRAY);
                sign.setBackgroundColor(Color.LTGRAY);
                sign.setText("今日已簽到");
            }

        setResetButtonTimer(sign);
        return view;
    }
    class getUserInfoTask implements Runnable {
        @Override
        public void run() {
            // 連接資料庫
            client_socket cs = new client_socket();
            ArrayList<String> get_userinfo = new ArrayList<String>();
            get_userinfo.add("get_userinfo");
            get_userinfo.add(user_ID);

            final ArrayList<ArrayList<String>> sql_result = cs.sql(get_userinfo);

            // 切換到主Thread更新UI
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!sql_result.isEmpty()) {
                        uName = sql_result.get(0).get(0);
                        coins = sql_result.get(0).get(4);
                        Username.setText(uName);
                        Coins.setText(coins);
                    }
                }
            });
        }
    }
    private boolean isButtonClickable() {
        long lastClickDate = sharedPreferences.getLong(lastClickDateKey, 0);

        // 取得今天的日期
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Taipei"));
        Calendar calendar = Calendar.getInstance();

        Date today = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        long todayDate = Long.parseLong(sdf.format(today));
        // 檢查是否是同一天
        return lastClickDate != todayDate;
    }

    private void saveLastClickDate() {
        // 保存今天的日期作為最后點擊按鈕的日期
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Taipei"));
        Calendar calendar = Calendar.getInstance();

        Date today = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        long todayDate = Long.parseLong(sdf.format(today));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(lastClickDateKey, todayDate);
        editor.apply();
    }

    private void setResetButtonTimer(Button button) {
        Timer timer = new Timer();
        // 計算距離下次重置的時間
        long delay = calculateDelayUntilReset();

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Taipei"));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date resetTime = new Date(System.currentTimeMillis() + delay);
        Log.d("ResetButtonTimer", "Timer will trigger at: " + sdf.format(resetTime));

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("ResetButtonTimer", "Timer triggered!");
                // 在特定時間點後，重置按鈕狀態
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        button.setEnabled(true);
                        sign.setTextColor(Color.parseColor("#616A6B"));
                        sign.setBackgroundColor(Color.WHITE);
                        button.setText("每日簽到");
                        //buttonReset = true;
                    }
                });
            }
        }, delay);
    }

    private long calculateDelayUntilReset() {
        // 計算距離下次重置的時間
        // 設每天在晚上12點重置按鈕狀態
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Taipei"));
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long currentTime = System.currentTimeMillis();
        long resetTime = calendar.getTimeInMillis();

        if (currentTime >= resetTime) {
            // 如果當前時間已經過了晚上12點，將下次重置時間設為明天的晚上12點
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            resetTime = calendar.getTimeInMillis();
        }

        return resetTime - currentTime;
    }

    @Override
    public void onEditNameComplete(String inputName) {
        EditName(inputName);
    }
    @Override
    public void EditName(String inputName) {
        // 創建新Thread執行資料更改
        Thread UserNameChangeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 連接資料庫
                client_socket cs = new client_socket();
                ArrayList<String> username_change = new ArrayList<String>();
                username_change.add("username_change");
                username_change.add(inputName);
                username_change.add(user_ID);

                final ArrayList<ArrayList<String>> sql_result = cs.sql(username_change);

                // 切換回主Thread更新UI
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (Integer.parseInt(sql_result.get(0).get(0)) > 0) {
                            Toast.makeText(getContext(), "更改成功", Toast.LENGTH_SHORT).show();
                            Username.setText(inputName);
                        } else {
                            Toast.makeText(getContext(), "更改失敗", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        UserNameChangeThread.start();
    }
}