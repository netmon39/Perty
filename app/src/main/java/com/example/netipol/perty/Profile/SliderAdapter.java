package com.example.netipol.perty.Profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.netipol.perty.R;

import java.util.Objects;

/**
 * Created by USER on 07/04/2018.
 */

public class SliderAdapter extends PagerAdapter{
    Context context;
    LayoutInflater layoutInflater;
    public SliderAdapter(Context context){
        this.context=context;
    }

    public int[] slide_images={

            R.drawable.logo,
            R.drawable.hne,
            R.drawable.jnc,
            R.drawable.pnp,
            R.drawable.nnp,
            R.drawable.done
    };

     public String[] slide_headings ={
            "Welcome To PERTY!",
             "HOME & EXPLORE",
             "JOIN & CREATE",
             "PUBLIC & PRIVATE",
             "NOTI. & PROFILE",
             "READY, SET, GO!"
     };

     public String[] slide_descri ={

            "Where parties started.",
             "HOME shows what you like."+"\nEXPLORE lets you see more!",
             "JOIN events."+"\nCREATE your own event!",
             "PUBLIC is for Everyone." + "\nPRIVATE is for friends.",
             "NOTI. & PROFILE keep track of your EVENTS.",
             "You are ready to PERTY!"

     };


    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject( View view, Object object) {
        return view == object;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public Object instantiateItem(ViewGroup container, int position){

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate (R.layout.tutorial_slider, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slideImage);
        TextView slideHeading = (TextView) view.findViewById(R.id.slideHeading);
        TextView slideDescri = (TextView) view.findViewById(R.id.slideDesc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescri.setText(slide_descri[position]);


        container.addView(view);

        return view;
    }

    public void destroyItem(ViewGroup container, int position, Object object){

        container.removeView((RelativeLayout)object);
    }
}
