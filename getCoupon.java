package com.example.roading;

import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class getCoupon extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("獲取折價券");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.getcoupon);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new getCoupon_fragment()).commit();
    }
}