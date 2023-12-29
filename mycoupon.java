package com.example.roading;

import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class mycoupon extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("我的折價券");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.mycoupon);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new mycoupon_fragment()).commit();
    }
}

