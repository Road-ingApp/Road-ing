package com.example.roading;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class routeDialog extends BottomSheetDialogFragment {

    public TextView Node;
    public ArrayList<String> nodeName = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.route, container, false);
        Node = view.findViewById(R.id.node);

        Bundle args = getArguments();
        if (args != null) {
            String nodeText = args.getString("node_name", "");
            nodeName.addAll(Arrays.asList(nodeText.split("_")));
        }
        for(int i = 0; i < nodeName.size(); i++){
            if(i==0){
                Node.append((i+1) + ". " + nodeName.get(i));
            }else {
                Node.append("\n\n" + (i + 1) + ". " + nodeName.get(i));
            }
        }
        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);
    }
}
