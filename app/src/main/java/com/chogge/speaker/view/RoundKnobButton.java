package com.chogge.speaker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class RoundKnobButton extends RelativeLayout implements OnGestureListener {
    private Bitmap bmpRotorOff;
    private Bitmap bmpRotorOn;
    private GestureDetector gestureDetector;
    private ImageView ivRotor;
    private float mAngleDown;
    private float mAngleUp;
    private boolean mState = false;
    private RoundKnobButtonListener m_listener;
    private int m_nHeight = 0;
    private int m_nWidth = 0;

    public interface RoundKnobButtonListener {
        void onRotate(int i);

        void onStateChange(boolean z);
    }

    public void SetListener(RoundKnobButtonListener l) {
        this.m_listener = l;
    }

    public void SetState(boolean state) {
        this.mState = state;
        this.ivRotor.setImageBitmap(state ? this.bmpRotorOn : this.bmpRotorOff);
    }

    public RoundKnobButton(Context context, int back, int rotoron, int rotoroff, int w, int h) {
        super(context);
        this.m_nWidth = w;
        this.m_nHeight = h;
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(back);
        LayoutParams layoutParams = new LayoutParams(w, h);
        layoutParams.addRule(13);
        addView(imageView, layoutParams);
        Bitmap srcon = BitmapFactory.decodeResource(context.getResources(), rotoron);
        Bitmap srcoff = BitmapFactory.decodeResource(context.getResources(), rotoroff);
        float scaleWidth = ((float) w) / ((float) srcon.getWidth());
        float scaleHeight = ((float) h) / ((float) srcon.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        this.bmpRotorOn = Bitmap.createBitmap(srcon, 0, 0, srcon.getWidth(), srcon.getHeight(), matrix, true);
        this.bmpRotorOff = Bitmap.createBitmap(srcoff, 0, 0, srcoff.getWidth(), srcoff.getHeight(), matrix, true);
        this.ivRotor = new ImageView(context);
        this.ivRotor.setImageBitmap(this.bmpRotorOn);
        layoutParams = new LayoutParams(-2, -2);
        layoutParams.addRule(13);
        addView(this.ivRotor, layoutParams);
        SetState(this.mState);
        this.gestureDetector = new GestureDetector(getContext(), this);
    }

    private float cartesianToPolar(float x, float y) {
        return (float) (-Math.toDegrees(Math.atan2((double) (x - 0.5f), (double) (y - 0.5f))));
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    public boolean onDown(MotionEvent event) {
        this.mAngleDown = cartesianToPolar(1.0f - (event.getX() / ((float) getWidth())), 1.0f - (event.getY() / ((float) getHeight())));
        return true;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        this.mAngleUp = cartesianToPolar(1.0f - (e.getX() / ((float) getWidth())), 1.0f - (e.getY() / ((float) getHeight())));
        if (!(Float.isNaN(this.mAngleDown) || Float.isNaN(this.mAngleUp) || Math.abs(this.mAngleUp - this.mAngleDown) >= 10.0f)) {
            SetState(!this.mState);
            if (this.m_listener != null) {
                this.m_listener.onStateChange(this.mState);
            }
        }
        return true;
    }

    public void setRotorPosAngle(float deg) {
        if (deg >= 210.0f || deg <= 150.0f) {
            if (deg > 180.0f) {
                deg -= 360.0f;
            }
            Matrix matrix = new Matrix();
            this.ivRotor.setScaleType(ScaleType.MATRIX);
            matrix.postRotate(deg, (float) (getWidth() / 2), (float) (getHeight() / 2));
            this.ivRotor.setImageMatrix(matrix);
        }
    }

    public void setRotorPercentage(int percentage) {
        int posDegree = (percentage * 3) - 150;
        if (posDegree < 0) {
            posDegree += 360;
        }
        setRotorPosAngle((float) posDegree);
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float rotDegrees = cartesianToPolar(1.0f - (e2.getX() / ((float) getWidth())), 1.0f - (e2.getY() / ((float) getHeight())));
        if (Float.isNaN(rotDegrees)) {
            return false;
        }
        float posDegrees = rotDegrees;
        if (rotDegrees < 0.0f) {
            posDegrees = 360.0f + rotDegrees;
        }
        if (posDegrees <= 210.0f && posDegrees >= 150.0f) {
            return false;
        }
        setRotorPosAngle(posDegrees);
        int percent = (int) ((rotDegrees + 150.0f) / 3.0f);
        if (this.m_listener != null) {
            this.m_listener.onRotate(percent);
        }
        return true;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }
}
