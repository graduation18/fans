package com.systemonline.fanscoupon.stepview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.shuhart.stepview.StepView;
import com.systemonline.fanscoupon.Base.BaseActivity;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.SplashScreen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class stepview_activity extends BaseActivity {
    StepView stepView;
    FrameLayout frame;
    List<String>steps=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( getSharedPreferences("trail_skip",MODE_PRIVATE).getBoolean("skipped",false)){
            Intent goto_splash=new Intent(stepview_activity.this,SplashScreen.class);
            startActivity(goto_splash);
        }
        setContentView(R.layout.step_view);
        frame = (FrameLayout) findViewById(R.id.frame);
        steps.add("First Step");
        steps.add("Second Step");
        steps.add("Third Step");
        steps.add("Fourth Step");
        stepView=(StepView)findViewById(R.id.step_view);
        stepView.setSteps(steps);
        stepView.getState()
                .selectedTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .animationType(StepView.ANIMATION_ALL)
                .selectedCircleColor(ContextCompat.getColor(this, R.color.colorAccent))
                .selectedStepNumberColor(ContextCompat.getColor(this, R.color.colorPrimary))
                // You should specify only stepsNumber or steps array of strings.

                // You should specify only steps number or steps array of strings.
                // In case you specify both steps array is chosen.
                .stepsNumber(4)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                // other state methods are equal to the corresponding xml attributes
                .commit();
        stepView.done(false);

        stepView.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {
                // 0 is the first step
                if (step==0){
                    frame.removeAllViews();
                    LayoutInflater.from(stepview_activity.this).inflate(R.layout.step0, frame, true);
                    stepView.go(step, true);
                }else if (step==1){
                    frame.removeAllViews();
                    LayoutInflater.from(stepview_activity.this).inflate(R.layout.step1, frame, true);
                    stepView.go(step, true);
                }else if (step==2){
                    frame.removeAllViews();
                    LayoutInflater.from(stepview_activity.this).inflate(R.layout.step2, frame, true);
                    stepView.go(step, true);
                }else {
                    frame.removeAllViews();
                    LayoutInflater.from(stepview_activity.this).inflate(R.layout.step3, frame, true);
                    stepView.go(step, true);
                    Button gotosplash=(Button)findViewById(R.id.gotosplash);
                    gotosplash.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getSharedPreferences("trail_skip",MODE_PRIVATE).edit()
                                    .putBoolean("skipped",true)
                                    .commit();
                            Intent goto_splash=new Intent(stepview_activity.this,SplashScreen.class);
                            startActivity(goto_splash);
                        }
                    });

                }
            }
        });


    }
}
