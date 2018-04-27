package com.teamperty.netipol.perty.Profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamperty.netipol.perty.R;

/**
 * Created by USER on 06/04/2018.
 */

public class TutorialActivityAsGuide extends AppCompatActivity {
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDots;

    private SliderAdapter sliderAdapter;

    private Button mSkipButton;

    private int mCurrentpage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        mSlideViewPager = (ViewPager) findViewById(R.id.tutorial_slide);
        mDotLayout=(LinearLayout)findViewById(R.id.tutorial_dot);

        sliderAdapter = new SliderAdapter(this);

        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        mSkipButton= (Button) findViewById(R.id.skip_button);


        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if opened from Profile Settings -> get rid of intent to activity

                if(mCurrentpage ==5){
                    finish();

                }else{

                    final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(TutorialActivityAsGuide.this);
                    alertDialog.setTitle("Are you ready?");
                    alertDialog.setMessage("You can always view this tutorial again in the setting option");
                    alertDialog.setCancelable(true);
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setPositiveButton("OK, Let's Go!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dialog.cancel();
                            finish();
                        }
                    });
                    alertDialog.show();

                }

            }
        });


    }

    public void addDotsIndicator(int position){

        mDots = new TextView[6];
        mDotLayout.removeAllViews();

        for(int i =0; i<mDots.length;i++){

            mDots[i]=new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(32);
            mDots[i].setTextColor(getResources().getColor(R.color.disdotyel));

            mDotLayout.addView(mDots[i]);

        }

        if(mDots.length > 0){

            mDots[position].setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int i) {

            addDotsIndicator(i);

            mCurrentpage=i;

            if(mCurrentpage == 5){
                Log.d("abcde", String.valueOf(mCurrentpage));
                mSkipButton.setText("Let's Go!");
            }else{
                mSkipButton.setText("Skip Tutorial");
                Log.d("abcde", String.valueOf(mCurrentpage));
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Guide");
        //bar.setDisplayHomeAsUpEnabled(true);

    }

}
