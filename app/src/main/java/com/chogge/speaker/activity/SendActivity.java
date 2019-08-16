package com.chogge.speaker.activity;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chogge.speaker.R;
import com.chogge.speaker.utils.SendThread;
import com.chogge.speaker.view.Knob;
import com.chogge.speaker.view.VolumeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

public class SendActivity extends AppCompatActivity implements View.OnTouchListener {

    @BindView(R.id.freq_1)
    TextView freq01;
    @BindView(R.id.freq_2)
    TextView freq02;
    @BindView(R.id.freq_4)
    TextView freq04;
    @BindView(R.id.freq_5)
    TextView freq05;
    @BindView(R.id.freq_6)
    TextView freq06;
    @BindView(R.id.freq_7)
    TextView freq07;
    @BindView(R.id.freq_8)
    TextView freq08;
    @BindView(R.id.freq_9)
    TextView freq09;
    @BindView(R.id.freq_10)
    TextView freq10;
    @BindView(R.id.freq_11)
    TextView freq11;
    @BindView(R.id.freq_12)
    TextView freq12;
    @BindView(R.id.freq_13)
    TextView freq13;
    @BindView(R.id.freq_14)
    TextView freq14;
    @BindView(R.id.freq_15)
    TextView freq15;
    @BindView(R.id.freq_16)
    TextView freq16;
    @BindView(R.id.freq_17)
    TextView freq17;
    @BindView(R.id.knob_left)
    Knob knobLeft;
    @BindView(R.id.knob_right)
    Knob knobRight;
    int lastValue = 0;
    float level = 0.9F;
    SoundPool mSoundPool;
    int preState = 0;
    int soundID_1;
    int soundID_2;
    int soundID_3;
    int soundID_4;
    static int soundID_num;
    static int soundICen_num;
    static String soundID_count = null;
    private StringBuffer receiveData = new StringBuffer();
    private String mIp = "";
    private int mPort = 0;
    private int put_info = 0;
    private SendThread sendthread;
    //创建震动服务对象
    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_send);
        ButterKnife.bind(this);
        clinet();

        //获取手机震动服务
        mVibrator=(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        mSoundPool = new SoundPool(1, 3, 0);
        soundID_1 = mSoundPool.load(getApplicationContext(), R.raw.wavefile, 1);
        soundID_2 = mSoundPool.load(getApplicationContext(), R.raw.afile, 1);
//        soundID_3 = mSoundPool.load(getApplicationContext(), R.raw.bfile, 1);
//        soundID_4 = mSoundPool.load(getApplicationContext(), R.raw.abfile, 1);

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
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

        knobLeft.setNumberOfStates(9);
        knobLeft.setState(4);
        knobLeft.setOnStateChanged(new Knob.OnStateChanged() {
            public void onState(int state) {
                if (preState != state) {
                    preState = state;
                    if(soundID_count != null){
                        soundID_count = getSound_HZ(state);
                        Log.i(TAG, "left：state=  " + state  + "  HZ = " + soundID_count  + "位数： "  + soundID_count.length());
                        putInfo(soundID_count);
                    }
                    Play();
                }
            }
        });

        knobRight.setNumberOfStates(9);
        knobRight.setState(4);
        knobRight.setOnStateChanged(new Knob.OnStateChanged() {
            public void onState(int state) {
                if (preState != state) {
                    preState = state;
                    if(soundID_count != null) {
                        soundID_count = getSound_V(state);
                        Log.i(TAG, "Right：state=  " + state + "  V = " + soundID_count + "位数： "  + soundID_count.length());
                        putInfo(soundID_count);
                    }
                    Play();
                }
            }
        });

        freq01.setOnTouchListener(this);
        freq02.setOnTouchListener(this);
        //freq03.setOnTouchListener(this);
        freq04.setOnTouchListener(this);
        freq05.setOnTouchListener(this);
        freq06.setOnTouchListener(this);
        freq07.setOnTouchListener(this);
        freq08.setOnTouchListener(this);
        freq09.setOnTouchListener(this);
        freq10.setOnTouchListener(this);
        freq11.setOnTouchListener(this);
        freq12.setOnTouchListener(this);
        freq13.setOnTouchListener(this);
        freq14.setOnTouchListener(this);
        freq15.setOnTouchListener(this);
        freq16.setOnTouchListener(this);
        freq17.setOnTouchListener(this);


    }

    /**
     *
     * wifi连接
     *
     */
    public void clinet(){

        mIp =  "192.168.1.1";
        mPort = 6060;
        soundID_count = null;
        soundICen_num = 0;
        sendthread = new SendThread(mIp, mPort, mHandler);
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendthread.run();
                if(sendthread.isConnected()){
                    mHandler.sendEmptyMessage(0x04);
                }
            }
        }).start();
    }

    /**
     *
     * 发送数据
     *
     */

    public  void putInfo(final String num){
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendthread.send(num);
            }
        }).start();

    }

    Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch(msg.what){
                case 0x00:
                    Log.i("mr_收到的数据： ", msg.obj.toString());
                    receiveData.append("接收到：" + msg.obj.toString());
                    receiveData.append("\r\n");
                    break;
                case 0x01:
                    break;
                case 0x02:
                    receiveData.append("连接中断：" );
                    receiveData.append("\r\n");
                    break;
                case 0x03:
                    receiveData.append("连接建立：" );
                    receiveData.append("\r\n");
                    break;
                case 0x04:
                    receiveData.append("连接成功");
                    receiveData.append("\r\n");
                    break;
                case 0x05:
                    receiveData.append("连接失败");
                    receiveData.append("\r\n");
                    break;
                case 0x06:
                    receiveData.append("连接开始");
                    receiveData.append("\r\n");
                    break;
                case 0x07:
                    receiveData.append("创建连接失败");
                    receiveData.append("\r\n");
                    break;
                case 0x08:
                    receiveData.append(msg.obj);
                    receiveData.append("\r\n");
                    break;
            }
        }
    };

    private void VIBRATE(){
        //   mVibrator.vibrate(new long[]{100,100},-1);
        Play();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int count = 0;
        switch (v.getId()) {
            case R.id.freq_1 /*2131558502*/:
                count = 3;
                soundID_count = "194000050";
                putInfo("194000050");
                //Play();
                break;
            case R.id.freq_2 /*2131558503*/:
                count = 3;
                soundID_count = "163500050";
                putInfo("163500050");
               // Play();
                break;
//            case R.id.freq_3 /*2131558504*/:
//                count = 3;
//                soundID_count = "141000050";
//                putInfo("141000050");
//               // Play();
//                break;
            case R.id.freq_4 /*2131558505*/:
                count = 3;
                soundID_count = "1A3100050";
                putInfo("1A3100050");
               // Play();
                break;
            case R.id.freq_5 /*2131558506*/:
                count = 3;
                soundID_count = "133200050";
                putInfo("133200050");
               // Play();
                break;
            case R.id.freq_6 /*2131558507*/:
                count = 3;
                soundID_count = "121000050";
                putInfo("121000050");
              //  Play();
                break;
            case R.id.freq_7 /*2131558508*/:
                count = 3;
                soundID_count = "1C2700050";
                putInfo("1C2700050");
                Play();
                break;
            case R.id.freq_8 /*2131558509*/:
                count = 3;
                soundID_count = "150740050";
                putInfo("150740050");
               // Play();
                break;
            case R.id.freq_9 /*2131558510*/:
                count = 3;
                soundID_count = "1E2200050";
                putInfo("1E2200050");
               // Play();
                break;
            case R.id.freq_10/*2131558510*/:
                count = 3;
                soundID_count = "1F2400050";
                putInfo("1F2400050");
               // Play();
                break;
            case R.id.freq_11 /*2131558510*/:
                count = 3;
                soundID_count = "182000050";
                putInfo("182000050");
               // Play();
                break;
            case R.id.freq_12 /*2131558510*/:
                count = 3;
                soundID_count = "171500050";
                putInfo("171500050");
                Play();
                break;
            case R.id.freq_13 /*2131558510*/:
                count = 3;
                soundID_count = "113000050";
                putInfo("113000050");
                //Play();
                break;
            case R.id.freq_14 /*2131558510*/:
                count = 3;
                soundID_count = "1G0800050";
                putInfo("1G0800050");
               // Play();
                break;
            case R.id.freq_15 /*2131558510*/:
                count = 3;
                soundID_count = "1B3000050";
                putInfo("1B3000050");
               // Play();
                break;
            case R.id.freq_16/*2131558510*/:
                count = 3;
                soundID_count = "1D2048050";
                putInfo("1D2048050");
                //Play();
                break;
            case R.id.freq_17:
                soundID_count = "2F2400050";
                putInfo("2F2400050");
                count = 3;
                break;
        }
        final int frequency = getSharedPreferences("bstar", 0).getInt("frequency", 30);
        final int temp = count;
        soundICen_num = Integer.parseInt(soundID_count.substring(2,6));
        new Thread() {
            public void run() {
                super.run();
                for (int i = 0; i < temp; i++) {
                    SendActivity.this.mSoundPool.play(SendActivity.this.soundID_num, SendActivity.this.level, SendActivity.this.soundID_num, 10, 1, 1.0f);
                    try {
                        Thread.sleep((long) (70));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        return false;
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
            default :
                soundID_num = soundID_1; break;
        }
        Log.i(TAG, "onRestart called.");
    }

    public void Play(){
        mSoundPool.play(soundID_num, level, level, 10, 1, 1.0f);
    }

    /**
     *
     * 频率旋转按钮，旋转获得发送值
     * @param soundID
     * @return
     */
    public String getSound_HZ(int soundID){
      String endString =  "";
        if( soundID > 4 ){
            if(soundICen_num + ((soundID - 4)*150) >= 1000){
                   endString = soundID_count.substring(0,2) + (soundICen_num + ((soundID - 4)*150)) +soundID_count.substring(6,9) ;
                }else{
                   endString = soundID_count.substring(0,2) + "0" + (soundICen_num + ((soundID - 4)*150)) +soundID_count.substring(6,9) ;
            }
        }else if(soundID < 4 ){
            if(soundICen_num + ((soundID - 4)*150) > 1000){
                endString = soundID_count.substring(0,2) + (soundICen_num - ((4 - soundID )*150)) + soundID_count.substring(6,9);
            }else{
                endString = soundID_count.substring(0,2) + "0" + (soundICen_num - ((4 - soundID )*150)) +soundID_count.substring(6,9);
            }
        }else{
            endString =soundID_count;
        }
        return endString;
    }

    /**
     *
     * 电压旋转按钮，旋转获得发送值
     * @param soundID
     * @return
     */

    public String getSound_V(int soundID){
        String endString =  "";
        if( soundID > 4 ){
                endString = soundID_count.substring(0,6) + "0"+ (50 +((soundID - 4)*10));
        }else if(soundID < 4 ){
                endString = soundID_count.substring(0,6) + "0"+  (50 - ((4 - soundID) * 10));
        }else{
            endString = soundID_count;
        }
        return endString;
    }


    @Override
    protected void onDestroy()
    {
        sendthread.close();
        super.onDestroy();
    }
}