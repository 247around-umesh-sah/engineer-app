package com.around.engineerbuddy.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.around.engineerbuddy.MainActivityHelper;

public class BMAUIUtil {

    public static void setBackgroundRound(View v, @ColorRes int bgColor, float[] radii) {
        GradientDrawable drawable = getRoundCorner(getColor(bgColor), radii);
        setBackground(drawable, v);
    }
    public static GradientDrawable getRoundCorner(@ColorInt int bgColor, float[] radii) {
        return getShape(bgColor, Color.TRANSPARENT, 0, radii, BMAEnum.ROUND_CORNER);
    }
    public static int getColor(@ColorRes int colorId) {

        return getColor(MainActivityHelper.application(), colorId);
    }
    public static int getColor(Context context, @ColorRes int colorId) {
        try {
            return ContextCompat.getColor(context, colorId);
        } catch (Exception e) {
            return colorId;
        }
    }
    public static void setBackground(Drawable drawable, View view) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundOld(view, drawable);
        } else {
            setBackgroundNew(view, drawable);
        }
    }
    @SuppressLint("NewApi")
    private static void setBackgroundNew(View view, Drawable drawable) {
        view.setBackground(drawable);
    }
    private static void setBackgroundOld(View view, Drawable drawable) {
        view.setBackgroundDrawable(drawable);
    }
    public static void setBackgroundRect(View v, @ColorRes int bgColor, float radius) {
        GradientDrawable drawable = getRectShape(getColor(bgColor), radius);
        setBackground(drawable, v);
    }

    public static GradientDrawable getRectShape(@ColorInt int bgColor, float radius) {
        return getShape(bgColor, Color.TRANSPARENT, 2, new float[] { radius }, BMAEnum.RECT_SHAPE);
    }
//    public static Drawable getDrawable(@DrawableRes int drawableResId) {
//        return ContextCompat.getDrawable(ApplicationHelper.application().getBaseContext(), drawableResId);
//    }
    public static GradientDrawable getShape(@ColorInt int bgColor, @ColorInt int strokeColor, int strokeWidth, float[] radii, BMAEnum shape) {
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[] {});
        gradientDrawable.setStroke(strokeWidth, strokeColor);
        gradientDrawable.setColor(bgColor);
        switch (shape) {
            case RECT_SHAPE:
                gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                float radius = radii[0];
                gradientDrawable.setCornerRadius(radius);
                break;
            case ROUND_CORNER:
                gradientDrawable.setCornerRadii(radii);
                break;
            case OVAL_SHAPE:
                gradientDrawable.setShape(GradientDrawable.OVAL);
                break;
            case RING_SHAPE:
                gradientDrawable.setShape(GradientDrawable.RING);
                break;
        }
        return gradientDrawable;
    }

}
