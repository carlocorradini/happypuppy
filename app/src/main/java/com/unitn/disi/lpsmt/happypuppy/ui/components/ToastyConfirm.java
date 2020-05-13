package com.unitn.disi.lpsmt.happypuppy.ui.components;


import android.content.Context;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.unitn.disi.lpsmt.happypuppy.R;

public class ToastyConfirm extends android.widget.Toast {

    public ToastyConfirm(Context c, View v, String message){
        super(c);
        LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        View layout = inflater.inflate(R.layout.custom_toast_confirm_fragment, v.findViewById(R.id.custom_toast_confirm_view));

        TextView text = layout.findViewById(R.id.custom_toast_message);
        text.setText(message);

        android.widget.Toast toast = new android.widget.Toast(c);
        toast.setGravity(Gravity.BOTTOM, 10, 40);
        toast.setDuration(android.widget.Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}