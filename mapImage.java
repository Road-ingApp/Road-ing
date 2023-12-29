package com.example.roading;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

public class mapImage extends DialogFragment {
    public View view;
    public ImageView image;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mapimage, container, false);

        Bundle args = getArguments();
        assert args != null;
        int imageResId = args.getInt("imageResId");

        image = view.findViewById(R.id.imageview);
        image.setImageResource(imageResId);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
    }

//    private void toAnotherPage() {
//        Intent intent = new Intent(getContext(), home_page.class);
//        intent.putExtra("selectedNavItem", "user");
//        getContext().startActivity(intent);
//    }
}
