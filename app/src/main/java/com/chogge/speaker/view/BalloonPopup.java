package com.chogge.speaker.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import com.chogge.speaker.R;

public class BalloonPopup {
    private View attachView;
    private BDelay bDelay;
    private BalloonAnimation balloonAnimation;
    private int bgColor;
    private Context ctx;
    private View customView;
    private boolean dismissOnTap;
    private Drawable drawable;
    private int fgColor;
    private BalloonGravity gravity;
    private View hostedView;
    private int layoutRes;
    private int offsetX;
    private int offsetY;
    private PopupWindow popupWindow;
    private boolean stayWithinScreenBounds;
    private String text;
    private int textSize;
    private TextView textView;
    private int timeToLive;

    public enum BalloonAnimation {
        pop,
        scale,
        fade,
        fade75,
        fade_and_pop,
        fade_and_scale,
        fade75_and_pop,
        fade75_and_scale,
        instantin_popout,
        instantin_scaleout,
        instantin_fadeout,
        instantin_fade75out,
        instantin_fade_and_popout,
        instantin_fade_and_scaleout,
        instantin_fade75_and_popout,
        instantin_fade75_and_scaleout
    }

    public enum BalloonGravity {
        alltop_allleft,
        alltop_halfleft,
        alltop_center,
        alltop_halfright,
        alltop_allright,
        halftop_allleft,
        halftop_halfleft,
        halftop_center,
        halftop_halfright,
        halftop_allright,
        center_allleft,
        center_halfleft,
        center,
        center_halfright,
        center_allright,
        halfbottom_allleft,
        halfbottom_halfleft,
        halfbottom_center,
        halfbottom_halfright,
        halfbottom_allright,
        allbottom_allleft,
        allbottom_halfleft,
        allbottom_center,
        allbottom_halfright,
        allbottom_allright
    }

    public enum BalloonShape {
        oval,
        rounded_square,
        little_rounded_square,
        square
    }

    public static class Builder {
        private View attachView;
        private BalloonAnimation balloonAnimation = BalloonAnimation.pop;
        private int bgColor = -1;
        private Context ctx;
        private View customView;
        private boolean dismissOnTap = true;
        private Drawable drawable;
        private int fgColor = ViewCompat.MEASURED_STATE_MASK;
        private BalloonGravity gravity = BalloonGravity.halftop_halfright;
        private int layoutRes = R.layout.text_balloon;
        private int offsetX = 0;
        private int offsetY = 0;
        private boolean stayWithinScreenBounds = true;
        private String text;
        private int textSize = 12;
        private int timeToLive = 1500;

        Builder(Context ctx, View attachView) {
            this.ctx = ctx;
            this.attachView = attachView;
            this.drawable = ctx.getResources().getDrawable(R.drawable.bg_circle);
        }

        public Builder gravity(BalloonGravity gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder dismissOnTap(boolean dismissOnTap) {
            this.dismissOnTap = dismissOnTap;
            return this;
        }

        public Builder stayWithinScreenBounds(boolean stayWithinScreenBounds) {
            this.stayWithinScreenBounds = stayWithinScreenBounds;
            return this;
        }

        public Builder offsetX(int offsetX) {
            this.offsetX = offsetX;
            return this;
        }

        public Builder offsetY(int offsetY) {
            this.offsetY = offsetY;
            return this;
        }

        public Builder positionOffset(int offsetX, int offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            return this;
        }

        public Builder bgColor(int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public Builder fgColor(int fgColor) {
            this.fgColor = fgColor;
            return this;
        }

        public Builder layoutRes(int layoutRes) {
            this.layoutRes = layoutRes;
            return this;
        }

        public Builder customView(View customView) {
            this.customView = customView;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder text(int textRes) {
            this.text = this.ctx.getResources().getString(textRes);
            return this;
        }

        public Builder textSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder shape(BalloonShape balloonShape) {
            switch (balloonShape) {
                case oval:
                    this.drawable = this.ctx.getResources().getDrawable(R.drawable.bg_circle);
                    break;
                case rounded_square:
                    this.drawable = this.ctx.getResources().getDrawable(R.drawable.bg_rounded_square);
                    break;
                case little_rounded_square:
                    this.drawable = this.ctx.getResources().getDrawable(R.drawable.bg_little_rounded_square);
                    break;
                case square:
                    this.drawable = this.ctx.getResources().getDrawable(R.drawable.bg_square);
                    break;
            }
            return this;
        }

        public Builder drawable(Drawable drawable) {
            this.drawable = drawable;
            return this;
        }

        public Builder drawable(int drawableRes) {
            this.drawable = this.ctx.getResources().getDrawable(drawableRes);
            return this;
        }

        public Builder animation(BalloonAnimation balloonAnimation) {
            this.balloonAnimation = balloonAnimation;
            return this;
        }

        public Builder timeToLive(int milliseconds) {
            this.timeToLive = milliseconds;
            return this;
        }

        public BalloonPopup show() {
            BalloonPopup bp = new BalloonPopup(this.ctx, this.attachView, this.gravity, this.dismissOnTap, this.stayWithinScreenBounds, this.offsetX, this.offsetY, this.bgColor, this.fgColor, this.layoutRes, this.customView, this.text, this.textSize, this.drawable, this.balloonAnimation, this.timeToLive);
            bp.show();
            return bp;
        }
    }

    public BalloonPopup(Context ctx, View attachView, BalloonGravity gravity, boolean dismissOnTap, boolean stayWithinScreenBounds, int offsetX, int offsetY, int bgColor, int fgColor, int layoutRes, View customView, String text, int textSize, Drawable drawable, BalloonAnimation balloonAnimation, int timeToLive) {
        this.ctx = ctx;
        this.attachView = attachView;
        this.gravity = gravity;
        this.dismissOnTap = dismissOnTap;
        this.stayWithinScreenBounds = stayWithinScreenBounds;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
        this.layoutRes = layoutRes;
        this.customView = customView;
        this.text = text;
        this.textSize = textSize;
        this.drawable = drawable;
        this.balloonAnimation = balloonAnimation;
        this.timeToLive = timeToLive;
    }

    public static Builder Builder(Context ctx, View anchorView) {
        return new Builder(ctx, anchorView);
    }

    ScrollView findScrollViewParent(View v) {
        if (v == null) {
            return null;
        }
        if (v instanceof ScrollView) {
            return (ScrollView) v;
        }
        return findScrollViewParent((ViewGroup) v.getParent());
    }

    private void show() {
        if (this.customView != null) {
            this.hostedView = this.customView;
        } else {
            this.hostedView = ((LayoutInflater) this.ctx.getSystemService("layout_inflater")).inflate(this.layoutRes, null);
        }
        if (this.text != null) {
            this.textView = (TextView) this.hostedView.findViewById(R.id.text_view);
            this.textView.setText(this.text);
            this.textView.setTextColor(this.fgColor);
            this.textView.setTextSize(2, (float) this.textSize);
        }
        if (this.popupWindow == null) {
            this.popupWindow = new PopupWindow(this.hostedView, -2, -2);
            if (VERSION.SDK_INT >= 21) {
                this.popupWindow.setElevation(5.0f);
            }
            this.popupWindow.setFocusable(false);
            this.popupWindow.setOutsideTouchable(false);
            this.popupWindow.setTouchable(true);
            this.popupWindow.setClippingEnabled(false);
            if (this.drawable != null) {
                this.drawable.setAlpha(getDrawableAlpha());
                this.popupWindow.setBackgroundDrawable(this.drawable);
                if (VERSION.SDK_INT >= 21) {
                    this.drawable.setTint(this.bgColor);
                }
            }
            switch (this.balloonAnimation) {
                case instantin_fadeout:
                    this.popupWindow.setAnimationStyle(R.style.instantin_fadeout);
                    break;
                case instantin_popout:
                    this.popupWindow.setAnimationStyle(R.style.instantin_popout);
                    break;
                case instantin_scaleout:
                    this.popupWindow.setAnimationStyle(R.style.instantin_scaleout);
                    break;
                case instantin_fade_and_popout:
                    this.popupWindow.setAnimationStyle(R.style.instantin_fade_and_popout);
                    break;
                case instantin_fade_and_scaleout:
                    this.popupWindow.setAnimationStyle(R.style.instantin_fade_and_scaleout);
                    break;
                case pop:
                    this.popupWindow.setAnimationStyle(R.style.pop);
                    break;
                case scale:
                    this.popupWindow.setAnimationStyle(R.style.scale);
                    break;
                case fade:
                    this.popupWindow.setAnimationStyle(R.style.fade);
                    break;
                case fade_and_pop:
                    this.popupWindow.setAnimationStyle(R.style.fade_and_pop);
                    break;
                case fade_and_scale:
                    this.popupWindow.setAnimationStyle(R.style.fade_and_scale);
                    break;
                case fade75:
                    this.popupWindow.setAnimationStyle(R.style.fade75);
                    break;
                case fade75_and_pop:
                    this.popupWindow.setAnimationStyle(R.style.fade75_and_pop);
                    break;
                case fade75_and_scale:
                    this.popupWindow.setAnimationStyle(R.style.fade75_and_scale);
                    break;
                case instantin_fade75out:
                    this.popupWindow.setAnimationStyle(R.style.instantin_fade75out);
                    break;
                case instantin_fade75_and_popout:
                    this.popupWindow.setAnimationStyle(R.style.instantin_fade75_and_popout);
                    break;
                case instantin_fade75_and_scaleout:
                    this.popupWindow.setAnimationStyle(R.style.instantin_fade75_and_scaleout);
                    break;
            }
        }
        if (this.timeToLive > 0) {
            if (this.bDelay == null) {
                this.bDelay = new BDelay((long) this.timeToLive, new Runnable() {
                    public void run() {
                        BalloonPopup.this.kill();
                    }
                });
            } else {
                this.bDelay.updateInterval((long) this.timeToLive);
                this.bDelay.setOnTickHandler(new Runnable() {
                    public void run() {
                        BalloonPopup.this.kill();
                    }
                });
            }
        }
        if (this.dismissOnTap) {
            this.popupWindow.setTouchInterceptor(new OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    BalloonPopup.this.kill();
                    return false;
                }
            });
        }
        draw(true);
    }

    int getDrawableAlpha() {
        if (this.balloonAnimation == BalloonAnimation.fade75 || this.balloonAnimation == BalloonAnimation.fade75_and_pop || this.balloonAnimation == BalloonAnimation.fade75_and_scale || this.balloonAnimation == BalloonAnimation.instantin_fade75_and_popout || this.balloonAnimation == BalloonAnimation.instantin_fade75_and_scaleout || this.balloonAnimation == BalloonAnimation.instantin_fade75out) {
            return 192;
        }
        return 255;
    }

    public void dismiss() {
        kill();
    }

    private void kill() {
        try {
            if (this.popupWindow != null) {
                this.popupWindow.dismiss();
            }
        } catch (Exception e) {
        }
        if (this.bDelay != null) {
            this.bDelay.clear();
        }
    }

    private void draw(final boolean restartLifeTime) {
        int[] loc = new int[2];
        this.attachView.getLocationOnScreen(loc);
        this.attachView.measure(0, 0);
        int widthAttachView = this.attachView.getMeasuredWidth();
        int heightAttachView = this.attachView.getMeasuredHeight();
        if (this.hostedView == null) {
            BDelay bDelay = new BDelay(50, new Runnable() {
                public void run() {
                    BalloonPopup.this.draw(restartLifeTime);
                }
            });
            return;
        }
        this.hostedView.measure(0, 0);
        int widthHostedView = this.hostedView.getMeasuredWidth();
        int heightHostedView = this.hostedView.getMeasuredHeight();
        int posX = loc[0] + this.offsetX;
        switch (this.gravity) {
            case alltop_allleft:
            case halftop_allleft:
            case center_allleft:
            case halfbottom_allleft:
            case allbottom_allleft:
                posX -= widthHostedView;
                break;
            case alltop_halfleft:
            case halftop_halfleft:
            case center_halfleft:
            case halfbottom_halfleft:
            case allbottom_halfleft:
                posX -= widthHostedView / 2;
                break;
            case alltop_center:
            case halftop_center:
            case center:
            case halfbottom_center:
            case allbottom_center:
                posX += (widthAttachView / 2) - (widthHostedView / 2);
                break;
            case alltop_halfright:
            case halftop_halfright:
            case center_halfright:
            case halfbottom_halfright:
            case allbottom_halfright:
                posX += widthAttachView - (widthHostedView / 2);
                break;
            case alltop_allright:
            case halftop_allright:
            case center_allright:
            case halfbottom_allright:
            case allbottom_allright:
                posX += widthAttachView;
                break;
        }
        int posY = loc[1] + this.offsetY;
        switch (this.gravity) {
            case alltop_allleft:
            case alltop_halfleft:
            case alltop_center:
            case alltop_halfright:
            case alltop_allright:
                posY -= heightHostedView;
                break;
            case halftop_allleft:
            case halftop_halfleft:
            case halftop_center:
            case halftop_halfright:
            case halftop_allright:
                posY -= heightHostedView / 2;
                break;
            case center_allleft:
            case center_halfleft:
            case center:
            case center_halfright:
            case center_allright:
                posY += (heightAttachView / 2) - (heightHostedView / 2);
                break;
            case halfbottom_allleft:
            case halfbottom_halfleft:
            case halfbottom_center:
            case halfbottom_halfright:
            case halfbottom_allright:
                posY += heightAttachView - (heightHostedView / 2);
                break;
            case allbottom_allleft:
            case allbottom_halfleft:
            case allbottom_center:
            case allbottom_halfright:
            case allbottom_allright:
                posY += heightAttachView;
                break;
        }
        if (this.stayWithinScreenBounds) {
            posX = Math.max(posX, 0);
            posY = Math.max(posY, 0);
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            posX = Math.min(metrics.widthPixels - widthHostedView, posX);
            posY = Math.min(metrics.heightPixels - heightHostedView, posY);
        }
        if (restartLifeTime && this.popupWindow.isShowing()) {
            this.popupWindow.update(posX, posY, this.popupWindow.getWidth(), this.popupWindow.getHeight());
            if (this.bDelay == null) {
                this.bDelay = new BDelay((long) this.timeToLive, new Runnable() {
                    public void run() {
                        BalloonPopup.this.kill();
                    }
                });
                return;
            } else if (this.timeToLive == 0) {
                this.bDelay.clear();
                return;
            } else {
                this.bDelay.updateInterval((long) this.timeToLive);
                return;
            }
        }
        this.attachView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if (BalloonPopup.this.popupWindow.isShowing()) {
                    BalloonPopup.this.draw(true);
                }
            }
        });
        final int x = posX;
        final int y = posY;
        this.attachView.post(new Runnable() {
            public void run() {
                BalloonPopup.this.popupWindow.showAtLocation(BalloonPopup.this.attachView, 0, x, y);
            }
        });
    }

    public boolean isShowing() {
        if (this.popupWindow == null) {
            return false;
        }
        return this.popupWindow.isShowing();
    }

    public void updateOffset(int newOffsetX, int newOffsetY, boolean restartLifeTime) {
        this.offsetX = newOffsetX;
        this.offsetY = newOffsetY;
        draw(restartLifeTime);
    }

    public void updateOffset(int newOffsetX, int newOffsetY) {
        updateOffset(newOffsetX, newOffsetY, true);
    }

    public void updateGravity(BalloonGravity gravity, boolean restartLifeTime) {
        this.gravity = gravity;
        draw(restartLifeTime);
    }

    public void updateGravity(BalloonGravity gravity) {
        updateGravity(gravity, true);
    }

    public void updateText(String newText, boolean restartLifeTime) {
        this.text = newText;
        this.textView.setText(this.text);
        draw(restartLifeTime);
    }

    public void updateText(String newText) {
        updateText(newText, true);
    }

    public void updateText(int newTextRes, boolean restartLifeTime) {
        updateText(this.ctx.getResources().getString(newTextRes), restartLifeTime);
    }

    public void updateText(int newTextRes) {
        updateText(this.ctx.getResources().getString(newTextRes), true);
    }

    public void updateTextSize(int textSize, boolean restartLifeTime) {
        this.textSize = textSize;
        this.textView.setTextSize((float) textSize);
        draw(restartLifeTime);
    }

    public void updateTextSize(int textSize) {
        updateTextSize(textSize, true);
    }

    public void updateFgColor(int fgColor, boolean restartLifeTime) {
        this.fgColor = fgColor;
        this.textView.setTextColor(fgColor);
        draw(restartLifeTime);
    }

    public void updateFgColor(int fgColor) {
        updateFgColor(fgColor, true);
    }

    public void updateBgColor(int bgColor, boolean restartLifeTime) {
        if (VERSION.SDK_INT >= 21 && this.drawable != null) {
            this.bgColor = bgColor;
            this.drawable.setTint(bgColor);
            draw(restartLifeTime);
        }
    }

    public void updateBgColor(int bgColor) {
        updateBgColor(bgColor, true);
    }

    public void updateLifeTimeToLive(int milliseconds, boolean restartLifeTime) {
        this.timeToLive = milliseconds;
        draw(restartLifeTime);
    }

    public void updateLifeTimeToLive(int milliseconds) {
        updateLifeTimeToLive(milliseconds, true);
    }

    public void restartLifeTime() {
        if (!this.popupWindow.isShowing()) {
            return;
        }
        if (this.bDelay == null) {
            this.bDelay = new BDelay((long) this.timeToLive, new Runnable() {
                public void run() {
                    BalloonPopup.this.kill();
                }
            });
        } else if (this.timeToLive == 0) {
            this.bDelay.clear();
        } else {
            this.bDelay.updateInterval((long) this.timeToLive);
        }
    }

    public void showAgain() {
        if (this.popupWindow.isShowing()) {
            restartLifeTime();
        } else {
            draw(true);
        }
    }
}
