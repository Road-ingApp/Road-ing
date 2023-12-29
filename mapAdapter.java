package com.example.roading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class mapAdapter extends BaseAdapter implements editRouteName.EditNameListener{
    private static List<MapItemData> dataList;
    private final Context context;
    @SuppressLint("StaticFieldLeak")
    private static ViewHolder holder;
    public String user_ID;
    public int map_ID;
    public FragmentManager fragmentManager;
    public editRouteName.EditNameListener editNameListener;

    public mapAdapter(Context context, FragmentManager fragmentManager, editRouteName.EditNameListener editNameListener, List<MapItemData> dataList) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.editNameListener = editNameListener;
        mapAdapter.dataList = dataList;
        this.user_ID = new MainActivity().getuNo();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.map_item, parent, false);
            holder = new ViewHolder();
            holder.preview = convertView.findViewById(R.id.preview);
            holder.trashcan = convertView.findViewById(R.id.trashcan);
            holder.routeNameTextView = convertView.findViewById(R.id.route_name);
            holder.firstNodeTextView = convertView.findViewById(R.id.first_node);
            holder.lastNodeTextView = convertView.findViewById(R.id.last_node);
            holder.EditRouteName = convertView.findViewById(R.id.editName);
            holder.EditRouteName.setTag(position);
            holder.routeNameTextView.setTag(position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //final MapItemData itemData = dataList.get(position);
        //map_ID = itemData.getMap_ID();
        //url = dataList.get(position).getUrl();
        //node = itemData.getNode();
        //route_name = itemData.getRoute();

        ArrayList<String> allNode = new ArrayList<>();

        Collections.addAll(allNode, dataList.get(position).getNode().split("_"));

        for(int i = 0; i < allNode.size(); i++){
            if (i == 0) {
                holder.firstNodeTextView.setText(allNode.get(i)); ;
            }else if (i == allNode.size() - 1){
                holder.lastNodeTextView.setText(allNode.get(i));
            }
        }

        holder.routeNameTextView.setText(dataList.get(position).getRoute());
        holder.preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(dataList.get(position).getUrl());
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("node_name", dataList.get(position).getNode());
                intent.putExtra("url", dataList.get(position).getUrl());
                context.startActivity(intent);
            }
        });

        holder.trashcan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = new MainActivity();
                // 創建新Thread執行資料更改
                Thread UseCouponThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 連接資料庫
                        client_socket cs = new client_socket();
                        ArrayList<String> delete_route = new ArrayList<String>();
                        delete_route.add("delete_route");
                        delete_route.add(user_ID);
                        delete_route.add(dataList.get(position).getUrl());
                        delete_route.add(dataList.get(position).getRoute());

                        final ArrayList<ArrayList<String>> sql_result = cs.sql(delete_route);

                        //切換回主Thread更新UI
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (Integer.parseInt(sql_result.get(0).get(0)) > 0) {
                                    Toast.makeText(context, "刪除成功", Toast.LENGTH_SHORT).show();
                                    toAnotherPage();
                                } else {
                                    Toast.makeText(context, "刪除失敗", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                UseCouponThread.start();
            }
        });
        holder.EditRouteName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.position = position;
                editRouteName edit = new editRouteName();
                edit.setEditNameListener(mapAdapter.this);
                edit.show(fragmentManager, "EditName");
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeDialog dialog = new routeDialog();
                Bundle args = new Bundle();
                args.putString("node_name", dataList.get(position).getNode());
                dialog.setArguments(args);
                dialog.show(fragmentManager, dialog.getTag());

            }
        });
        return convertView;
    }

    private void toAnotherPage() {
        Intent intent = new Intent(context, home_page.class);
        intent.putExtra("selectedNavItem", "map");
        context.startActivity(intent);
    }
    @Override
    public void onEditNameComplete(String inputName) {
        int position = holder.position;
        EditName(inputName, position);
    }
    @Override
    public void EditName(String inputName, int position) {
        // 創建新Thread執行資料更改
        Thread RouteNameChangeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 連接資料庫
                client_socket cs = new client_socket();
                ArrayList<String> routeName_change = new ArrayList<String>();
                routeName_change.add("routeName_change");
                routeName_change.add(inputName);
                routeName_change.add(user_ID);
                routeName_change.add(Integer.toString(dataList.get(position).getMap_ID()));

                final ArrayList<ArrayList<String>> sql_result = cs.sql(routeName_change);
                // 切換回主Thread更新UI
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (Integer.parseInt(sql_result.get(0).get(0)) > 0) {
                            Toast.makeText(context, "更改成功", Toast.LENGTH_SHORT).show();
                            toAnotherPage();
                        } else {
                            Toast.makeText(context, "更改失敗", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        RouteNameChangeThread.start();
    }
    class ViewHolder {
        Button preview;
        Button trashcan;
        TextView firstNodeTextView;
        TextView lastNodeTextView;
        TextView routeNameTextView;
        ImageButton EditRouteName;
        int position;
    }
}