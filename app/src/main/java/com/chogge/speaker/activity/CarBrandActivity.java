package com.chogge.speaker.activity;

import android.app.Service;
import android.media.SoundPool;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.chogge.speaker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;


public class CarBrandActivity extends AppCompatActivity implements View.OnTouchListener {

    @BindView(R.id.logo_leapmotor)
    RelativeLayout leapmotor;
    @BindView(R.id.logo_reechauto)
    RelativeLayout reechauto;
    @BindView(R.id.logo_skywell)
    RelativeLayout skywell;
    @BindView(R.id.logo_faw)
    RelativeLayout faw;
    float level = 0.9F;
    static int soundID_num;
    //创建震动服务对象
    private Vibrator mVibrator;
    private  int soundID_1;
    private  int soundID_2;
    private  int soundID_3;
    private  int soundID_4;
    private SoundPool mSoundPool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_car_brand);
        ButterKnife.bind(this);


        //获取手机震动服务
        mVibrator=(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        mSoundPool = new SoundPool(1, 3, 0);

        soundID_1 = mSoundPool.load(getApplicationContext(), R.raw.leapmotor, 1);
        soundID_2 = mSoundPool.load(getApplicationContext(), R.raw.reechauto, 1);
        soundID_3 = mSoundPool.load(getApplicationContext(), R.raw.skywell, 1);
        soundID_4 = mSoundPool.load(getApplicationContext(), R.raw.faw, 1);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

            }
        });

        leapmotor.setOnTouchListener(this);
        reechauto.setOnTouchListener(this);
        skywell.setOnTouchListener(this);
        faw.setOnTouchListener(this);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int count = 0;
        switch (v.getId()) {
            case R.id.logo_leapmotor /*2131558502*/:
                count = 3;
                soundID_num = soundID_1;
                //Play();
                break;
            case R.id.logo_reechauto /*2131558503*/:
                count = 3;
                soundID_num = soundID_2;
                // Play();
                break;
            case R.id.logo_skywell /*2131558504*/:
                count = 3;
                soundID_num = soundID_3;
                // Play();
                break;
            case R.id.logo_faw /*2131558505*/:
                count = 3;
                soundID_num = soundID_4;
                //Play();
                break;
        }
        final int temp = count;
        new Thread() {
            public void run() {
                super.run();
                for (int i = 0; i < temp; i++) {
                    CarBrandActivity.this.mSoundPool.play(CarBrandActivity.this.soundID_num, CarBrandActivity.this.level, CarBrandActivity.this.level, 10, 1, 1.0f);
                    try {
                        Thread.sleep((long) (70));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "run: num" + i+ "");
                }
            }
        }.start();
        return false;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
