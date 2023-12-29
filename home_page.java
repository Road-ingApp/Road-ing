package com.example.roading;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import com.chaquo.python.Python;
import com.chaquo.python.PyObject;
import com.chaquo.python.android.AndroidPlatform;
import java.net.HttpURLConnection;
import java.net.URL;

public class home_page extends AppCompatActivity {
    public String user_name, user_ID, node;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Road-ing路營");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.home_page);

        //初始化python環境
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
        Python python=Python.getInstance();

        MainActivity mainActivity = new MainActivity();
        user_ID = mainActivity.getuNo();

        // 為導覽列設置Listener
        BottomNavigationView bottomNav = findViewById(R.id.navigation);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new Fragment();

                switch (item.getItemId()) {
                    case R.id.home:
                        fragment = new home();
                        break;
                    case R.id.camp:
                        fragment = new camp();
                        break;
                    case R.id.map:
                        fragment = new map();
                        break;
                    case R.id.user:
                        fragment = new user_info();
                        break;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                return true;
            }
        });
        // 預設選擇第一個菜單項目
        // 獲取傳入的 Intent
        Intent receivedIntent = getIntent();
        String action = receivedIntent.getAction();
        String type = receivedIntent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                // 處理分享的文本資料
                String sharedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
                String[] newText = sharedText.split("：");
                PyObject pyObject = python.getModule("expandURL");
                PyObject resultObject =pyObject.callAttr("expand_short_url",newText[1]);
                node = (resultObject.toString());
                Thread UserMapThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 連接資料庫
                        client_socket cs = new client_socket();
                        ArrayList<String> get_userinfo = new ArrayList<String>();
                        get_userinfo.add("get_userinfo");
                        get_userinfo.add(user_ID);

                        final ArrayList<ArrayList<String>> sql_result = cs.sql(get_userinfo);
                        user_name = sql_result.get(0).get(0);

                        ArrayList<String> user_map = new ArrayList<String>();
                        user_map.add("user_map");
                        user_map.add(user_ID);
                        user_map.add(node);
                        user_map.add(newText[1]);
                        user_map.add(user_name + "的路線");

                        final ArrayList<ArrayList<String>> sql_result2 = cs.sql(user_map);

                        // 切換回主Thread更新UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Integer.parseInt(sql_result2.get(0).get(0)) > 0) {
                                    Toast.makeText(getApplicationContext(), "新增成功", Toast.LENGTH_SHORT).show();
                                    toAnotherPage();
                                } else {
                                    Toast.makeText(getApplicationContext(), "新增失敗", Toast.LENGTH_SHORT).show();
                                    bottomNav.setSelectedItemId(R.id.map);
                                }
                            }
                        });
                    }
                });

                UserMapThread.start();
            }
        }
        String selectedNavItem = getIntent().getStringExtra("selectedNavItem");
        if("user".equals(selectedNavItem)){
            bottomNav.setSelectedItemId(R.id.user);
        }
        else if("map".equals(selectedNavItem)){
            bottomNav.setSelectedItemId(R.id.map);
        }
        else{
            bottomNav.setSelectedItemId(R.id.home);
        }
    }
    private void toAnotherPage() {
        Intent loginIntent = new Intent(this, home_page.class);
        loginIntent.putExtra("selectedNavItem", "map");
        this.startActivity(loginIntent);
    }
}