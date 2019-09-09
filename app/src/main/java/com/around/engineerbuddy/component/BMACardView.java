package com.around.engineerbuddy.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Dimension;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import com.around.engineerbuddy.R;

public class BMACardView extends CardView {
    @Dimension
    private float elevation;
    @Dimension
    private float cornerRadius;

    public BMACardView(Context context) {
        super(context);
        this.init();
    }

    public BMACardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttr(attrs);
        this.init();
    }

    public BMACardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttr(attrs);
        this.init();
    }


    private void loadAttr(AttributeSet attrs) {
        @SuppressLint("CustomViewStyleable")
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CardView);
        this.elevation = a.getDimension(R.styleable.CardView_cardElevation, 1.0f);
        this.cornerRadius = a.getDimension(R.styleable.CardView_cardCornerRadius, 5);
        a.recycle();
    }

    private void init() {
        if (this.isInEditMode()) {
            return;
        }
        this.setCardElevation(this.elevation);
        this.setRadius(this.cornerRadius);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            this.setPreventCornerOverlap(false);
        }
    }
}
