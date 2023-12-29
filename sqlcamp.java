package com.example.roading;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.roading.R;
import com.example.roading.camp_info;

import java.util.HashMap;
import java.util.LinkedList;

public class sqlcamp extends Fragment {
    public ListView camps;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camp, container, false);
        // 指派變數
        camps = view.findViewById(R.id.camp_list);
        LinkedList<HashMap<String,String>> data=new LinkedList<>();
        String[] from={"name","location","phone"};
        int[] to={R.id.camp_name,R.id.camp_location,R.id.phone};

        for (int i=0;i<1;i++){
            HashMap<String,String> hash_data=new HashMap<>();
            hash_data.put("name","露營樂2號店(狩獵帳)-旗津豪華露營區");
            hash_data.put("location","高雄市旗津區旗津三路51號");
            hash_data.put("phone","09-87757278");
            data.add(hash_data);
        }

        SimpleAdapter sa = new SimpleAdapter(getActivity(),data,R.layout.camp_item,from,to);
        camps.setAdapter(sa);

        camps.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(getActivity(), camp_info.class);
            startActivity(intent);
        });

        return view;
    }
//    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(requireActivity(),"您點選了:"+continent[position],Toast.LENGTH_SHORT).show();
//        }
//    };
}
