package com.chogge.speaker.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.chogge.speaker.R;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;

public class SettingActivity extends AppCompatActivity implements OnClickListener {
    private Button cancel;
    private  EditText duration;
    private EditText frequency;
    private EditText interval_factor;
    private EditText rotate_fator;
    private  Button save;
    private RadioButton square;
    private RadioButton triangular;
    private RadioGroup radioGroup;

    private static int filetype = 0 ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
    //    setRequestedOrientation(SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView((int) R.layout.activity_setting);
        square = (RadioButton) findViewById(R.id.square);
        radioGroup= (RadioGroup) findViewById(R.id.radiogroup);
        triangular = (RadioButton) findViewById(R.id.triangular);
//        sine = (RadioButton) findViewById(R.id.sine);
//        ab= (RadioButton) findViewById(R.id.AB);
        square.setChecked(true);

        save = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);
        frequency = (EditText) findViewById(R.id.frequency);
        interval_factor = (EditText) findViewById(R.id.interval_factor);
        duration = (EditText) findViewById(R.id.duration);
        rotate_fator = (EditText) findViewById(R.id.rotate_fator);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroupListener());


        SharedPreferences sp = getSharedPreferences("bstar", 0);
        int hz = sp.getInt("frequency", 30);
        int durationValue = sp.getInt("duration", 10);
        int interval_factorValue = sp.getInt("interval_factor", 5);
        int rotate_fatorValue = sp.getInt("rotate_fator", 5);
        int file_type = sp.getInt("wavefile", 0);


        switch(file_type){
            case 0: square.setChecked(true); break;
            case 1: triangular.setChecked(true); break;
//            case 2: sine.setChecked(true); break;
//            case 3: ab.setChecked(true); break;
            default :
                square.setChecked(true); break;
        }
        frequency.setText(hz + "");
        duration.setText(durationValue + "");
        interval_factor.setText(interval_factorValue + "");
        rotate_fator.setText(rotate_fatorValue + "");

    }

    @SuppressLint("WrongConstant")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel /*2131558532*/:
                finish();
                return;
            case R.id.save /*2131558533*/:
                Toast.makeText(this, "Save Success!", 0).show();
                int hz = Integer.valueOf(this.frequency.getText().toString()).intValue();
                int durationValue = Integer.valueOf(this.duration.getText().toString()).intValue();
                int interval_factorValue = Integer.valueOf(this.interval_factor.getText().toString()).intValue();
                getSharedPreferences("bstar", 0).edit()
                        .putInt("wavefile", filetype)
                        .putInt("frequency", hz)
                        .putInt("duration", durationValue)
                        .putInt("interval_factor", interval_factorValue)
                        .putInt("rotate_fator", Integer.valueOf(this.rotate_fator.getText().toString()).intValue()).commit();
                finish();
                return;
            default:
                return;
        }
    }

    class RadioGroupListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId==square.getId()){
                filetype = 0;
                System.out.println("选中了female!"+0);
            }else if (checkedId==triangular.getId()){
                filetype = 1;
                System.out.println("选中了male!"+1);
            }
//            else if (checkedId==sine.getId()){
//                filetype = 2;
//                System.out.println("选中了male!"+2);
//            }else if (checkedId==ab.getId()){
//                filetype = 3;
//                System.out.println("选中了male!"+2);
//            }
        }
    }

}
