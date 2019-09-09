package com.around.engineerbuddy.component;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.util.BMAUIUtil;

public class BMATextSwitch  extends FrameLayout {

    Context context;
    int slideViewWidth;
    private FrameLayout layout;
    private View toggleCircle;
    private TextView background_oval_off, background_oval_on;
    private boolean _crossfadeRunning = false;
    private ObjectAnimator _oaLeft, _oaRight;
    private boolean isToggleOn;
    private String leftTxt, rightTxt;
    private OnSwitchChangeListener mOnSwitchChangeListener;
//    private FNOnClickListener actionClickListener = new FNOnClickListener() {
//        @Override
//        public void execute(View v) {
//            if (isEnabled()) {
//                changeSelection(!isToggleOn);
//            }
//        }
//    };

    public BMATextSwitch(Context context) {
        this(context, null);
    }

    public BMATextSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FNTextSwitch);
        try {
            this.isToggleOn = a.getBoolean(R.styleable.FNTextSwitch_istoggle_on, false);
            this.leftTxt = a.getString(R.styleable.FNTextSwitch_left_text);
            this.rightTxt = a.getString(R.styleable.FNTextSwitch_right_text);
        } finally {
            a.recycle();
        }
        this.init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.bmatext_switch, this, true);
        background_oval_off = view.findViewById(R.id.background_oval_off);
        background_oval_off.setVisibility(View.GONE);
        background_oval_on = view.findViewById(R.id.background_oval_on);
        background_oval_off.setText(this.leftTxt);
        background_oval_on.setText(this.rightTxt);

        toggleCircle = view.findViewById(R.id.toggleCircle);
        layout = view.findViewById(R.id.layout);
        BMAUIUtil.setBackground(ContextCompat.getDrawable(this.getContext().getApplicationContext(), R.drawable.fn_text_switch), layout);
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnabled()) {
                    changeSelection(!isToggleOn);
                }
            }
        });
        _oaLeft = ObjectAnimator.ofFloat(toggleCircle, "x", getSlideWidth(), 0).setDuration(250);
        _oaRight = ObjectAnimator.ofFloat(toggleCircle, "x", 0, getSlideWidth()).setDuration(250);
        postView(false);
    }

    public int getSlideWidth() {
        slideViewWidth = getWidth() - toggleCircle.getWidth();
        return slideViewWidth;
    }

    public void setOnSwitchChangeListener(OnSwitchChangeListener listener) {
        this.mOnSwitchChangeListener = listener;
        switchListener();
    }

    public void setOnSwitchChangeListener(OnSwitchChangeListener listener, boolean willCallInvoke) {
        this.mOnSwitchChangeListener = listener;
        if (willCallInvoke) {
            switchListener();
        }
    }

    public void switchListener() {
        if (this.mOnSwitchChangeListener != null) {
            this.mOnSwitchChangeListener.onCheckedChanged(isToggleOn);
        }
    }

    private void postView(boolean willChangeSelection) {
        this.post(new Runnable() {
            @Override
            public void run() {
                _oaRight = ObjectAnimator.ofFloat(toggleCircle, "x", 0, getSlideWidth()).setDuration(250);
                _oaLeft = ObjectAnimator.ofFloat(toggleCircle, "x", getSlideWidth(), 0).setDuration(250);
                if (willChangeSelection) {
                    changeState();
                }
            }
        });
    }

    private void crossfadeViews(final View begin, View end, int duration) {
        _crossfadeRunning = true;
        end.setAlpha(0f);
        end.setVisibility(View.VISIBLE);
        end.animate().alpha(1f).setDuration(duration).setListener(null);
        begin.animate().alpha(0f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                begin.setVisibility(View.GONE);
                _crossfadeRunning = false;
            }
        });
    }

    public void changeSelection(boolean isSelected) {
        this.isToggleOn = isSelected;
        switchListener();
        if (slideViewWidth == 0) {
            postView(true);
        } else {
            changeState();
        }
    }

    private void changeState() {
        if (_oaLeft.isRunning() || _oaRight.isRunning() || _crossfadeRunning) {
            return;
        }

        if (!isToggleOn && isChecked()) {
            _oaLeft.start();
            crossfadeViews(background_oval_off, background_oval_on, 250);
        } else if (isToggleOn && !isChecked()) {
            _oaRight.start();
            crossfadeViews(background_oval_on, background_oval_off, 250);
        }
        layout.setSelected(isToggleOn);

    }

    public void setLeftTxt(@StringRes int leftTxt) {
        this.background_oval_off.setText(leftTxt);
    }

    public void setLeftTxt(String leftTxt) {
        this.background_oval_off.setText(leftTxt);
    }

    public void setRightTxt(@StringRes int leftTxt) {
        this.background_oval_on.setText(leftTxt);
    }

    public void setRightTxt(String leftTxt) {
        this.background_oval_on.setText(leftTxt);
    }

    public boolean isChecked() {
        return layout != null && layout.isSelected();
    }

    public void setChecked(boolean isChecked) {
        changeSelection(isChecked);
    }

    public void changeSwitchColor(@ColorRes int colorId) {
        changeSwitchColor(colorId, colorId);
    }

    public void changeSwitchColor(@ColorRes int offTrackColor, @ColorRes int onTranckColor) {
        if (layout == null) {
            return;
        }
        int resId = BMAUIUtil.getColor(offTrackColor);
        if (resId != 0) {
            BMAUIUtil.setBackgroundRect(layout, offTrackColor, getResources().getDimension(R.dimen._50dp));
        }
    }

    public interface OnSwitchChangeListener {
        void onCheckedChanged(boolean isChecked);
    }
}
