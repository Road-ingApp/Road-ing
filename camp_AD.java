package com.example.roading;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class camp_AD extends Fragment {
    private SimpleAdapter sa;
    private LinkedList<HashMap<String, String>> data;
    private final String[] from = {"name", "location", "phone", "medal", "comment_total", "rate"};
    private final int[] to = {R.id.camp_name, R.id.camp_location, R.id.phone, R.id.medal, R.id.comment_total, R.id.rate};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camp_ad, container, false);
        data = new LinkedList<>();
        ListView campListView = view.findViewById(R.id.campListView);

        sa = new SimpleAdapter(requireContext(), data, R.layout.homepage_listview, from, to);
        campListView.setAdapter(sa);

        Bundle args = getArguments();
        assert args != null;
        int imageResId = args.getInt("imageResId");
        String keyword = args.getString("keyword");

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(imageResId);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        new Thread(new Runnable() {
            @Override
            public void run() {
                client_socket clientSocket = new client_socket();
                ArrayList<String> keyword_select = new ArrayList<String>();
                keyword_select.add("keyword_select");
                keyword_select.add(keyword);
                final ArrayList<ArrayList<String>> queryResult = clientSocket.sql(keyword_select);

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (queryResult != null) {
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
                            sa.notifyDataSetChanged();
                        } else {
                            Log.e("QueryResult", "Query failed or returned null result");
                        }
                        campListView.setOnItemClickListener((parent, view1, position, id) -> {
                            Intent intent = new Intent(getActivity(), camp_info.class);
                            intent.putExtra("campID",Integer.parseInt(queryResult.get((int)id).get(6)));
                            startActivity(intent);
                        });
                    }
                });
            }
        }).start();

        return view;
    }
}
