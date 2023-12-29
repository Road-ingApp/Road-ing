package com.example.roading;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class camp extends Fragment {
    public ListView camps;
    public EditText camp_search;
    public Button searchKey_all,searchKey_north,searchKey_middle,searchKey_south,searchKey_east,more_searchKey;
    public String user_search_region;
    public ArrayList<String> advanced_search_key;
    public ArrayList<Button> region_list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camp, container, false);

        // 進階搜尋視窗初始化
        AdvancedSearchFragment advancedSearchFragment = new AdvancedSearchFragment();
        advancedSearchFragment.getInstance(this);
        // 指派變數
        camps = view.findViewById(R.id.camp_list);

        // 搜尋初始化
        user_search_region = "全台";
        advanced_search_key = new ArrayList<>();

        // 搜尋框動作
        camp_search = view.findViewById(R.id.camp_search);
        camp_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND) {
                    Toast.makeText(requireActivity(),"搜尋中", Toast.LENGTH_SHORT).show();
                    performDatabaseSearch();
                    return true;
                }
                return false;
            }
        });

        // 簡易地區搜尋
        searchKey_all = view.findViewById(R.id.searchKey_all);
        searchKey_north = view.findViewById(R.id.searchKey_north);
        searchKey_middle = view.findViewById(R.id.searchKey_middle);
        searchKey_south = view.findViewById(R.id.searchKey_south);
        searchKey_east = view.findViewById(R.id.searchKey_east);
        region_list = new ArrayList<>();
        region_list.add(searchKey_all);
        region_list.add(searchKey_north);
        region_list.add(searchKey_middle);
        region_list.add(searchKey_south);
        region_list.add(searchKey_east);

        for(Button region_key:region_list){
            region_key.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    for(Button change_key:region_list){
                        change_key.setBackgroundResource(R.drawable.button_shape_unselect);
                    }
                    region_key.setBackgroundResource(R.drawable.button_shape);
                    user_search_region = region_key.getText().toString();
                    performDatabaseSearch();
                }
            });
        }

        // 進階搜尋
        more_searchKey = view.findViewById(R.id.more_searchKey);

        more_searchKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advancedSearchFragment.show(getChildFragmentManager(), advancedSearchFragment.getTag());
            }
        });

        // 營區項目初始化
        change_region_search(user_search_region);
        performDatabaseSearch();
        return view;
    }

    public void performDatabaseSearch() {
        new Thread(() -> {
            // 連接資料庫
            client_socket cs = new client_socket();
            ArrayList<String> user_search = new ArrayList<String>();
            user_search.add("camp_search");
            if (!camp_search.getText().toString().equals("")) {
                user_search.add(camp_search.getText().toString());
            } else {
                user_search.add("");
            }
            user_search.add(user_search_region);
            user_search.add(String.join(",",advanced_search_key));
            ArrayList<ArrayList<String>> sql_result = cs.sql(user_search);

            requireActivity().runOnUiThread(() -> updateUI(sql_result));
        }).start();
    }

    protected void updateUI(ArrayList<ArrayList<String>> result) {
        // 指派變數
        LinkedList<HashMap<String,String>> data=new LinkedList<>();
        String[] from={"name","location","phone","medal","comment_total","sample_camp1","sample_camp2"};
        int[] to={R.id.camp_name,R.id.camp_location,R.id.phone,
                R.id.medal,R.id.comment_total,R.id.sample_camp1,R.id.sample_camp2};

        // 獲取營區圖片


        // 資料庫內容測試
        for (int i=0;i<result.size();i++){
            HashMap<String,String> hash_data = new HashMap<>();
            hash_data.put("name",result.get(i).get(1) + " - " + result.get(i).get(0));
            hash_data.put("location",result.get(i).get(2));
            hash_data.put("phone",result.get(i).get(3));
            hash_data.put("comment_total",result.get(i).get(5));

            int drawableResourceId = getResources().getIdentifier("c"+result.get(i).get(6)+"_1", "drawable", getActivity().getPackageName());
            hash_data.put("sample_camp1", String.valueOf(drawableResourceId));
            drawableResourceId = getResources().getIdentifier("c"+result.get(i).get(6)+"_2", "drawable", getActivity().getPackageName());
            hash_data.put("sample_camp2", String.valueOf(drawableResourceId));

            switch (result.get(i).get(4)){
                case "1":
                    hash_data.put("medal",String.valueOf(R.drawable.gold_medal));
                    break;
                case "2":
                    hash_data.put("medal",String.valueOf(R.drawable.silver_medal));
                    break;
                case "3":
                    hash_data.put("medal",String.valueOf(R.drawable.bronze_medal));
                    break;
                case "4":
                    hash_data.put("medal",String.valueOf(R.drawable.iron_medal));
                    break;
            }

            data.add(hash_data);
        }
        SimpleAdapter sa = new SimpleAdapter(getActivity(),data,R.layout.camp_item,from,to);
        camps.setAdapter(sa);

        camps.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(getActivity(), camp_info.class);
            intent.putExtra("campID",Integer.parseInt(result.get((int)id).get(6)));
            startActivity(intent);
        });
    }

    public void set_advanced_key(ArrayList<String> advanced_search_key){
        this.advanced_search_key = advanced_search_key;
    }
    public ArrayList<String> get_advanced_key(){
        return advanced_search_key;
    }
    public void change_region_search(String search_region){
        for(Button change_key:region_list){
            if(change_key.getText().equals(search_region)){
                change_key.setBackgroundResource(R.drawable.button_shape);
            }else{
                change_key.setBackgroundResource(R.drawable.button_shape_unselect);
            }
        }
    }
    public void set_user_search_region(String user_search_region){
        this.user_search_region = user_search_region;
    }
    public String get_user_search_region(){
        return user_search_region;
    }


    // 營區詳細標籤
    public static class AdvancedSearchFragment extends BottomSheetDialogFragment {
        public camp origin_camp;
        public String search_region;
        public ArrayList<String> advanced_keys;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View advanced_view = inflater.inflate(R.layout.advanced_search, container, false);

            // 地區標籤列表
            int[] region_keys_id = {
                    R.id.searchKey_all, R.id.searchKey_north, R.id.searchKey_middle, R.id.searchKey_south,R.id.searchKey_east,
            };
            ArrayList<Button> region_keys = new ArrayList<>();
            for(int region_key_id:region_keys_id){
                region_keys.add(advanced_view.findViewById(region_key_id));
            }

            // 詳細標籤列表
            int[] search_keys_id = {
                    R.id.searchKey_advanced_1, R.id.searchKey_advanced_2, R.id.searchKey_advanced_3,
                    R.id.searchKey_advanced_4, R.id.searchKey_advanced_5, R.id.searchKey_advanced_6,
                    R.id.searchKey_advanced_7, R.id.searchKey_advanced_8, R.id.searchKey_advanced_9,
                    R.id.searchKey_advanced_10, R.id.searchKey_advanced_11, R.id.searchKey_advanced_12
            };
            ArrayList<Button> search_keys = new ArrayList<>();
            for(int search_key_id:search_keys_id){
                search_keys.add(advanced_view.findViewById(search_key_id));
            }

            // 連接詳細標籤
            new Thread(() -> {
                // 連接資料庫
                client_socket cs = new client_socket();
                ArrayList<String> search_key = new ArrayList<>();
                search_key.add("camp_key");
                ArrayList<ArrayList<String>> sql_result = cs.sql(search_key);

                requireActivity().runOnUiThread(() -> updateKey(sql_result,search_keys));
            }).start();

            // 詳細標籤按鈕監聽
            for(Button search_key:search_keys){
                search_key.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isSelect = false;
                        for(String advanced_key:advanced_keys){
                            if(advanced_key.equals(search_key.getText())){
                                isSelect = true;
                            }
                        }
                        if(!isSelect){
                            search_key.setBackgroundResource(R.drawable.button_shape);
                            advanced_keys.add(String.valueOf(search_key.getText()));
                        }else{
                            search_key.setBackgroundResource(R.drawable.button_shape_unselect);
                            advanced_keys.remove(search_key.getText());
                        }
                        origin_camp.set_advanced_key(advanced_keys);
                    }
                });
            }

            // 地區標籤初始化
            search_region = origin_camp.get_user_search_region();
            for(Button region_key:region_keys){
                if(region_key.getText().equals(search_region)){
                    region_key.setBackgroundResource(R.drawable.button_shape);
                }else{
                    region_key.setBackgroundResource(R.drawable.button_shape_unselect);
                }
            }

            // 地區標籤按鈕監聽
            for(Button region_key:region_keys){
                region_key.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(Button region_key:region_keys){
                            region_key.setBackgroundResource(R.drawable.button_shape_unselect);
                        }

                        region_key.setBackgroundResource(R.drawable.button_shape);
                        search_region = String.valueOf(region_key.getText());

                        origin_camp.set_user_search_region(search_region);
                    }
                });
            }

            return advanced_view;
        }
        @Override
        public void onDismiss(DialogInterface dialogInterface){
            super.onDismiss(dialogInterface);
            origin_camp.change_region_search(search_region);
            origin_camp.performDatabaseSearch();
        }

        // 獲得原始視窗
        public void getInstance(camp c){
            origin_camp = c;
        }
        // 更新標籤內容
        protected void updateKey(ArrayList<ArrayList<String>> result, ArrayList<Button> search_keys) {
            for(int i=0;i<12;i++){
                search_keys.get(i).setText(result.get(i).get(0));
            }

            // 詳細標籤初始化
            advanced_keys = origin_camp.get_advanced_key();
            for(Button search_key:search_keys){
                boolean isSelect = false;
                for(String ad_key:advanced_keys){
                    if(search_key.getText().equals(ad_key)){
                        isSelect = true;
                    }
                }
                if(isSelect){
                    search_key.setBackgroundResource(R.drawable.button_shape);
                }else{
                    search_key.setBackgroundResource(R.drawable.button_shape_unselect);
                }
            }
        }
    }
}