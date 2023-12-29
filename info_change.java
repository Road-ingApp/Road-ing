package com.example.roading;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

public class info_change extends AppCompatActivity{
    private Button confirm_btn, cancel_btn;
    private static TextInputEditText password;
    private static TextInputEditText email;
    private static TextInputEditText phone;
    public static String uPwd;
    public static String uEmail;
    public static String uPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.info_change);

        MainActivity mainActivity = new MainActivity();
        String uNo = mainActivity.getuNo();

        new getUserInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uNo);

        confirm_btn = findViewById(R.id.confirm_btn);
        cancel_btn = findViewById(R.id.cancel_btn);
        password = findViewById(R.id.userPswinput);
        email = findViewById(R.id.userMailinput);
        phone = findViewById(R.id.userPhoneinput);

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Password = password.getText().toString();
                String Email = email.getText().toString();
                String Phone = phone.getText().toString();

                if(Password.length() < 2 ){
                    Toast.makeText(getApplicationContext(),"密碼不符合要求請重新輸入",Toast.LENGTH_LONG).show();
                }
                else if (!isValidEmailAddress(Email)){
                    Toast.makeText(getApplicationContext(),"Email不符合格式要求請重新輸入",Toast.LENGTH_LONG).show();
                }
                else{
                    // 創建新Thread執行資料更改
                    Thread InfoChangeThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 連接資料庫
                            client_socket cs = new client_socket();
                            ArrayList<String> userinfo_change = new ArrayList<String>();
                            userinfo_change.add("userinfo_change");
                            userinfo_change.add(Password);
                            userinfo_change.add(Email);
                            userinfo_change.add(Phone);
                            userinfo_change.add(uNo);

                            final ArrayList<ArrayList<String>> sql_result = cs.sql(userinfo_change);

                            // 切換回主Thread更新UI
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (Integer.parseInt(sql_result.get(0).get(0)) > 0) {
                                        Toast.makeText(getApplicationContext(), "更改成功", Toast.LENGTH_SHORT).show();
                                        toAnotherPage();
//                                        Intent intent = new Intent(info_change.this, home_page.class);
//                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "更改失敗", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                    InfoChangeThread.start();
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAnotherPage();
//                Intent intent=new Intent();
//                intent.setClass(info_change.this,home_page.class);
//                startActivity(intent);
            }
        });
    }
    static class getUserInfoTask extends AsyncTask<String,Integer,ArrayList<ArrayList<String>>> {
        @Override
        protected ArrayList<ArrayList<String>> doInBackground(String... params) {
            String uNo = params[0];

            // 連接資料庫
            client_socket cs = new client_socket();
            ArrayList<String> get_userinfo = new ArrayList<String>();
            get_userinfo.add("get_userinfo");
            get_userinfo.add(uNo);

            ArrayList<ArrayList<String>> sql_result = cs.sql(get_userinfo);
            return sql_result;
        }
        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> result) {
            if(!result.isEmpty()){
                uPwd = result.get(0).get(1);
                uEmail = result.get(0).get(2);
                uPhone = result.get(0).get(3);
                password.setText(uPwd);
                email.setText(uEmail);
                phone.setText(uPhone);
            }
        }
    }
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    private void toAnotherPage() {
        Intent loginIntent = new Intent(this, home_page.class);
        loginIntent.putExtra("selectedNavItem", "user");
        this.startActivity(loginIntent);
    }
}

