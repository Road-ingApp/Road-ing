package com.example.roading;


import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button login, noAccount;
    private CheckBox rememberMe;
    public static String uNo, userName, uPwd, uEmail, uPhone;
    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        setTitle("Road-ing路營");

        EditText EditTextname = (EditText) findViewById(R.id.userNameinput);
        EditText EditTextpassword = (EditText) findViewById(R.id.userPswinput);
        rememberMe = findViewById(R.id.rememberMe);
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        boolean isRemembered = sharedPreferences.getBoolean("isRemembered", false);

        if (isRemembered) {
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");
            EditTextname.setText(savedUsername);
            EditTextpassword.setText(savedPassword);
            rememberMe.setChecked(true);
        }

        login=(Button)findViewById(R.id.register_btn);
        noAccount=(Button)findViewById(R.id.noAccount_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = EditTextname.getText().toString();
                String password = EditTextpassword.getText().toString();

                // 創建新Thread執行登錄
                Thread loginThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 連接資料庫
                        client_socket cs = new client_socket();
                        ArrayList<String> user_login = new ArrayList<String>();
                        user_login.add("user_login");
                        user_login.add(username);
                        user_login.add(password);
                        ArrayList<ArrayList<String>> sql_result = cs.sql(user_login);

                        // 切換回主Thread更新UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!sql_result.isEmpty()) {
                                    uNo = sql_result.get(0).get(0);
                                    userName = sql_result.get(0).get(1);
                                    uPwd = sql_result.get(0).get(2);
                                    uEmail = sql_result.get(0).get(3);
                                    uPhone = sql_result.get(0).get(4);
                                    boolean isRememberMe = rememberMe.isChecked();
                                    if(isRememberMe){
                                        Toast.makeText(getApplicationContext(), "登入成功", Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("username", username);
                                        editor.putString("password", password);
                                        editor.putBoolean("isRemembered", true);
                                        editor.apply();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "登入成功", Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.remove("username");
                                        editor.remove("password");
                                        editor.putBoolean("isRemembered", false);
                                        editor.apply();
                                    }
                                    Intent intent = new Intent(MainActivity.this, home_page.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "帳號或密碼錯誤", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                loginThread.start();
            }
        });

        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,Register.class);
                startActivity(intent);;
            }
        });
    }

    public String getuNo(){
        return uNo;
    }
}