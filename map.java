package com.example.roading;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
public class map extends Fragment {

    public ListView map_list;
    public mapAdapter adapter;
    public List<MapItemData> dataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map, container, false);

        map_list = view.findViewById(R.id.map_list);

        performMyRoute();
        return view;
    }

    private void performMyRoute() {
        MainActivity mainActivity = new MainActivity();
        new Thread(() -> {
            String uNo = mainActivity.getuNo();
            // 連接資料庫
            client_socket cs = new client_socket();
            ArrayList<String> get_routeInfo = new ArrayList<String>();
            get_routeInfo.add("get_routeInfo");
            get_routeInfo.add(uNo);
            final ArrayList<ArrayList<String>> sql_result = cs.sql(get_routeInfo);
            requireActivity().runOnUiThread(() -> updateUI(sql_result));
        }).start();
    }
    private void updateUI(ArrayList<ArrayList<String>> result) {
        //Context context = getActivity();
        dataList = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            MapItemData itemData = new MapItemData(Integer.parseInt(result.get(i).get(0)), result.get(i).get(1), result.get(i).get(2), result.get(i).get(3));
            dataList.add(itemData);
        }

        adapter = new mapAdapter(getActivity(), getChildFragmentManager(), (editRouteName.EditNameListener) getParentFragment(), dataList);
        map_list.setAdapter(adapter);
    }
}


