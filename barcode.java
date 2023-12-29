package com.example.roading;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

public class barcode extends DialogFragment {
    public View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.barcode, container, false);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                toAnotherPage();
            }
        });
    }

    private void toAnotherPage() {
        Intent intent = new Intent(getContext(), home_page.class);
        intent.putExtra("selectedNavItem", "user");
        getContext().startActivity(intent);
    }
}
