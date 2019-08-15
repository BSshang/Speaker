package com.chogge.speaker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.chogge.speaker.shabi.DisplayUtil;
import com.chogge.speaker.utils.MyUtils;

public class VolumeView extends View {
    private int MAX;
    private int current = 0;
    private int height;
    private int leftMargen = 0;
    private OnChangeListener onChangeListener;
    private Paint paint = new Paint();
    private int recW;
    private int rectH = this.height;
    private int rectMargen;
    private int width;

    public interface OnChangeListener {
        void onChange(int i);
    }

    public VolumeView(Context context) {
        super(context);
        init();
    }

    public VolumeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VolumeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        //   this.height = (int) MyUtils.dp2Px(100, getContext());
        this.height = DisplayUtil.dp2px(getContext(), 45);
        this.MAX = 8;
        this.rectH = this.height;
        this.recW = DisplayUtil.dp2px(getContext(), 21.0f);
        this.rectMargen = this.recW + DisplayUtil.dp2px(getContext(), 8.0f);
        this.width = DisplayUtil.dp2px(getContext(), 280.0f);
        this.current = 0;
        this.leftMargen = 0;
    }

    protected void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        this.paint.setColor(Color.parseColor("#8A8A8A"));
        for (i = this.current; i <= this.MAX; i++) {
            canvas.drawRect((float) (this.leftMargen + ((i + 0) * this.rectMargen)), (float) (this.height - (DisplayUtil.dp2px(getContext(), 5f) * i)), (float) ((this.leftMargen + ((i + 0) * this.rectMargen)) + this.recW), (float) (((this.height - this.rectH) / 2) + this.rectH), this.paint);
        }
        this.paint.setColor(Color.parseColor("#ff669900"));
        for (i = 0; i <= this.current; i++) {
            canvas.drawRect((float) (this.leftMargen + ((i + 0) * this.rectMargen)), (float) (this.height - (DisplayUtil.dp2px(getContext(), 5f) * i)), (float) ((this.leftMargen + ((i + 0) * this.rectMargen)) + this.recW), (float) (((this.height - this.rectH) / 2) + this.rectH), this.paint);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
            case 1:
            case 2:
                if (event.getX() > ((float) (this.leftMargen + this.rectMargen)) && event.getX() < ((float) ((this.leftMargen + ((this.MAX + 1) * this.rectMargen)) + this.recW)) && event.getY() > ((float) ((this.height - this.rectH) / 2)) && event.getY() < ((float) (((this.height - this.rectH) / 2) + this.rectH))) {
                    this.current = ((int) ((event.getX() - ((float) this.leftMargen)) / ((float) this.rectMargen))) - 1;
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChange(this.current);
                        break;
                    }
                }
                break;
        }
        invalidate();
        return true;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(this.width, this.height);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
        postInvalidate();
    }
}
