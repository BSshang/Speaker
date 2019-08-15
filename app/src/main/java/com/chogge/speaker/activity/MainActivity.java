package com.chogge.speaker.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chogge.speaker.R;
import com.chogge.speaker.view.Knob;
import com.chogge.speaker.view.Knob.OnStateChanged;
import com.chogge.speaker.view.VolumeView;
import com.chogge.speaker.view.VolumeView.OnChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements View.OnTouchListener {
    @BindView(R.id.knob_left)
    Knob knobLeft;
    @BindView(R.id.knob_right)
    Knob knobRight;
    @BindView(R.id.volume)
    VolumeView volumeView;
    int lastValue = 0;
    float level = 0.9F;
    SoundPool mSoundPool;
    int preState = 0;
    int soundID_1;
    int soundID_2;
    int soundID_3;
    int soundID_4;
    static int soundID_num;
    @BindView(R.id.btn_haptic)
    TextView btnHaptic;
    @BindView(R.id.btn_photos)
    TextView btnPhotos;
    @BindView(R.id.btn_message)
    TextView btnMessage;
    @BindView(R.id.btn_map)
    TextView btnMap;
    @BindView(R.id.btn_music)
    TextView btnMusic;
    @BindView(R.id.btn_call)
    TextView btnCall;
    @BindView(R.id.btn_search)
    ImageView btnSearch;
    @BindView(R.id.btn_microphone)
    ImageView btnMicrophone;
    @BindView(R.id.btn_wifi)
    ImageView btnWifi;
    @BindView(R.id.btn_max)
    ImageView btnMax;
    @BindView(R.id.btn_min)
    ImageView btnMin;
    @BindView(R.id.btn_setting)
    TextView btnSetting;
    @BindView(R.id.btn_controls)
    TextView btnControls;
    @BindView(R.id.btn_logo)
    TextView btnLogo;
    //创建震动服务对象
    private Vibrator mVibrator;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
    //    setRequestedOrientation(SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.activity_main4);
        ButterKnife.bind(this);

        //获取手机震动服务
        mVibrator=(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        mSoundPool = new SoundPool(1, 3, 0);
        soundID_1 = mSoundPool.load(getApplicationContext(), R.raw.wavefile, 1);
        soundID_2 = mSoundPool.load(getApplicationContext(), R.raw.afile, 1);
        soundID_3 = mSoundPool.load(getApplicationContext(), R.raw.bfile, 1);
        soundID_4 = mSoundPool.load(getApplicationContext(), R.raw.abfile, 1);

        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

            }
        });
        SharedPreferences sp = getSharedPreferences("bstar", 0);
        int file_type = sp.getInt("wavefile", 0);
        switch(file_type){
            case 0: soundID_num = soundID_1; break;
            case 1: soundID_num = soundID_2; break;
//            case 2: soundID_num = soundID_3; break;
//            case 3: soundID_num = soundID_4; break;
            default :
                soundID_num = soundID_1; break;
        }


        volumeView.setOnChangeListener(new OnChangeListener() {
            public void onChange(int count) {
                if (lastValue != count) {
                    lastValue = count;
                    mSoundPool.play(soundID_num, level, level, 10, 2, 1.0f);
                }
            }
        });

        knobLeft.setNumberOfStates(9);
        knobLeft.setState(0);
        knobLeft.getNumberOfStates();
        knobLeft.setOnStateChanged(new OnStateChanged() {
            public void onState(int state) {
                if (preState != state) {
                    preState = state;
                    mSoundPool.play(soundID_num, level, level, 10, 1, 1.0f);
                }
            }
        });

        knobRight.setNumberOfStates(9);
        knobRight.setState(0);
        knobRight.setOnStateChanged(new OnStateChanged() {
            public void onState(int state) {
                if (preState != state) {
                    preState = state;
                    mSoundPool.play(soundID_num, level, level, 10, 1, 1.0f);
                }
            }
        });

        btnHaptic.setOnTouchListener(this);
        btnPhotos.setOnTouchListener(this);
        btnMessage.setOnTouchListener(this);
        btnMap.setOnTouchListener(this);
        btnMusic.setOnTouchListener(this);
        btnCall.setOnTouchListener(this);
        btnSearch.setOnTouchListener(this);
        btnMicrophone.setOnTouchListener(this);
        btnWifi.setOnTouchListener(this);
        btnLogo.setOnTouchListener(this);
    }


    //Activity从后台重新回到前台时被调用
    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sp = getSharedPreferences("bstar", 0);
        int file_type = sp.getInt("wavefile", 0);
        switch(file_type){
            case 0: soundID_num = soundID_1; break;
            case 1: soundID_num = soundID_2; break;
//            case 2: soundID_num = soundID_3; break;
//            case 3: soundID_num = soundID_4; break;
            default :
                soundID_num = soundID_1; break;
        }
        Log.i(TAG, "onRestart called.");
    }

    //Activity创建或者从被覆盖、后台重新回到前台时被调用
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called.");
    }

    @OnClick({R.id.btn_max, R.id.btn_min, R.id.btn_setting, R.id.btn_controls, R.id.btn_logo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_max:
                Log.e("TAG", "max:"+volumeView.getCurrent());
                if (volumeView.getCurrent() < 8) {
                    volumeView.setCurrent(volumeView.getCurrent() + 1);
                    Play();
                }
                break;
            case R.id.btn_min:
                Log.e("TAG", "min:"+volumeView.getCurrent());
                if (volumeView.getCurrent() >= 1) {
                    volumeView.setCurrent(volumeView.getCurrent() - 1);
                    Play();
                }
                break;
            case R.id.btn_setting:
                VIBRATE();
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            case R.id.btn_controls:
                VIBRATE();
                startActivity(new Intent(MainActivity.this, SendActivity.class));
                break;
            case R.id.btn_logo:
                VIBRATE();
                startActivity(new Intent(MainActivity.this, CarBrandActivity.class));
                break;
        }
    }

    private void VIBRATE(){
     //   mVibrator.vibrate(new long[]{100,100},-1);
        Play();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int count = 0;
        switch (v.getId()) {
            case R.id.btn_haptic /*2131558502*/:
                count = 3;
                //Play();
                break;
            case R.id.btn_photos /*2131558503*/:
                count = 3;
               // Play();
                break;
            case R.id.btn_message /*2131558504*/:
                count = 3;
               // Play();
                break;
            case R.id.btn_map /*2131558505*/:
                count = 3;
                //Play();
                break;
            case R.id.btn_music /*2131558506*/:
                count = 3;
               // Play();
                break;
            case R.id.btn_call /*2131558507*/:
                count = 3;
                //Play();
                break;
            case R.id.btn_search /*2131558508*/:
                count = 3;
                //Play();
                break;
            case R.id.btn_microphone /*2131558509*/:
                count = 3;
               // Play();
                break;
            case R.id.btn_wifi /*2131558510*/:
                count = 3;
                //Play();
                break;
        }
        final int frequency = getSharedPreferences("bstar", 0).getInt("frequency", 30);
        final int temp = count;
        new Thread() {
            public void run() {
                super.run();
                for (int i = 0; i < temp; i++) {
                    MainActivity.this.mSoundPool.play(MainActivity.this.soundID_num, MainActivity.this.level, MainActivity.this.level, 10, 1, 1.0f);
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

     public void Play(){
         mSoundPool.play(soundID_num, level, level, 10, 1, 1.0f);
    }
}
