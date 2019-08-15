package com.chogge.speaker.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewParent;
import com.chogge.speaker.R;
import com.chogge.speaker.utils.MyUtils;
import com.chogge.speaker.view.BalloonPopup.BalloonAnimation;
import com.chogge.speaker.view.BalloonPopup.BalloonGravity;
import com.chogge.speaker.view.BalloonPopup.BalloonShape;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

public class Knob extends View {
    public static final int BALLONANIMATION_FADE = 2;
    public static final int BALLONANIMATION_POP = 0;
    public static final int BALLONANIMATION_SCALE = 1;
    public static final int ONCLICK_MENU = 4;
    public static final int ONCLICK_NEXT = 1;
    public static final int ONCLICK_NONE = 0;
    public static final int ONCLICK_PREV = 2;
    public static final int ONCLICK_RESET = 3;
    public static final int ONCLICK_USER = 5;
    public static final int SWIPEDIRECTION_CIRCULAR = 4;
    public static final int SWIPEDIRECTION_HORIZONTAL = 2;
    public static final int SWIPEDIRECTION_HORIZONTALVERTICAL = 3;
    public static final int SWIPEDIRECTION_NONE = 0;
    public static final int SWIPEDIRECTION_VERTICAL = 1;
    private int actualState = this.currentState;
    private boolean animation = true;
    private float animationBounciness = 40.0f;
    private float animationSpeed = 10.0f;
    private BalloonPopup balloonPopup;
    private int balloonValuesAnimation = 0;
    private CharSequence[] balloonValuesArray = null;
    private float balloonValuesRelativePosition = 1.3f;
    private boolean balloonValuesSlightlyTransparent = true;
    private float balloonValuesTextSize = 9.0f;
    private int balloonValuesTimeToLive = 400;
    private int borderColor = ViewCompat.MEASURED_STATE_MASK;
    private int borderWidth = 2;
    private float centerX;
    private float centerY;
    private int circularIndicatorColor = ViewCompat.MEASURED_STATE_MASK;
    private float circularIndicatorRelativePosition = 0.7f;
    private float circularIndicatorRelativeRadius = 0.0f;
    private int clickBehaviour = 1;
    private Context ctx;
    private double currentAngle;
    private int currentState = this.defaultState;
    private int defaultState = 0;
    private boolean enabled = true;
    private float externalRadius;
    private boolean freeRotation = true;
    private int indicatorColor = ViewCompat.MEASURED_STATE_MASK;
    private float indicatorRelativeLength = 0.5f;
    private int indicatorWidth = 6;
    private int knobCenterColor = getResources().getColor(R.color.colorAccent);
    private float knobCenterRelativeRadius = 0.45f;
    private int knobColor = getResources().getColor(R.color.colorPrimary);
    private Drawable knobDrawable;
    private int knobDrawableRes = 0;
    private boolean knobDrawableRotates = true;
    private float knobRadius;
    private float knobRelativeRadius = 0.8f;
    private OnStateChanged listener;
    private float maxAngle = 360.0f;
    private float minAngle = 0.0f;
    private int numberOfStates = 9;
    private Paint paint;
    private int previousState = this.defaultState;
    private int selectedStateMarkerColor = InputDeviceCompat.SOURCE_ANY;
    private boolean selectedStateMarkerContinuous = false;
    private boolean showBalloonValues = false;
    Spring spring;
    SpringSystem springSystem;
    private int stateMarkersAccentColor = ViewCompat.MEASURED_STATE_MASK;
    private int stateMarkersAccentPeriodicity = 0;
    private float stateMarkersAccentRelativeLength = 0.11f;
    private int stateMarkersAccentWidth = 3;
    private int stateMarkersColor = ViewCompat.MEASURED_STATE_MASK;
    private float stateMarkersRelativeLength = 0.06f;
    private int stateMarkersWidth = 2;
    private int swipeDirection = 4;
    private int swipeSensibilityPixels = 100;
    private int swipeX = 0;
    private int swipeY = 0;
    boolean swipeing = false;
    private Runnable userRunnable = null;

    public interface OnStateChanged {
        void onState(int i);
    }

    public Knob(Context context) {
        super(context);
        init(null);
    }

    public Knob(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public Knob(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = 21)
    public Knob(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Resources r = Resources.getSystem();
        if (widthMode == 0 || widthMode == Integer.MIN_VALUE) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) TypedValue.applyDimension(1, 50.0f, r.getDisplayMetrics()), MeasureSpec.EXACTLY);
        }
        if (heightMode == 0 || heightSize == Integer.MIN_VALUE) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) TypedValue.applyDimension(1, 30.0f, r.getDisplayMetrics()), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int raidus;

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        raidus = (int) (getWidth() / 2 - MyUtils.dp2Px(1, getContext()));
        this.externalRadius = ((float) Math.min(width, height)) * 0.32f;
        this.knobRadius = this.externalRadius * this.knobRelativeRadius;
        this.centerX = (float) (width / 2);
        this.centerY = (float) (height / 2);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paintKnob(canvas);
    //    paintMarkers(canvas);
    //    paintIndicator(canvas);

        /*this.paint.setStyle(Style.STROKE);
        this.paint.setColor(Color.parseColor("#FFFFFF"));
        this.paint.setStrokeWidth((float) this.indicatorWidth);
        canvas.drawCircle(this.centerX, this.centerY, raidus, this.paint);*/

    //    paintCircularIndicator(canvas);
    //    paintKnobCenter(canvas);
    //    paintKnobBorder(canvas);
        //displayBalloons();
    }

    void paintKnob(Canvas canvas) {
        if (this.knobDrawableRes == 0 || this.knobDrawable == null) {
            this.paint.setColor(this.knobColor);
            this.paint.setStyle(Style.FILL);
            canvas.drawCircle(this.centerX, this.centerY, this.knobRadius, this.paint);
            return;
        }
        this.knobDrawable.setBounds((int) (this.centerX - this.knobRadius), (int) (this.centerY - this.knobRadius), (int) (this.centerX + this.knobRadius), (int) (this.centerY + this.knobRadius));
        if (this.knobDrawableRotates) {
            canvas.save();
            canvas.rotate((float) (-Math.toDegrees(3.141592653589793d + this.currentAngle)), this.centerX, this.centerY);
            this.knobDrawable.draw(canvas);
            canvas.restore();
            return;
        }
        this.knobDrawable.draw(canvas);
    }

    void paintKnobBorder(Canvas canvas) {
        if (this.borderWidth != 0) {
            this.paint.setColor(this.borderColor);
            this.paint.setStyle(Style.STROKE);
            this.paint.setStrokeWidth((float) this.borderWidth);
            canvas.drawCircle(this.centerX, this.centerY, this.knobRadius, this.paint);
        }
    }

    void paintKnobCenter(Canvas canvas) {
        if ((this.knobDrawableRes == 0 || this.knobDrawable == null) && this.knobCenterRelativeRadius != 0.0f) {
            this.paint.setColor(this.knobCenterColor);
            this.paint.setStyle(Style.FILL);
            canvas.drawCircle(this.centerX, this.centerY, this.knobCenterRelativeRadius * this.knobRadius, this.paint);
        }
    }

    double normalizeAngle(double angle) {
        while (angle < 0.0d) {
            angle += 6.283185307179586d;
        }
        while (angle >= 6.283185307179586d) {
            angle -= 6.283185307179586d;
        }
        return angle;
    }

    double calcAngle(int position) {
        double min = Math.toRadians((double) this.minAngle);
        double range = Math.toRadians(((double) this.maxAngle) - 1.0E-4d) - min;
        if (this.numberOfStates <= 1) {
            return 0.0d;
        }
        double singleStepAngle = range / ((double) (this.numberOfStates - 1));
        if (6.283185307179586d - range < singleStepAngle) {
            singleStepAngle = range / ((double) this.numberOfStates);
        }
        return normalizeAngle((3.141592653589793d - min) - (((double) position) * singleStepAngle));
    }

    void setIndicatorAngleWithDirection() {
        double angleCurr = normalizeAngle(this.spring.getCurrentValue());
        double angleNew = calcAngle(this.actualState);
        if (this.freeRotation) {
            if (angleCurr > angleNew && angleCurr - angleNew > 3.141592653589793d) {
                angleNew += 6.283185307179586d;
            } else if (angleCurr < angleNew && angleNew - angleCurr > 3.141592653589793d) {
                angleNew -= 6.283185307179586d;
            }
        }
        this.spring.setCurrentValue(angleCurr);
        this.spring.setEndValue(angleNew);
    }

    void paintIndicator(Canvas canvas) {
        if (this.indicatorWidth != 0 && this.indicatorRelativeLength != 0.0f) {
            this.paint.setColor(this.indicatorColor);
            this.paint.setStrokeWidth((float) this.indicatorWidth);
            canvas.drawLine(this.centerX + ((float) (((double) (this.knobRadius * (1.0f - this.indicatorRelativeLength))) * Math.sin(this.currentAngle))),
                    this.centerY + ((float) (((double) (this.knobRadius * (1.0f - this.indicatorRelativeLength))) * Math.cos(this.currentAngle))),
                    this.centerX + ((float) (((double) this.knobRadius) * Math.sin(this.currentAngle))),
                    this.centerY + ((float) (((double) this.knobRadius) * Math.cos(this.currentAngle))), this.paint);
        }
    }

    void paintCircularIndicator(Canvas canvas) {
        if (this.circularIndicatorRelativeRadius != 0.0f) {
            this.paint.setColor(this.circularIndicatorColor);
            this.paint.setStrokeWidth(0.0f);
            this.paint.setStyle(Style.FILL);
            canvas.drawCircle(this.centerX + ((float) (((double) (this.externalRadius * this.circularIndicatorRelativePosition)) * Math.sin(this.currentAngle))), this.centerY + ((float) (((double) (this.externalRadius * this.circularIndicatorRelativePosition)) * Math.cos(this.currentAngle))), this.externalRadius * this.circularIndicatorRelativeRadius, this.paint);
        }
    }

    void paintMarkers(Canvas canvas) {
        if ((this.stateMarkersRelativeLength != 0.0f && this.stateMarkersWidth != 0) || (this.stateMarkersAccentRelativeLength != 0.0f && this.stateMarkersAccentWidth != 0)) {
            int w = 0;
            while (w < this.numberOfStates) {
                boolean big = false;
                if (this.stateMarkersAccentPeriodicity != 0) {
                    big = w % this.stateMarkersAccentPeriodicity == 0;
                }
                boolean selected = w == this.actualState || (w <= this.actualState && this.selectedStateMarkerContinuous);
                this.paint.setStrokeWidth(big ? (float) this.stateMarkersAccentWidth : (float) this.stateMarkersWidth);
                double angle = calcAngle(w);
                float startX = this.centerX + ((float) (((double) ((1.0f - (big ? this.stateMarkersAccentRelativeLength : this.stateMarkersRelativeLength)) * this.externalRadius) - MyUtils.dp2Px(3, getContext())) * Math.sin(angle)));
                float startY = this.centerY + ((float) (((double) ((1.0f - (big ? this.stateMarkersAccentRelativeLength : this.stateMarkersRelativeLength)) * this.externalRadius) - MyUtils.dp2Px(3, getContext())) * Math.cos(angle)));
                float endX = this.centerX + ((float) (((double) this.externalRadius - MyUtils.dp2Px(3, getContext())) * Math.sin(angle)));
                float endY = this.centerY + ((float) (((double) this.externalRadius - MyUtils.dp2Px(3, getContext())) * Math.cos(angle)));
                Paint paint = this.paint;
                int i = selected ? this.selectedStateMarkerColor : big ? this.stateMarkersAccentColor : this.stateMarkersColor;
                paint.setColor(i);
                canvas.drawLine(startX, startY, endX, endY, this.paint);
                w++;
            }
        }
    }

    int balloonsX() {
        return (int) (this.centerX + ((float) (((double) (this.externalRadius * this.balloonValuesRelativePosition)) * Math.sin(this.currentAngle))));
    }

    int balloonsY() {
        return (int) (this.centerY + ((float) (((double) (this.externalRadius * this.balloonValuesRelativePosition)) * Math.cos(this.currentAngle))));
    }

    String balloonText() {
        if (this.balloonValuesArray == null) {
            return Integer.toString(this.actualState);
        }
        return this.balloonValuesArray[this.actualState].toString();
    }

    void displayBalloons() {
        if (!this.showBalloonValues) {
            return;
        }
        if (this.balloonPopup == null || !this.balloonPopup.isShowing()) {
            this.balloonPopup = BalloonPopup.Builder(this.ctx, this).text(balloonText()).gravity(BalloonGravity.halftop_halfleft).offsetX(balloonsX()).offsetY(balloonsY()).textSize((int) this.balloonValuesTextSize).shape(BalloonShape.rounded_square).timeToLive(this.balloonValuesTimeToLive).animation(getBalloonAnimation()).stayWithinScreenBounds(true).show();
            return;
        }
        this.balloonPopup.updateOffset(balloonsX(), balloonsY(), true);
        this.balloonPopup.updateText(balloonText(), true);
        this.balloonPopup.updateTextSize((int) this.balloonValuesTextSize, true);
    }

    BalloonAnimation getBalloonAnimation() {
        if (this.balloonValuesAnimation == 0 && this.balloonValuesSlightlyTransparent) {
            return BalloonAnimation.fade75_and_pop;
        }
        if (this.balloonValuesAnimation == 0) {
            return BalloonAnimation.fade_and_pop;
        }
        if (this.balloonValuesAnimation == 1 && this.balloonValuesSlightlyTransparent) {
            return BalloonAnimation.fade75_and_scale;
        }
        if (this.balloonValuesAnimation == 1) {
            return BalloonAnimation.fade_and_scale;
        }
        if (this.balloonValuesAnimation == 2 && this.balloonValuesSlightlyTransparent) {
            return BalloonAnimation.fade75;
        }
        return BalloonAnimation.fade;
    }

    void init(AttributeSet attrs) {
        this.ctx = getContext();
        loadAttributes(attrs);
        initTools();
        initDrawables();
        initBalloons();
        initListeners();
        initStatus();
    }

    void initTools() {
        this.paint = new Paint(1);
        this.paint.setStrokeCap(Cap.ROUND);
        this.springSystem = SpringSystem.create();
        this.spring = this.springSystem.createSpring();
        this.spring.setSpringConfig(SpringConfig.fromBouncinessAndSpeed((double) this.animationSpeed, (double) this.animationBounciness));
        this.spring.setOvershootClampingEnabled(false);
    }

    void initDrawables() {
        if (this.knobDrawableRes != 0) {
            this.knobDrawable = getResources().getDrawable(this.knobDrawableRes);
        }
    }

    void loadAttributes(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = this.ctx.obtainStyledAttributes(attrs, R.styleable.Knob);
            this.numberOfStates = typedArray.getInt(R.styleable.Knob_kNumberOfStates, this.numberOfStates);
            this.defaultState = typedArray.getInt(R.styleable.Knob_kDefaultState, this.defaultState);
            this.borderWidth = typedArray.getDimensionPixelSize(R.styleable.Knob_kBorderWidth, this.borderWidth);
            this.borderColor = typedArray.getColor(R.styleable.Knob_kBorderColor, this.borderColor);
            this.indicatorWidth = typedArray.getDimensionPixelSize(R.styleable.Knob_kIndicatorWidth, this.indicatorWidth);
            this.indicatorColor = typedArray.getColor(R.styleable.Knob_kIndicatorColor, this.indicatorColor);
            this.indicatorRelativeLength = typedArray.getFloat(R.styleable.Knob_kIndicatorRelativeLength, this.indicatorRelativeLength);
            this.circularIndicatorRelativeRadius = typedArray.getFloat(R.styleable.Knob_kCircularIndicatorRelativeRadius, this.circularIndicatorRelativeRadius);
            this.circularIndicatorRelativePosition = typedArray.getFloat(R.styleable.Knob_kCircularIndicatorRelativePosition, this.circularIndicatorRelativePosition);
            this.circularIndicatorColor = typedArray.getColor(R.styleable.Knob_kCircularIndicatorColor, this.circularIndicatorColor);
            this.knobColor = typedArray.getColor(R.styleable.Knob_kKnobColor, this.knobColor);
            this.knobRelativeRadius = typedArray.getFloat(R.styleable.Knob_kKnobRelativeRadius, this.knobRelativeRadius);
            this.knobCenterRelativeRadius = typedArray.getFloat(R.styleable.Knob_kKnobCenterRelativeRadius, this.knobCenterRelativeRadius);
            this.knobCenterColor = typedArray.getColor(R.styleable.Knob_kKnobCenterColor, this.knobCenterColor);
            this.knobDrawableRes = typedArray.getResourceId(R.styleable.Knob_kKnobDrawable, this.knobDrawableRes);
            this.knobDrawableRotates = typedArray.getBoolean(R.styleable.Knob_kKnobDrawableRotates, this.knobDrawableRotates);
            this.stateMarkersWidth = typedArray.getDimensionPixelSize(R.styleable.Knob_kStateMarkersWidth, this.stateMarkersWidth);
            this.stateMarkersColor = typedArray.getColor(R.styleable.Knob_kStateMarkersColor, this.stateMarkersColor);
            this.selectedStateMarkerColor = typedArray.getColor(R.styleable.Knob_kSelectedStateMarkerColor, this.selectedStateMarkerColor);
            this.stateMarkersRelativeLength = typedArray.getFloat(R.styleable.Knob_kStateMarkersRelativeLength, this.stateMarkersRelativeLength);
            this.selectedStateMarkerContinuous = typedArray.getBoolean(R.styleable.Knob_kSelectedStateMarkerContinuous, this.selectedStateMarkerContinuous);
            this.animation = typedArray.getBoolean(R.styleable.Knob_kAnimation, this.animation);
            this.animationSpeed = typedArray.getFloat(R.styleable.Knob_kAnimationSpeed, this.animationSpeed);
            this.animationBounciness = typedArray.getFloat(R.styleable.Knob_kAnimationBounciness, this.animationBounciness);
        //    this.swipeDirection = swipeAttrToInt(typedArray.getString(R.styleable.Knob_swipe));
            this.swipeSensibilityPixels = typedArray.getInt(R.styleable.Knob_kSwipeSensitivityPixels, this.swipeSensibilityPixels);
            this.freeRotation = typedArray.getBoolean(R.styleable.Knob_kFreeRotation, this.freeRotation);
            this.minAngle = typedArray.getFloat(R.styleable.Knob_kMinAngle, this.minAngle);
            this.maxAngle = typedArray.getFloat(R.styleable.Knob_kMaxAngle, this.maxAngle);
            this.stateMarkersAccentWidth = typedArray.getDimensionPixelSize(R.styleable.Knob_kStateMarkersWidth, this.stateMarkersAccentWidth);
            this.stateMarkersAccentColor = typedArray.getColor(R.styleable.Knob_kStateMarkersAccentColor, this.stateMarkersAccentColor);
            this.stateMarkersAccentRelativeLength = typedArray.getFloat(R.styleable.Knob_kStateMarkersRelativeLength, this.stateMarkersAccentRelativeLength);
            this.stateMarkersAccentPeriodicity = typedArray.getInt(R.styleable.Knob_kStateMarkersAccentPeriodicity, this.stateMarkersAccentPeriodicity);
            this.showBalloonValues = typedArray.getBoolean(R.styleable.Knob_kShowBalloonValues, this.showBalloonValues);
            this.balloonValuesTimeToLive = typedArray.getInt(R.styleable.Knob_kBalloonValuesTimeToLive, this.balloonValuesTimeToLive);
            this.balloonValuesRelativePosition = typedArray.getFloat(R.styleable.Knob_kBalloonValuesRelativePosition, this.balloonValuesRelativePosition);
            this.balloonValuesTextSize = typedArray.getDimension(R.styleable.Knob_kBalloonValuesTextSize, this.balloonValuesTextSize);
            this.balloonValuesAnimation = balloonAnimationAttrToInt(typedArray.getString(R.styleable.Knob_kBalloonValuesAnimation));
            this.balloonValuesArray = typedArray.getTextArray(R.styleable.Knob_kBalloonValuesArray);
            this.balloonValuesSlightlyTransparent = typedArray.getBoolean(R.styleable.Knob_kBalloonValuesSlightlyTransparent, this.balloonValuesSlightlyTransparent);
            this.clickBehaviour = clickAttrToInt(typedArray.getString(R.styleable.Knob_kClickBehaviour));
            this.enabled = typedArray.getBoolean(R.styleable.Knob_kEnabled, this.enabled);
            typedArray.recycle();
        }
    }

    int swipeAttrToInt(String s) {
        if (s == null) {
            return 4;
        }
        if (s.equals("0")) {
            return 0;
        }
        if (s.equals("1")) {
            return 1;
        }
        if (s.equals("2")) {
            return 2;
        }
        if (s.equals("3")) {
            return 3;
        }
        if (s.equals("4")) {
        }
        return 4;
    }

    int clickAttrToInt(String s) {
        if (s == null) {
            return 1;
        }
        if (s.equals("0")) {
            return 0;
        }
        if (s.equals("1")) {
            return 1;
        }
        if (s.equals("2")) {
            return 2;
        }
        if (s.equals("3")) {
            return 3;
        }
        if (s.equals("4")) {
            return 4;
        }
        if (s.equals("5")) {
            return 5;
        }
        return 1;
    }

    int balloonAnimationAttrToInt(String s) {
        if (s == null || s.equals("0")) {
            return 0;
        }
        if (s.equals("1")) {
            return 1;
        }
        if (s.equals("2")) {
            return 2;
        }
        return 0;
    }

    private void disallowParentToHandleTouchEvents() {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    void clickMe(View view) {
        switch (this.clickBehaviour) {
            case 1:
                toggle(this.animation);
                return;
            case 2:
                inverseToggle(this.animation);
                return;
            case 3:
                revertToDefault(this.animation);
                return;
            case 4:
                createPopupMenu(view);
                return;
            case 5:
                runUserBehaviour();
                return;
            default:
                return;
        }
    }

    void initListeners() {
        setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (Knob.this.enabled) {
                    Knob.this.clickMe(view);
                }
            }
        });
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!Knob.this.enabled) {
                    return false;
                }
                if (Knob.this.swipeDirection == 0) {
                    Knob.this.toggle(Knob.this.animation);
                    return false;
                }
                int action = motionEvent.getAction();
                int y;
                int x;
                if (Knob.this.swipeDirection == 1) {
                    y = (int) motionEvent.getY();
                    if (action == 0) {
                        Knob.this.swipeY = y;
                        Knob.this.swipeing = false;
                        Knob.this.disallowParentToHandleTouchEvents();
                        return false;
                    } else if (action == 2) {
                        if (y - Knob.this.swipeY > Knob.this.swipeSensibilityPixels) {
                            Knob.this.swipeY = y;
                            Knob.this.swipeing = true;
                            Knob.this.decreaseValue();
                            return true;
                        } else if (Knob.this.swipeY - y <= Knob.this.swipeSensibilityPixels) {
                            return false;
                        } else {
                            Knob.this.swipeY = y;
                            Knob.this.swipeing = true;
                            Knob.this.increaseValue();
                            return true;
                        }
                    } else if (action != 1) {
                        return false;
                    } else {
                        if (!Knob.this.swipeing) {
                            Knob.this.clickMe(view);
                        }
                        return true;
                    }
                } else if (Knob.this.swipeDirection == 2) {
                    x = (int) motionEvent.getX();
                    if (action == 0) {
                        Knob.this.swipeX = x;
                        Knob.this.swipeing = false;
                        Knob.this.disallowParentToHandleTouchEvents();
                        return false;
                    } else if (action == 2) {
                        if (x - Knob.this.swipeX > Knob.this.swipeSensibilityPixels) {
                            Knob.this.swipeX = x;
                            Knob.this.swipeing = true;
                            Knob.this.increaseValue();
                            return true;
                        } else if (Knob.this.swipeX - x <= Knob.this.swipeSensibilityPixels) {
                            return false;
                        } else {
                            Knob.this.swipeX = x;
                            Knob.this.swipeing = true;
                            Knob.this.decreaseValue();
                            return true;
                        }
                    } else if (action != 1) {
                        return false;
                    } else {
                        if (!Knob.this.swipeing) {
                            Knob.this.clickMe(view);
                        }
                        return true;
                    }
                } else if (Knob.this.swipeDirection == 3) {
                    x = (int) motionEvent.getX();
                    y = (int) motionEvent.getY();
                    if (action == 0) {
                        Knob.this.swipeX = x;
                        Knob.this.swipeY = y;
                        Knob.this.swipeing = false;
                        Knob.this.disallowParentToHandleTouchEvents();
                        return false;
                    } else if (action == 2) {
                        if (x - Knob.this.swipeX > Knob.this.swipeSensibilityPixels || Knob.this.swipeY - y > Knob.this.swipeSensibilityPixels) {
                            Knob.this.swipeX = x;
                            Knob.this.swipeY = y;
                            Knob.this.swipeing = true;
                            Knob.this.increaseValue();
                            return true;
                        } else if (Knob.this.swipeX - x <= Knob.this.swipeSensibilityPixels && y - Knob.this.swipeY <= Knob.this.swipeSensibilityPixels) {
                            return false;
                        } else {
                            Knob.this.swipeX = x;
                            Knob.this.swipeY = y;
                            Knob.this.swipeing = true;
                            Knob.this.decreaseValue();
                            return true;
                        }
                    } else if (action != 1) {
                        return false;
                    } else {
                        if (!Knob.this.swipeing) {
                            Knob.this.clickMe(view);
                        }
                        return true;
                    }
                } else if (Knob.this.swipeDirection != 4) {
                    return false;
                } else {
                    x = (int) motionEvent.getX();
                    y = (int) motionEvent.getY();
                    if (action == 0) {
                        Knob.this.swipeing = false;
                        Knob.this.disallowParentToHandleTouchEvents();
                        return false;
                    } else if (action == 2) {
                        double angle = Math.atan2((double) (((float) y) - Knob.this.centerY), (double) (((float) x) - Knob.this.centerX));
                        Knob.this.swipeing = true;
                        Knob.this.setValueByAngle(angle, Knob.this.animation);
                        return true;
                    } else if (action != 1) {
                        return false;
                    } else {
                        if (!Knob.this.swipeing) {
                            Knob.this.clickMe(view);
                        }
                        return true;
                    }
                }
            }
        });
        this.spring.addListener(new SimpleSpringListener() {
            public void onSpringUpdate(Spring spring) {
                Knob.this.currentAngle = spring.getCurrentValue();
                Knob.this.postInvalidate();
            }
        });
    }

    void createPopupMenu(View view) {
        PopupMenu mPopupMenu = new PopupMenu(getContext(), view);
        int w;
        if (this.balloonValuesArray == null) {
            for (w = 0; w < this.numberOfStates; w++) {
                mPopupMenu.getMenu().add(0, w + 1, w + 1, Integer.toString(w));
            }
        } else {
            for (w = 0; w < this.numberOfStates; w++) {
                mPopupMenu.getMenu().add(0, w + 1, w + 1, this.balloonValuesArray[w].toString());
            }
        }
        mPopupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Knob.this.setState(item.getItemId() - 1);
                return true;
            }
        });
        mPopupMenu.show();
    }

    void initStatus() {
        this.currentState = this.defaultState;
        this.previousState = this.defaultState;
        calcActualState();
        this.currentAngle = calcAngle(this.currentState);
        this.spring.setCurrentValue(this.currentAngle);
    }

    void initBalloons() {
    }

    public void toggle(boolean animate) {
        increaseValue(animate);
    }

    public void toggle() {
        toggle(this.animation);
    }

    public void inverseToggle(boolean animate) {
        decreaseValue(animate);
    }

    public void inverseToggle() {
        inverseToggle(this.animation);
    }

    public void revertToDefault(boolean animate) {
        setState(this.defaultState, animate);
    }

    public void revertToDefault() {
        revertToDefault(this.animation);
    }

    private void calcActualState() {
        this.actualState = this.currentState % this.numberOfStates;
        if (this.actualState < 0) {
            this.actualState += this.numberOfStates;
        }
    }

    public void increaseValue(boolean animate) {
        this.previousState = this.currentState;
        this.currentState++;
        if (!this.freeRotation && this.currentState >= this.numberOfStates) {
            this.currentState = this.numberOfStates - 1;
        }
        calcActualState();
        if (this.listener != null) {
            this.listener.onState(this.actualState);
        }
        takeEffect(animate);
    }

    public void increaseValue() {
        increaseValue(this.animation);
    }

    public void decreaseValue(boolean animate) {
        this.previousState = this.currentState;
        this.currentState--;
        if (!this.freeRotation && this.currentState < 0) {
            this.currentState = 0;
        }
        calcActualState();
        if (this.listener != null) {
            this.listener.onState(this.actualState);
        }
        takeEffect(animate);
    }

    public void decreaseValue() {
        decreaseValue(this.animation);
    }

    public void setValueByAngle(double angle, boolean animate) {
        if (this.numberOfStates > 1) {
            this.previousState = this.currentState;
            double min = Math.toRadians((double) this.minAngle);
            double max = Math.toRadians(((double) this.maxAngle) - 1.0E-4d);
            double range = max - min;
            double singleStepAngle = range / ((double) this.numberOfStates);
            if (6.283185307179586d - range < singleStepAngle) {
                singleStepAngle = range / ((double) this.numberOfStates);
            }
            min = (double) ((float) normalizeAngle(min));
            while (min > max) {
                max += 6.283185307179586d;
            }
            angle = normalizeAngle(1.5707963267948966d + angle);
            while (angle < min) {
                angle += 6.283185307179586d;
            }
            if (angle > max) {
                if (angle - max > (min - angle) + 6.283185307179586d) {
                    angle = min;
                } else {
                    angle = max;
                }
            }
            this.currentState = (int) ((angle - min) / singleStepAngle);
            if (!this.freeRotation && Math.abs(this.currentState - this.previousState) == this.numberOfStates - 1) {
                this.currentState = this.previousState;
            }
            calcActualState();
            if (this.listener != null) {
                this.listener.onState(this.actualState);
            }
            takeEffect(animate);
        }
    }

    private void takeEffect(boolean animate) {
        if (animate) {
            setIndicatorAngleWithDirection();
        } else {
            this.spring.setCurrentValue(calcAngle(this.actualState));
        }
        postInvalidate();
    }

    public void setOnStateChanged(OnStateChanged onStateChanged) {
        this.listener = onStateChanged;
    }

    public void setState(int newState) {
        setState(newState, this.animation);
    }

    public void setState(int newState, boolean animate) {
        forceState(newState, animate);
        if (this.listener != null) {
            this.listener.onState(this.currentState);
        }
    }

    public void forceState(int newState) {
        forceState(newState, this.animation);
    }

    public void forceState(int newState, boolean animate) {
        this.previousState = this.currentState;
        this.currentState = newState;
        calcActualState();
        takeEffect(animate);
    }

    public int getState() {
        return this.actualState;
    }

    public int getNumberOfStates() {
        return this.numberOfStates;
    }

    public void setNumberOfStates(int numberOfStates) {
        setNumberOfStates(numberOfStates, this.animation);
    }

    public void setNumberOfStates(int numberOfStates, boolean animate) {
        this.numberOfStates = numberOfStates;
        takeEffect(animate);
    }

    public int getDefaultState() {
        return this.defaultState;
    }

    public void setDefaultState(int defaultState) {
        this.defaultState = defaultState;
    }

    public int getBorderWidth() {
        return this.borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        takeEffect(this.animation);
    }

    public int getBorderColor() {
        return this.borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        takeEffect(this.animation);
    }

    public int getIndicatorWidth() {
        return this.indicatorWidth;
    }

    public void setIndicatorWidth(int indicatorWidth) {
        this.indicatorWidth = indicatorWidth;
        takeEffect(this.animation);
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        takeEffect(this.animation);
    }

    public float getIndicatorRelativeLength() {
        return this.indicatorRelativeLength;
    }

    public void setIndicatorRelativeLength(float indicatorRelativeLength) {
        this.indicatorRelativeLength = indicatorRelativeLength;
        takeEffect(this.animation);
    }

    public int getKnobColor() {
        return this.knobColor;
    }

    public void setKnobColor(int knobColor) {
        this.knobColor = knobColor;
        takeEffect(this.animation);
    }

    public float getKnobRelativeRadius() {
        return this.knobRelativeRadius;
    }

    public void setKnobRelativeRadius(float knobRelativeRadius) {
        this.knobRelativeRadius = knobRelativeRadius;
        takeEffect(this.animation);
    }

    public float getKnobCenterRelativeRadius() {
        return this.knobCenterRelativeRadius;
    }

    public void setKnobCenterRelativeRadius(float knobCenterRelativeRadius) {
        this.knobCenterRelativeRadius = knobCenterRelativeRadius;
        takeEffect(this.animation);
    }

    public int getKnobCenterColor() {
        return this.knobCenterColor;
    }

    public void setKnobCenterColor(int knobCenterColor) {
        this.knobCenterColor = knobCenterColor;
        takeEffect(this.animation);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        takeEffect(this.animation);
    }

    public boolean isAnimation() {
        return this.animation;
    }

    public void setAnimation(boolean animation) {
        this.animation = animation;
    }

    public float getAnimationSpeed() {
        return this.animationSpeed;
    }

    public void setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public float getAnimationBounciness() {
        return this.animationBounciness;
    }

    public void setAnimationBounciness(float animationBounciness) {
        this.animationBounciness = animationBounciness;
    }

    public int getStateMarkersWidth() {
        return this.stateMarkersWidth;
    }

    public void setStateMarkersWidth(int stateMarkersWidth) {
        this.stateMarkersWidth = stateMarkersWidth;
        takeEffect(this.animation);
    }

    public int getStateMarkersColor() {
        return this.stateMarkersColor;
    }

    public void setStateMarkersColor(int stateMarkersColor) {
        this.stateMarkersColor = stateMarkersColor;
        takeEffect(this.animation);
    }

    public int getSelectedStateMarkerColor() {
        return this.selectedStateMarkerColor;
    }

    public void setSelectedStateMarkerColor(int selectedStateMarkerColor) {
        this.selectedStateMarkerColor = selectedStateMarkerColor;
        takeEffect(this.animation);
    }

    public float getStateMarkersRelativeLength() {
        return this.stateMarkersRelativeLength;
    }

    public void setStateMarkersRelativeLength(float stateMarkersRelativeLength) {
        this.stateMarkersRelativeLength = stateMarkersRelativeLength;
        takeEffect(this.animation);
    }

    public float getKnobRadius() {
        return this.knobRadius;
    }

    public void setKnobRadius(float knobRadius) {
        this.knobRadius = knobRadius;
        takeEffect(this.animation);
    }

    public boolean isFreeRotation() {
        return this.freeRotation;
    }

    public void setFreeRotation(boolean freeRotation) {
        this.freeRotation = freeRotation;
    }

    public int getSwipeDirection() {
        return this.swipeDirection;
    }

    public void setSwipeDirection(int swipeDirection) {
        this.swipeDirection = swipeDirection;
    }

    public int getSwipeSensibilityPixels() {
        return this.swipeSensibilityPixels;
    }

    public void setSwipeSensibilityPixels(int swipeSensibilityPixels) {
        this.swipeSensibilityPixels = swipeSensibilityPixels;
    }

    public int getStateMarkersAccentWidth() {
        return this.stateMarkersAccentWidth;
    }

    public void setStateMarkersAccentWidth(int stateMarkersAccentWidth) {
        this.stateMarkersAccentWidth = stateMarkersAccentWidth;
        takeEffect(this.animation);
    }

    public int getStateMarkersAccentColor() {
        return this.stateMarkersAccentColor;
    }

    public void setStateMarkersAccentColor(int stateMarkersAccentColor) {
        this.stateMarkersAccentColor = stateMarkersAccentColor;
        takeEffect(this.animation);
    }

    public float getStateMarkersAccentRelativeLength() {
        return this.stateMarkersAccentRelativeLength;
    }

    public void setStateMarkersAccentRelativeLength(float stateMarkersAccentRelativeLength) {
        this.stateMarkersAccentRelativeLength = stateMarkersAccentRelativeLength;
        takeEffect(this.animation);
    }

    public int getStateMarkersAccentPeriodicity() {
        return this.stateMarkersAccentPeriodicity;
    }

    public void setStateMarkersAccentPeriodicity(int stateMarkersAccentPeriodicity) {
        this.stateMarkersAccentPeriodicity = stateMarkersAccentPeriodicity;
        takeEffect(this.animation);
    }

    public int getKnobDrawableRes() {
        return this.knobDrawableRes;
    }

    public void setKnobDrawableRes(int knobDrawableRes) {
        this.knobDrawableRes = knobDrawableRes;
        takeEffect(this.animation);
    }

    public boolean isKnobDrawableRotates() {
        return this.knobDrawableRotates;
    }

    public void setKnobDrawableRotates(boolean knobDrawableRotates) {
        this.knobDrawableRotates = knobDrawableRotates;
        takeEffect(this.animation);
    }

    public float getCircularIndicatorRelativeRadius() {
        return this.circularIndicatorRelativeRadius;
    }

    public void setCircularIndicatorRelativeRadius(float circularIndicatorRelativeRadius) {
        this.circularIndicatorRelativeRadius = circularIndicatorRelativeRadius;
        takeEffect(this.animation);
    }

    public float getCircularIndicatorRelativePosition() {
        return this.circularIndicatorRelativePosition;
    }

    public void setCircularIndicatorRelativePosition(float circularIndicatorRelativePosition) {
        this.circularIndicatorRelativePosition = circularIndicatorRelativePosition;
        takeEffect(this.animation);
    }

    public int getCircularIndicatorColor() {
        return this.circularIndicatorColor;
    }

    public void setCircularIndicatorColor(int circularIndicatorColor) {
        this.circularIndicatorColor = circularIndicatorColor;
        takeEffect(this.animation);
    }

    public boolean isSelectedStateMarkerContinuous() {
        return this.selectedStateMarkerContinuous;
    }

    public void setSelectedStateMarkerContinuous(boolean selectedStateMarkerContinuous) {
        this.selectedStateMarkerContinuous = selectedStateMarkerContinuous;
        takeEffect(this.animation);
    }

    public float getMinAngle() {
        return this.minAngle;
    }

    public void setMinAngle(float minAngle) {
        this.minAngle = minAngle;
        takeEffect(this.animation);
    }

    public float getMaxAngle() {
        return this.maxAngle;
    }

    public void setMaxAngle(float maxAngle) {
        this.maxAngle = maxAngle;
        takeEffect(this.animation);
    }

    public float getExternalRadius() {
        return this.externalRadius;
    }

    public void setExternalRadius(float externalRadius) {
        this.externalRadius = externalRadius;
        takeEffect(this.animation);
    }

    public Drawable getKnobDrawable() {
        return this.knobDrawable;
    }

    public void setKnobDrawable(Drawable knobDrawable) {
        this.knobDrawable = knobDrawable;
        takeEffect(this.animation);
    }

    public boolean isShowBalloonValues() {
        return this.showBalloonValues;
    }

    public void setShowBalloonValues(boolean showBalloonValues) {
        this.showBalloonValues = showBalloonValues;
    }

    public int getBalloonValuesTimeToLive() {
        return this.balloonValuesTimeToLive;
    }

    public void setBalloonValuesTimeToLive(int balloonValuesTimeToLive) {
        this.balloonValuesTimeToLive = balloonValuesTimeToLive;
    }

    public float getBalloonValuesRelativePosition() {
        return this.balloonValuesRelativePosition;
    }

    public void setBalloonValuesRelativePosition(float balloonValuesRelativePosition) {
        this.balloonValuesRelativePosition = balloonValuesRelativePosition;
    }

    public float getBalloonValuesTextSize() {
        return this.balloonValuesTextSize;
    }

    public void setBalloonValuesTextSize(float balloonValuesTextSize) {
        this.balloonValuesTextSize = balloonValuesTextSize;
    }

    public boolean isBalloonValuesSlightlyTransparent() {
        return this.balloonValuesSlightlyTransparent;
    }

    public void setBalloonValuesSlightlyTransparent(boolean balloonValuesSlightlyTransparent) {
        this.balloonValuesSlightlyTransparent = balloonValuesSlightlyTransparent;
    }

    public int getClickBehaviour() {
        return this.clickBehaviour;
    }

    public void setClickBehaviour(int clickBehaviour) {
        this.clickBehaviour = clickBehaviour;
    }

    public void setUserBehaviour(Runnable userRunnable) {
        this.userRunnable = userRunnable;
    }

    public void runUserBehaviour() {
        if (this.userRunnable != null) {
            this.userRunnable.run();
        }
    }
}
