package com.example.netipol.perty.SelectPref;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.netipol.perty.R;

public class GridItemView extends FrameLayout {//SelectPref

    private TextView textView;

    public GridItemView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.activity_grid_item_view, this);
        textView = (TextView) getRootView().findViewById(R.id.text);
    }

    public void display(String text, boolean isSelected, int pos) {
        textView.setText(text);
        display(isSelected, pos);
    }

    public void display(boolean isSelected, int pos) {
        //textView.setBackgroundResource(isSelected ? R.drawable.green_square : R.drawable.gray_square);
        switch (pos) {
            case 0:
                textView.setBackgroundResource(isSelected ? R.drawable.cat0sel : R.drawable.cat0);
                break;
            case 1:
                textView.setBackgroundResource(isSelected ? R.drawable.cat1sel : R.drawable.cat1);
                break;
            case 2:
                textView.setBackgroundResource(isSelected ? R.drawable.cat2sel : R.drawable.cat2);
                break;
            case 3:
                textView.setBackgroundResource(isSelected ? R.drawable.cat3sel : R.drawable.cat3);
                break;
            case 4:
                textView.setBackgroundResource(isSelected ? R.drawable.cat4sel : R.drawable.cat4);
                break;
            case 5:
                textView.setBackgroundResource(isSelected ? R.drawable.cat5sel : R.drawable.cat5);
                break;
            case 6:
                textView.setBackgroundResource(isSelected ? R.drawable.cat6sel : R.drawable.cat6);
                break;
            case 7:
                textView.setBackgroundResource(isSelected ? R.drawable.cat7sel : R.drawable.cat7);
                break;
            case 8:
                textView.setBackgroundResource(isSelected ? R.drawable.cat8sel : R.drawable.cat8);
                break;
        }
    }

}
