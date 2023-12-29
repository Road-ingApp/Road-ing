package com.example.roading;

import android.app.AlertDialog;
import android.app.Dialog;
import androidx.fragment.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class editRouteName extends DialogFragment{
    private EditText inputName;
    public EditNameListener listener;
    public interface EditNameListener{
        void onEditNameComplete(String inputname);

        void EditName(String inputname, int position);
    }
    public void setEditNameListener(EditNameListener listener) {
        this.listener = listener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.editname, null);
        inputName = (EditText)view.findViewById(R.id.inputname);

        builder.setView(view)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onEditNameComplete(inputName.getText().toString());
                        }
//                        EditNameListener listener = (EditNameListener) getParentFragment();
//                        listener.onEditNameComplete(inputName.getText().toString());
                    }
                }).setNegativeButton("取消", null);
        return builder.create();
    }
}
