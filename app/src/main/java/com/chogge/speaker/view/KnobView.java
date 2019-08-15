package com.chogge.speaker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import com.chogge.speaker.R;

public class KnobView extends View {
    private float angleOne;
    private int angleRate;
    private Paint arcPaint;
    private int arcRadius;
    private Bitmap buttonImage;
    private Bitmap buttonImageShadow;
    private Paint buttonPaint;
    private float currentAngle;
    private Paint dialPaint;
    private int dialRadius;
    private int height;
    private boolean isDown;
    private boolean isMove;
    private int maxTemp;
    private int minTemp;
    private OnClickListener onClickListener;
    private OnTempChangeListener onTempChangeListener;
    private PaintFlagsDrawFilter paintFlagsDrawFilter;
    private float rotateAngle;
    private int scaleHeight;
    private Paint tempFlagPaint;
    private Paint tempPaint;
    private int temperature;
    private String title;
    private Paint titlePaint;
    private int width;

    public interface OnClickListener {
        void onClick(int i);
    }

    public interface OnTempChangeListener {
        void change(int i);
    }

    public KnobView(Context context) {
        this(context, null);
    }

    public KnobView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KnobView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.scaleHeight = dp2px(20.0f);
        this.title = "最高温度设置";
        this.temperature = 0;
        this.minTemp = 0;
        this.maxTemp = 36;
        this.angleRate = 4;
        this.angleOne = (270.0f / ((float) (this.maxTemp - this.minTemp))) / ((float) this.angleRate);
        this.buttonImage = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_rotate);
        this.buttonImageShadow = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_rotate_shadow);
        init();
    }

    private void init() {
        this.dialPaint = new Paint();
        this.dialPaint.setAntiAlias(true);
        this.dialPaint.setStrokeWidth((float) dp2px(3.0f));
        this.dialPaint.setStyle(Style.STROKE);
        this.arcPaint = new Paint();
        this.arcPaint.setAntiAlias(true);
        this.arcPaint.setColor(Color.parseColor("#3F51B5"));
        this.arcPaint.setStrokeWidth((float) dp2px(2.0f));
        this.arcPaint.setStyle(Style.STROKE);
        this.titlePaint = new Paint();
        this.titlePaint.setAntiAlias(true);
        this.titlePaint.setTextSize((float) sp2px(15.0f));
        this.titlePaint.setColor(Color.parseColor("#3F51B5"));
        this.titlePaint.setStyle(Style.STROKE);
        this.tempFlagPaint = new Paint();
        this.tempFlagPaint.setAntiAlias(true);
        this.tempFlagPaint.setTextSize((float) sp2px(20.0f));
        this.tempFlagPaint.setColor(Color.parseColor("#E4A07E"));
        this.tempFlagPaint.setStyle(Style.STROKE);
        this.buttonPaint = new Paint();
        this.tempFlagPaint.setAntiAlias(true);
        this.paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, 3);
        this.tempPaint = new Paint();
        this.tempPaint.setAntiAlias(true);
        this.tempPaint.setTextSize((float) sp2px(30.0f));
        this.tempPaint.setColor(Color.parseColor("#E27A3F"));
        this.tempPaint.setStyle(Style.STROKE);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int min = Math.min(h, w);
        this.height = min;
        this.width = min;
        this.dialRadius = (this.width / 2) - dp2px(20.0f);
        this.arcRadius = this.dialRadius - dp2px(20.0f);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScale(canvas);
        drawArc(canvas);
        drawText(canvas);
        drawButton(canvas);
        drawTemp(canvas);
    }

    private void drawScale(Canvas canvas) {
        int i;
        canvas.save();
        canvas.translate((float) (getWidth() / 2), (float) (getHeight() / 2));
        canvas.rotate(-133.0f);
        this.dialPaint.setColor(Color.parseColor("#8A8A8A"));
        for (i = 0; i < this.angleRate * (this.maxTemp - this.minTemp); i++) {
            canvas.drawLine(0.0f, (float) (-this.dialRadius), 0.0f, (float) ((-this.dialRadius) + this.scaleHeight), this.dialPaint);
            canvas.rotate(this.angleOne);
        }
        canvas.rotate(90.0f);
        this.dialPaint.setColor(Color.parseColor("#3F51B5"));
        for (i = 0; i < (this.temperature - this.minTemp) * this.angleRate; i++) {
            canvas.drawLine(0.0f, (float) (-this.dialRadius), 0.0f, (float) ((-this.dialRadius) + this.scaleHeight), this.dialPaint);
            canvas.rotate(this.angleOne);
        }
        canvas.restore();
    }

    private void drawArc(Canvas canvas) {
        canvas.save();
        canvas.translate((float) (getWidth() / 2), (float) (getHeight() / 2));
        canvas.rotate(137.0f);
        canvas.drawArc(new RectF((float) (-this.arcRadius), (float) (-this.arcRadius), (float) this.arcRadius, (float) this.arcRadius), 0.0f, 265.0f, false, this.arcPaint);
        canvas.restore();
    }

    private void drawText(Canvas canvas) {
        canvas.save();
        String minTempFlag;
        if (this.minTemp < 10) {
            minTempFlag = "0" + this.minTemp;
        } else {
            minTempFlag = this.minTemp + "";
        }
        float tempFlagWidth = this.titlePaint.measureText(this.maxTemp + "");
        canvas.rotate(55.0f, (float) (this.width / 2), (float) (this.height / 2));
        canvas.drawText("min", (((float) this.width) - tempFlagWidth) / 2.0f, (float) (this.height + dp2px(5.0f)), this.tempFlagPaint);
        canvas.rotate(-105.0f, (float) (this.width / 2), (float) (this.height / 2));
        canvas.drawText("max", (((float) this.width) - tempFlagWidth) / 2.0f, (float) (this.height + dp2px(5.0f)), this.tempFlagPaint);
        canvas.restore();
    }

    private void drawButton(Canvas canvas) {
        int buttonWidth = this.buttonImage.getWidth();
        int buttonHeight = this.buttonImage.getHeight();
        canvas.drawBitmap(this.buttonImageShadow, (float) ((this.width - this.buttonImageShadow.getWidth()) / 2), (float) ((this.height - this.buttonImageShadow.getHeight()) / 2), this.buttonPaint);
        Matrix matrix = new Matrix();
        matrix.setTranslate((float) ((this.width - buttonWidth) / 2), (float) ((this.height - buttonHeight) / 2));
        matrix.postRotate(45.0f + this.rotateAngle, (float) (this.width / 2), (float) (this.height / 2));
        canvas.setDrawFilter(this.paintFlagsDrawFilter);
        canvas.drawBitmap(this.buttonImage, matrix, this.buttonPaint);
    }

    private void drawTemp(Canvas canvas) {
        canvas.save();
        canvas.translate((float) (getWidth() / 2), (float) (getHeight() / 2));
        canvas.drawText(this.temperature + "", ((-this.tempPaint.measureText(this.temperature + "")) / 2.0f) - ((float) dp2px(5.0f)), -((this.tempPaint.ascent() + this.tempPaint.descent()) / 2.0f), this.tempPaint);
        canvas.restore();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.isDown = true;
                this.currentAngle = calcAngle(event.getX(), event.getY());
                break;
            case 2:
                this.isMove = true;
                float targetX = event.getX();
                float f = targetX;
                float targetY = event.getY();
                float f2 = targetY;
                float angle = calcAngle(targetX, targetY);
                float angleIncreased = angle - this.currentAngle;
                if (angleIncreased < -270.0f) {
                    angleIncreased += 360.0f;
                } else if (angleIncreased > 270.0f) {
                    angleIncreased -= 360.0f;
                }
                IncreaseAngle(angleIncreased);
                this.currentAngle = angle;
                invalidate();
                if (this.isDown) {
                    if (this.isMove) {
                        this.rotateAngle = ((float) ((this.temperature - this.minTemp) * this.angleRate)) * this.angleOne;
                        invalidate();
                        if (this.onTempChangeListener != null) {
                            this.onTempChangeListener.change(this.temperature);
                        }
                        this.isMove = false;
                    } else if (this.onClickListener != null) {
                        this.onClickListener.onClick(this.temperature);
                    }
                    this.isDown = false;
                    break;
                }
                break;
        }
        return true;
    }

    private float calcAngle(float targetX, float targetY) {
        double radian;
        float x = targetX - ((float) (this.width / 2));
        float y = targetY - ((float) (this.height / 2));
        if (x != 0.0f) {
            float tan = Math.abs(y / x);
            if (x > 0.0f) {
                if (y >= 0.0f) {
                    radian = Math.atan((double) tan);
                } else {
                    radian = 6.283185307179586d - Math.atan((double) tan);
                }
            } else if (y >= 0.0f) {
                radian = 3.141592653589793d - Math.atan((double) tan);
            } else {
                radian = 3.141592653589793d + Math.atan((double) tan);
            }
        } else if (y > 0.0f) {
            radian = 1.5707963267948966d;
        } else {
            radian = -1.5707963267948966d;
        }
        return (float) ((180.0d * radian) / 3.141592653589793d);
    }

    private void IncreaseAngle(float angle) {
        this.rotateAngle += angle;
        if (this.rotateAngle < 0.0f) {
            this.rotateAngle = 0.0f;
        } else if (this.rotateAngle > 270.0f) {
            this.rotateAngle = 270.0f;
        }
        this.temperature = ((int) (((double) ((this.rotateAngle / this.angleOne) / ((float) this.angleRate))) + 0.5d)) + this.minTemp;
    }

    public void setAngleRate(int angleRate) {
        this.angleRate = angleRate;
    }

    public void setTemp(int temp) {
        setTemp(this.minTemp, this.maxTemp, temp);
    }

    public void setTemp(int minTemp, int maxTemp, int temp) {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        if (temp < minTemp) {
            this.temperature = minTemp;
        } else {
            this.temperature = temp;
        }
        this.rotateAngle = ((float) ((temp - minTemp) * this.angleRate)) * this.angleOne;
        this.angleOne = (270.0f / ((float) (maxTemp - minTemp))) / ((float) this.angleRate);
        invalidate();
    }

    public void setOnTempChangeListener(OnTempChangeListener onTempChangeListener) {
        this.onTempChangeListener = onTempChangeListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(1, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(2, sp, getResources().getDisplayMetrics());
    }
}
