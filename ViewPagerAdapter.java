package com.example.roading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    private final Context context;
    private final ArrayList<ImageView> imagesList;
    private ArrayList<Integer> images;
    private final ViewPager viewPager;
    private static final long AUTO_SCROLL_DELAY = 3000;
    private final Handler autoScrollHandler;
    private final Runnable autoScrollRunnable;
    private static final int MAX_VALUE = 10000; // 一個足夠大的值，用於實現循環

    public ViewPagerAdapter(Context context, ArrayList<ImageView> imagesList, ViewPager viewPager) {
        this.context = context;
        this.imagesList = imagesList;
        this.viewPager = viewPager;

        autoScrollHandler = new Handler();
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                // 自動切換到下一個頁面
                int nextPosition = getItem();
                if (nextPosition > 0 && nextPosition < imagesList.size()) {
                    viewPager.setCurrentItem(nextPosition, true);
                }else if(nextPosition == 0){
                    viewPager.setCurrentItem(0, false);
                }

                // 重複執行自動切換
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY);
            }
        };
    }

    public void startAutoScroll() {
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
    }

    public void stopAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }

    private int getItem() {
        int currentPosition = viewPager.getCurrentItem();
        return (currentPosition + 1) % imagesList.size();
    }

    @Override
    public int getCount() {
        // 返回一個足夠大的值，實現循環
        return MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // 使用 position 取餘數確保循環
        images = new ArrayList<>();
        images.add(R.drawable.cabin_smu);
        images.add(R.drawable.bbq_smu);
        images.add(R.drawable.cloud_smu);
        int adjustedPosition = position % imagesList.size();
        ImageView imageView = imagesList.get(adjustedPosition);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // 如果 ImageView 已經有 parent，則將它從 parent 移除
        ViewGroup parent = (ViewGroup) imageView.getParent();
        if (parent != null) {
            parent.removeView(imageView);
        }

        // 添加點擊事件
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 根據不同的圖片索引執行不同的跳轉操作
                switch (adjustedPosition) {
                    case 0:
                        camp_AD campAdFragment = new camp_AD();
                        Bundle args = new Bundle();
                        args.putInt("imageResId", images.get(adjustedPosition));
                        args.putString("keyword", "小木屋");
                        campAdFragment.setArguments(args);

                        // 获取FragmentManager
                        FragmentManager fragmentManager = ((home_page) context).getSupportFragmentManager();

                        // 开始Fragment事务
                        FragmentTransaction transaction = fragmentManager.beginTransaction();

                        // 设置自定义动画
                        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);

                        // 替换当前Fragment为camp_AD
                        transaction.replace(R.id.fragment_container, campAdFragment);

                        // 添加事务到返回栈，如果需要的话
                        transaction.addToBackStack(null);

                        // 提交事务
                        transaction.commit();
                        break;
                    case 1:
                        camp_AD campAdFragment2 = new camp_AD();
                        Bundle args2 = new Bundle();
                        args2.putInt("imageResId", images.get(adjustedPosition));
                        args2.putString("keyword", "烤肉");
                        campAdFragment2.setArguments(args2);

                        // 获取FragmentManager
                        FragmentManager fragmentManager2 = ((home_page) context).getSupportFragmentManager();

                        // 开始Fragment事务
                        FragmentTransaction transaction2 = fragmentManager2.beginTransaction();

                        // 设置自定义动画
                        transaction2.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);

                        // 替换当前Fragment为camp_AD
                        transaction2.replace(R.id.fragment_container, campAdFragment2);

                        // 添加事务到返回栈，如果需要的话
                        transaction2.addToBackStack(null);

                        // 提交事务
                        transaction2.commit();
                        break;
                    case 2:
                        camp_AD campAdFragment3 = new camp_AD();
                        Bundle args3 = new Bundle();
                        args3.putInt("imageResId", images.get(adjustedPosition));
                        args3.putString("keyword", "雲海");
                        campAdFragment3.setArguments(args3);

                        // 获取FragmentManager
                        FragmentManager fragmentManager3 = ((home_page) context).getSupportFragmentManager();

                        // 开始Fragment事务
                        FragmentTransaction transaction3 = fragmentManager3.beginTransaction();

                        // 设置自定义动画
                        transaction3.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);

                        // 替换当前Fragment为camp_AD
                        transaction3.replace(R.id.fragment_container, campAdFragment3);

                        // 添加事务到返回栈，如果需要的话
                        transaction3.addToBackStack(null);

                        // 提交事务
                        transaction3.commit();
                        break;
                }
            }
        });

        container.addView(imageView);
        if (position == 0) {
            startAutoScroll();
        }
        return imageView;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        stopAutoScroll();
    }
}
