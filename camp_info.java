package com.example.roading;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class camp_info extends AppCompatActivity {
    private Button toMap, label1, label2, label3, offical_web;
    int campID;
    public Context context;
    TextView CampName,CampAddress,CampPhone,commentNum,score;
    ImageView sample1,sample2,sample3,sample4,sample5;
    RecyclerView camp_comments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.campinfo_final);

        // 獲取營區代號
        campID = getIntent().getIntExtra("campID",0);

        // 獲取內容標籤
        CampName = findViewById(R.id.CampName); //營區名稱
        CampAddress = findViewById(R.id.CampAddress); //地址
        CampPhone = findViewById(R.id.CampPhone); //電話
        commentNum = findViewById(R.id.commentNum); //評論數
        score = findViewById(R.id.score); //平均評分
        camp_comments = findViewById(R.id.comment_list); //評論清單
        sample1 = findViewById(R.id.sample1);
        sample2 = findViewById(R.id.sample2);
        sample3 = findViewById(R.id.sample3);
        sample4 = findViewById(R.id.sample4);
        sample5 = findViewById(R.id.sample5);

        toMap=(Button)findViewById(R.id.toMap);
        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = (String) CampName.getText(); // 用您要跳转的地名替换这里

                // 創建 Intent
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps"); // 指定使用 Google Maps 應用
                startActivity(mapIntent);
            }
        });

        label1=(Button)findViewById(R.id.label1);

        label2=(Button)findViewById(R.id.label2);

        label3=(Button)findViewById(R.id.label3);


        performDatabaseSearch();
    }

    public void performDatabaseSearch() {
        new Thread(() -> {
            // 連接資料庫
            client_socket cs = new client_socket();
            ArrayList<String> user_search = new ArrayList<String>();
            // 設定為進入營區詳細資料
            user_search.add("camp_info");
            // 設定查詢目標營區ID
            user_search.add(String.valueOf(campID));

            ArrayList<ArrayList<String>> sql_result = cs.sql(user_search);

            runOnUiThread(() -> updateUI_info(sql_result));
        }).start();
        new Thread(() -> {
            // 連接資料庫
            client_socket cs = new client_socket();
            ArrayList<String> user_search = new ArrayList<String>();
            // 設定為進入營區詳細資料
            user_search.add("camp_comment");
            // 設定查詢目標營區ID
            user_search.add(String.valueOf(campID));

            ArrayList<ArrayList<String>> sql_result = cs.sql(user_search);

            runOnUiThread(() -> updateUI_comment(sql_result));
        }).start();

        // 標籤
        new Thread(() -> {
            // 連接資料庫
            client_socket cs = new client_socket();
            ArrayList<String> user_search = new ArrayList<String>();
            // 設定為進入營區詳細資料
            user_search.add("camp_info_key");
            // 設定查詢目標營區ID
            user_search.add(String.valueOf(campID));

            ArrayList<ArrayList<String>> sql_result = cs.sql(user_search);

            runOnUiThread(() -> updateUI_key(sql_result));
        }).start();
    }
    @SuppressLint("SetTextI18n")
    protected void updateUI_info(ArrayList<ArrayList<String>> result) {
        CampName.setText(result.get(0).get(0));
        CampAddress.setText(result.get(0).get(2));
        CampPhone.setText(result.get(0).get(3));
        score.setText(score.getText() + result.get(0).get(7));
        commentNum.setText("(來自" + result.get(0).get(5) + "則留言)");

        ArrayList<ImageView> imgs = new ArrayList<ImageView>();
        imgs.add(sample1);
        imgs.add(sample2);
        imgs.add(sample3);
        imgs.add(sample4);
        imgs.add(sample5);
        for(int i=0;i<5;i++){
            int id = getResources().getIdentifier("c"+campID+"_"+String.valueOf(i+1), "drawable", getPackageName());
            if(id!=0){
                imgs.get(i).setImageResource(id);
                imgs.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //camp_info campFragment = new camp_info();
                        Bundle args = new Bundle();
                        args.putInt("imageResId", id);
                        //campFragment.setArguments(args);
                        // 創建DialogFragment實例
                        mapImage fragment = new mapImage();

                        // 使用FragmentManager
                        FragmentManager fragmentManager = getSupportFragmentManager();

                        fragment.setArguments(args);
                        // 顯示對話框
                        fragment.show(fragmentManager, "dialog");
                    }
                });
            }else{
                ViewGroup.LayoutParams params = imgs.get(i).getLayoutParams();
                params.width = 0;
                imgs.get(i).setLayoutParams(params);
            }
        }
        // 前往官方網站
        String url = result.get(0).get(8);
        offical_web = (Button)findViewById(R.id.offical_web);
        offical_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setPackage("com.android.chrome"); // 指定 Chrome 瀏覽器的包名
                startActivity(intent);
            }
        });
        if(url.equals("無")){
            offical_web.setVisibility(View.INVISIBLE);
        }
    }
    protected void updateUI_comment(ArrayList<ArrayList<String>> result) {
        RecyclerView recyclerView = findViewById(R.id.comment_list);
        comment_adapter ca = new comment_adapter(result);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(ca);
    }
    protected void updateUI_key(ArrayList<ArrayList<String>> result) {
        label1.setText(result.get(0).get(0));
        label2.setText(result.get(1).get(0));
        label3.setText(result.get(2).get(0));
    }
    // 要記住，程式沒完成=彩蛋
    // 動態載入評論
    public static class comment_adapter extends RecyclerView.Adapter<comment_adapter.commentViewHolder>{
        ArrayList<ArrayList<String>> comment_list;
        public comment_adapter(ArrayList<ArrayList<String>> comment_list){
            this.comment_list = comment_list;
        }
        @Override
        public commentViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item, viewGroup, false);
            return new commentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull commentViewHolder holder, int position) {
            holder.user.setText(comment_list.get(position).get(0));
            switch (comment_list.get(position).get(1)){
                case "1":
                    holder.star.setImageResource(R.drawable.one_score_removebg_preview);
                    break;
                case "2":
                    holder.star.setImageResource(R.drawable.two_score_removebg_preview);
                    break;
                case "3":
                    holder.star.setImageResource(R.drawable.three_score_removebg_preview);
                    break;
                case "4":
                    holder.star.setImageResource(R.drawable.four_score_removebg_preview);
                    break;
                case "5":
                    holder.star.setImageResource(R.drawable.five_score_removebg_preview);
                    break;
            }
            holder.time.setText(comment_list.get(position).get(2));
            holder.comment.setText(comment_list.get(position).get(3));
        }

        @Override
        public int getItemCount() {
            return comment_list.size();
        }

        public static class commentViewHolder extends RecyclerView.ViewHolder {
            TextView user,time,comment;
            ImageView star;
            public commentViewHolder(View view){
                super(view);
                user = view.findViewById(R.id.user);
                star = view.findViewById(R.id.star);
                time = view.findViewById(R.id.time);
                comment = view.findViewById(R.id.comment_text);
            }
        }
    }
}