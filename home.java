package com.example.roading;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class home extends Fragment {
    private SimpleAdapter sa;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_fragment, container, false);

        ViewPager viewPager = view.findViewById(R.id.viewPager);
        ListView campListView = view.findViewById(R.id.campListView);
        Button southButton = view.findViewById(R.id.南部標籤);
        Button northButton = view.findViewById(R.id.北部標籤);
        Button midButton = view.findViewById(R.id.中部標籤);
        Button eastButton = view.findViewById(R.id.東部標籤);

        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.cabin_smu);
        images.add(R.drawable.bbq_smu);
        images.add(R.drawable.cloud_smu);

        ArrayList<ImageView> imagesList = new ArrayList<>();
        for(int i = 0;i < images.size();i++){
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(images.get(i));
            imagesList.add(iv);
        }

        LinkedList<HashMap<String, String>> data = new LinkedList<>();
        String[] from = {"name", "location", "phone","medal", "comment_total","rate"};
        int[] to = {R.id.camp_name, R.id.camp_location, R.id.phone, R.id.medal, R.id.comment_total,R.id.rate};
        final boolean[] southStatus = {false};
        final boolean[] northStatus = {false};
        final boolean[] midStatus = {false};
        final boolean[] eastStatus = {false};

        southButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                southStatus[0] = true;
                southButton.setBackgroundResource(R.drawable.button_shape);
                if(midStatus[0] || eastStatus[0] || northStatus[0]){
                    midStatus[0] = false;
                    midButton.setBackgroundResource(R.drawable.button_shape_unselect);

                    eastStatus[0] = false;
                    eastButton.setBackgroundResource(R.drawable.button_shape_unselect);

                    northStatus[0] = false;
                    northButton.setBackgroundResource(R.drawable.button_shape_unselect);
                }
                // 清空数据
                data.clear();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client_socket clientSocket = new client_socket();
                        ArrayList<String> getSouthCamp = new ArrayList<String>();
                        getSouthCamp.add("getSouthCamp");
                        final ArrayList<ArrayList<String>> queryResult = clientSocket.sql(getSouthCamp);

                        // 在UI线程中更新数据和通知适配器
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (queryResult != null) {

                                    // 清空数据
                                    data.clear();

                                    for (int i = 0; i < queryResult.size(); i++) {
                                        HashMap<String, String> hash_data = new HashMap<>();
                                        hash_data.put("name", queryResult.get(i).get(0) + " - " + queryResult.get(i).get(1));
                                        hash_data.put("location", queryResult.get(i).get(2));
                                        hash_data.put("phone", queryResult.get(i).get(3));
                                        hash_data.put("comment_total", queryResult.get(i).get(5));
                                        hash_data.put("rate", queryResult.get(i).get(7));
                                        switch (queryResult.get(i).get(4)) {
                                            case "1":
                                                hash_data.put("medal", String.valueOf(R.drawable.gold_medal));
                                                break;
                                            case "2":
                                                hash_data.put("medal", String.valueOf(R.drawable.silver_medal));
                                                break;
                                            case "3":
                                                hash_data.put("medal", String.valueOf(R.drawable.bronze_medal));
                                                break;
                                            case "4":
                                                hash_data.put("medal", String.valueOf(R.drawable.iron_medal));
                                                break;
                                        }
                                            data.add(hash_data);
                                    }

                                    // 通知适配器数据已更改
                                    sa.notifyDataSetChanged();
                                    SimpleAdapter sa = new SimpleAdapter(getActivity(), data, R.layout.homepage_listview, from, to);
                                    campListView.setAdapter(sa);
                                } else {
                                    // 输出错误信息
                                    Log.e("QueryResult", "Query failed or returned null result");
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        northButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                northStatus[0] = true;
                northButton.setBackgroundResource(R.drawable.button_shape);
                if(midStatus[0] || eastStatus[0] || southStatus[0]){
                    midStatus[0] = false;
                    midButton.setBackgroundResource(R.drawable.button_shape_unselect);

                    eastStatus[0] = false;
                    eastButton.setBackgroundResource(R.drawable.button_shape_unselect);

                    southStatus[0] = false;
                    southButton.setBackgroundResource(R.drawable.button_shape_unselect);
                }
                // 清空数据
                data.clear();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client_socket clientSocket = new client_socket();
                        ArrayList<String> getNorthCamp = new ArrayList<String>();
                        getNorthCamp.add("getNorthCamp");
                        final ArrayList<ArrayList<String>> queryResult = clientSocket.sql(getNorthCamp);

                        // 在UI线程中更新数据和通知适配器
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (queryResult != null) {

                                    // 清空数据
                                    data.clear();

                                    // 解析查询结果并添加到 data
                                    //int camp_list_size = (queryResult.size() < 10) ? queryResult.size() : 10;
                                    for (int i = 0; i < queryResult.size(); i++) {
                                        HashMap<String, String> hash_data = new HashMap<>();
                                        hash_data.put("name", queryResult.get(i).get(0) + " - " + queryResult.get(i).get(1));
                                        hash_data.put("location", queryResult.get(i).get(2));
                                        hash_data.put("phone", queryResult.get(i).get(3));
                                        hash_data.put("comment_total", queryResult.get(i).get(5));
                                        hash_data.put("rate", queryResult.get(i).get(7));
                                        switch (queryResult.get(i).get(4)) {
                                            case "1":
                                                hash_data.put("medal", String.valueOf(R.drawable.gold_medal));
                                                break;
                                            case "2":
                                                hash_data.put("medal", String.valueOf(R.drawable.silver_medal));
                                                break;
                                            case "3":
                                                hash_data.put("medal", String.valueOf(R.drawable.bronze_medal));
                                                break;
                                            case "4":
                                                hash_data.put("medal", String.valueOf(R.drawable.iron_medal));
                                                break;
                                        }
                                        data.add(hash_data);
                                    }

                                    // 通知适配器数据已更改
                                    sa.notifyDataSetChanged();
                                    SimpleAdapter sa = new SimpleAdapter(getActivity(), data, R.layout.homepage_listview, from, to);
                                    campListView.setAdapter(sa);

                                    campListView.setOnItemClickListener((parent, view1, position, id) -> {
                                        Intent intent = new Intent(getActivity(), camp_info.class);
                                        intent.putExtra("campID",Integer.parseInt(queryResult.get((int)id).get(6)));
                                        startActivity(intent);
                                    });
                                } else {
                                    // 输出错误信息
                                    Log.e("QueryResult", "Query failed or returned null result");
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        midButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                midStatus[0] = true;
                midButton.setBackgroundResource(R.drawable.button_shape);
                if(southStatus[0] || eastStatus[0] || northStatus[0]){
                    southStatus[0] = false;
                    southButton.setBackgroundResource(R.drawable.button_shape_unselect);

                    eastStatus[0] = false;
                    eastButton.setBackgroundResource(R.drawable.button_shape_unselect);

                    northStatus[0] = false;
                    northButton.setBackgroundResource(R.drawable.button_shape_unselect);
                }
                // 清空数据
                data.clear();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client_socket clientSocket = new client_socket();
                        ArrayList<String> getMidCamp = new ArrayList<String>();
                        getMidCamp.add("getMidCamp");
                        final ArrayList<ArrayList<String>> queryResult = clientSocket.sql(getMidCamp);

                        // 在UI线程中更新数据和通知适配器
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (queryResult != null) {

                                    // 清空数据
                                    data.clear();

                                    // 解析查询结果并添加到 data
                                    for (int i = 0; i < queryResult.size(); i++) {
                                        HashMap<String, String> hash_data = new HashMap<>();
                                        hash_data.put("name", queryResult.get(i).get(0) + " - " + queryResult.get(i).get(1));
                                        hash_data.put("location", queryResult.get(i).get(2));
                                        hash_data.put("phone", queryResult.get(i).get(3));
                                        hash_data.put("comment_total", queryResult.get(i).get(5));
                                        hash_data.put("rate", queryResult.get(i).get(7));
                                        switch (queryResult.get(i).get(4)) {
                                            case "1":
                                                hash_data.put("medal", String.valueOf(R.drawable.gold_medal));
                                                break;
                                            case "2":
                                                hash_data.put("medal", String.valueOf(R.drawable.silver_medal));
                                                break;
                                            case "3":
                                                hash_data.put("medal", String.valueOf(R.drawable.bronze_medal));
                                                break;
                                            case "4":
                                                hash_data.put("medal", String.valueOf(R.drawable.iron_medal));
                                                break;
                                        }
                                        data.add(hash_data);
                                    }

                                    // 通知适配器数据已更改
                                    sa.notifyDataSetChanged();
                                    SimpleAdapter sa = new SimpleAdapter(getActivity(), data, R.layout.homepage_listview, from, to);
                                    campListView.setAdapter(sa);

                                    campListView.setOnItemClickListener((parent, view1, position, id) -> {
                                        Intent intent = new Intent(getActivity(), camp_info.class);
                                        intent.putExtra("campID",Integer.parseInt(queryResult.get((int)id).get(6)));
                                        startActivity(intent);
                                    });
                                } else {
                                    // 输出错误信息
                                    Log.e("QueryResult", "Query failed or returned null result");
                                }
                            }
                        });
                    }
                }).start();
            }
        });
        eastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eastStatus[0] = true;
                eastButton.setBackgroundResource(R.drawable.button_shape);
                if(midStatus[0] || southStatus[0] || northStatus[0]){
                    midStatus[0] = false;
                    midButton.setBackgroundResource(R.drawable.button_shape_unselect);

                    southStatus[0] = false;
                    southButton.setBackgroundResource(R.drawable.button_shape_unselect);

                    northStatus[0] = false;
                    northButton.setBackgroundResource(R.drawable.button_shape_unselect);
                }
                // 清空数据
                data.clear();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client_socket clientSocket = new client_socket();
                        ArrayList<String> getEastCamp = new ArrayList<String>();
                        getEastCamp.add("getEastCamp");
                        final ArrayList<ArrayList<String>> queryResult = clientSocket.sql(getEastCamp);

                        // 在UI线程中更新数据和通知适配器
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (queryResult != null) {

                                    // 清空数据
                                    data.clear();

                                    // 解析查询结果并添加到 data
                                    for (int i = 0; i < queryResult.size(); i++) {
                                        HashMap<String, String> hash_data = new HashMap<>();
                                        hash_data.put("name", queryResult.get(i).get(0) + " - " + queryResult.get(i).get(1));
                                        hash_data.put("location", queryResult.get(i).get(2));
                                        hash_data.put("phone", queryResult.get(i).get(3));
                                        hash_data.put("comment_total", queryResult.get(i).get(5));
                                        hash_data.put("rate", queryResult.get(i).get(7));
                                        switch (queryResult.get(i).get(4)) {
                                            case "1":
                                                hash_data.put("medal", String.valueOf(R.drawable.gold_medal));
                                                break;
                                            case "2":
                                                hash_data.put("medal", String.valueOf(R.drawable.silver_medal));
                                                break;
                                            case "3":
                                                hash_data.put("medal", String.valueOf(R.drawable.bronze_medal));
                                                break;
                                            case "4":
                                                hash_data.put("medal", String.valueOf(R.drawable.iron_medal));
                                                break;
                                        }
                                        data.add(hash_data);
                                    }

                                    // 通知适配器数据已更改
                                    sa.notifyDataSetChanged();
                                    SimpleAdapter sa = new SimpleAdapter(getActivity(), data, R.layout.homepage_listview, from, to);
                                    campListView.setAdapter(sa);

                                    campListView.setOnItemClickListener((parent, view1, position, id) -> {
                                        Intent intent = new Intent(getActivity(), camp_info.class);
                                        intent.putExtra("campID",Integer.parseInt(queryResult.get((int)id).get(6)));
                                        startActivity(intent);
                                    });
                                } else {
                                    // 输出错误信息
                                    Log.e("QueryResult", "Query failed or returned null result");
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        campListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sqlcamp newFragment = new sqlcamp();

                FragmentManager fragmentManager = getParentFragmentManager(); // 在 Fragment 中

                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.replace(R.id.fragment_container, newFragment); // R.id.fragment_container 是用于放置 Fragment 的容器视图

                transaction.commit();
            }
        });
        northButton.setBackgroundResource(R.drawable.button_shape); // 将北部按钮状态设置为按下
        northStatus[0] = true; // 更新北部按钮的狀態

        northButton.performClick();

        ViewPagerAdapter adapter = new ViewPagerAdapter(requireContext(), imagesList, viewPager);
        viewPager.setAdapter(adapter);
        this.sa = new SimpleAdapter(getActivity(), data, R.layout.camp_item, from, to);
        campListView.setAdapter(sa);

        return view;
    }
}
