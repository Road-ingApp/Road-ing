package com.example.roading;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

public class Register extends AppCompatActivity{
    Button register, tologin;
    CheckBox ruleconfirm;
    TextView ruleconfirmTextView;
    TextInputEditText username, password, email, phone;
    boolean usedName = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.registerpage);

        register = findViewById(R.id.register_btn);
        tologin = findViewById(R.id.tologin_btn);
        username = findViewById(R.id.userNameinput);
        password = findViewById(R.id.userPswinput);
        email = findViewById(R.id.userMailinput);
        phone = findViewById(R.id.userPhoneinput);
        ruleconfirm = findViewById(R.id.ruleconfirm);
        ruleconfirmTextView = findViewById(R.id.rulehyperlink);

        ruleconfirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 顯示自定義的 DialogFragment
                user_rule dialog = new user_rule();
                FragmentManager fragmentManager = getSupportFragmentManager();
                dialog.show(fragmentManager, "dialog");
                Toast.makeText(getApplicationContext(),"點擊框框外任意處關閉",Toast.LENGTH_LONG).show();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Username = username.getText().toString();
                String Password = password.getText().toString();
                String Email = email.getText().toString();
                String Phone = phone.getText().toString();
                boolean isRuleConfirm = ruleconfirm.isChecked();

                Thread nameThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client_socket cs = new client_socket();
                        ArrayList<String> getAllUsername = new ArrayList<String>();
                        getAllUsername.add("getAllUsername");
                        ArrayList<ArrayList<String>> sql_result = cs.sql(getAllUsername);
                        for (int i = 0; i < sql_result.size(); i++) {
                            if (Username.equals(sql_result.get(i).get(0))) {
                                usedName = true;
                                break;
                            }
                        }
                    }
                });
                nameThread.start();

                if (!isRuleConfirm){
                    Toast.makeText(getApplicationContext(),"您未同意使用者條款",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(Username.length() < 2 || Password.length() < 2 ){
                    Toast.makeText(getApplicationContext(),"使用者名稱和密碼需大於2個字，請重新輸入",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!isValidEmailAddress(Email)){
                    Toast.makeText(getApplicationContext(),"Email不符合格式要求請重新輸入",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (usedName){
                    Toast.makeText(getApplicationContext(), "該使用者名稱已被使用", Toast.LENGTH_SHORT).show();
                }
                else {
                    Thread registerThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 連接資料庫
                            client_socket cs = new client_socket();
                            ArrayList<String> user_register = new ArrayList<String>();
                            user_register.add("user_register");
                            user_register.add(Username);
                            user_register.add(Password);
                            user_register.add(Email);
                            user_register.add(Phone);
                            user_register.add("0");

                            final ArrayList<ArrayList<String>> sql_result = cs.sql(user_register);

                            // 切换回主线程更新UI
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!sql_result.isEmpty()) {
                                        Toast.makeText(getApplicationContext(), "註冊成功", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Register.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "註冊失敗", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                    registerThread.start();
                }
            }
        });

        tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(Register.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}

