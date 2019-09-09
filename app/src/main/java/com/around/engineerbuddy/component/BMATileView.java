package com.around.engineerbuddy.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.util.BMAUIUtil;

public class BMATileView extends LinearLayout {

    protected TextView titleView, countView, explanationView, booking;
    protected ImageView imageView;
    protected View view;
    protected CardView cardViewLayout;
    protected LinearLayout imageContainer;
    TextView titleHorizontal;
    int orientation = 1;
    @ColorRes
    int circularViewBGColor = R.color.bg_color;
    private int imgHeight, imgWidth, outerPadding;
    private boolean isCircularImg;

    public BMATileView(Context context) {
        this(context, null);
    }

    public BMATileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BMATileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BMATileView, defStyle, 0);
        if (array.hasValue(R.styleable.BMATileView_imgHeight)) {
            this.imgHeight = (int) array.getDimension(R.styleable.BMATileView_imgHeight, 30);
        }
        if (array.hasValue(R.styleable.BMATileView_imgWidth)) {
            this.imgWidth = (int) array.getDimension(R.styleable.BMATileView_imgWidth, 30);
        }
        if (array.hasValue(R.styleable.BMATileView_outerPadding)) {
            this.outerPadding = (int) array.getDimension(R.styleable.BMATileView_outerPadding, 12);
        }
        if (array.hasValue(R.styleable.BMATileView_isCircularImg)) {
            this.isCircularImg = array.getBoolean(R.styleable.BMATileView_isCircularImg, false);
        }
        array.recycle();
        this.init();
    }

    public void setImageCircleColor(boolean isaddImageCircle) {
        this.circularViewBGColor = isaddImageCircle ? R.color.bg_color : android.R.color.transparent;
    }

    private void init() {
        if (this.isInEditMode()) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.view = inflater.inflate(layoutId(), this, true);
        this.titleView = view.findViewById(R.id.title);
        this.countView = view.findViewById(R.id.count);
        this.imageView = view.findViewById(R.id.image);
        this.imageContainer = view.findViewById(R.id.imageContainer);
        this.explanationView = view.findViewById(R.id.explanation);
        this.cardViewLayout = view.findViewById(R.id.cardViewLayout);
        titleHorizontal = view.findViewById(R.id.bottomBoader);
        this.booking = view.findViewById(R.id.booking);
        // this.imageContainer.setPadding(this.outerPadding, this.outerPadding, this.outerPadding, this.outerPadding);
        setTileImageSize();
        // addImageCircle();
    }

    public void setTileImageSize() {
        if (this.imgHeight > 0 && imgWidth > 0) {
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(imgWidth, imgHeight);
            this.imageView.setLayoutParams(parms);
            // this.imageView.setCircular(isCircularImg);
        }
    }

    protected int layoutId() {
        return R.layout.bma_tile_view;
    }

    public void setTitle(String title) {
        TextView titleView = getTileView();
        if (titleView == null) {
            return;
        }
        titleView.setText(title);
    }

    public void setTitle(@StringRes int title) {
        setTitle(getResources().getString(title));
    }

    public void setTitleColor(@ColorRes int colorId) {
        TextView titleView = getTileView();
        if (titleView == null) {
            return;
        }
        int resId = getResources().getColor(colorId);
        if (resId != 0) {
            titleView.setTextColor(resId);
        }
    }

    private boolean isVertical() {
        return orientation == LinearLayout.VERTICAL;
    }

    public void setCount(int count) {
        if (this.countView == null) {
            return;
        }
        this.countView.setVisibility(View.VISIBLE);
        String countString = String.valueOf(count);
        if (count > 99) {
            this.countView.setTextSize(10f);//.setTextDimen(R.dimen._10dp);
            countString = 99 + "+";
        }
        this.countView.setText(countString);
    }

    public void setExplanation(String explanation) {
        if (this.explanationView == null) {
            return;
        }
        this.explanationView.setVisibility(VISIBLE);
        this.explanationView.setText(explanation);
    }

    public void setExplanation(@StringRes int explanation) {
        setExplanation(getResources().getString(explanation));
    }

    public void hideImage() {
        if (imageView != null) {
            this.imageView.setVisibility(View.GONE);
        }
    }

    private boolean isImageViewInit() {
        if (this.imageView == null) {
            return false;
        }
        this.imageView.setVisibility(View.VISIBLE);
        return true;
    }

//    public void setImageUrl(String imgUrl) {
//        if (isImageViewInit()) {
//            imageView.setURL(imgUrl, R.drawable.noimage);
//        }
//    }

//    /**
//     * Work same as setImageURL,extra option imageText added to show alpha
//     * characters.
//     *
//     * @param imgUrl
//     *            image url
//     * @param imageText
//     *            text of whom initials are going to be use in alpha image
//     */
//    public void setImageUrl(String imgUrl, String imageText) {
//        if (isImageViewInit()) {
//            imgUrl = StringEscapeUtils.unescapeHtml3(imgUrl);
//            if (FNUtil.isAlphaImg(imgUrl)) {
//                imageView.setImageDrawable(FNImageUtil.alphaImageDrawable(imageText));
//            } else {
//                imageView.setURL(imgUrl, R.drawable.noimage);
//            }
//        }
//    }

    public void setImageDrawable(Drawable icon) {
        if (isImageViewInit()) {
            this.imageView.setImageDrawable(icon);
        }
    }

    public void setImageResource(@DrawableRes int icon) {
        if (isImageViewInit()) {
            this.imageView.setImageResource(icon);
            //this.addImageCircle();
        }
    }

    public void setVisibility(boolean isVisible) {
        this.booking.setVisibility(isVisible ? VISIBLE : GONE);
    }

    public void setText(String text) {
        this.booking.setText(text);
    }

    public void setImageResource(@DrawableRes int icon, boolean isCircular) {
        if (isImageViewInit()) {
            this.imageView.setImageResource(icon);
            this.addImageCircle();
        }
    }

    public void hideCountField() {
        if (countView == null) {
            return;
        }
        this.countView.setVisibility(View.GONE);
    }

    public void hideTitleView() {
        if (titleView != null) {
            titleView.setVisibility(GONE);
        }
    }

//    public void setExplanationColor(@ColorRes int colorId) {
//        if (this.explanationView == null) {
//            return;
//        }
//        int resId = FNUIUtil.getColor(getContext(), colorId);
//        if (resId != 0) {
//            this.explanationView.setTextColor(resId);
//        }
//    }

    public void addImageCircle() {
        imageContainer.post(new Runnable() {
            @Override
            public void run() {
                int radius = imageContainer.getWidth() > imageContainer.getHeight() ? imageContainer.getWidth() / 2 : imageContainer.getHeight() / 2;
                int paddingLeft = imageContainer.getWidth() > imageContainer.getHeight() ? outerPadding : (imageContainer.getHeight() - imageContainer.getWidth()) + outerPadding;
                int paddingTop = imageContainer.getHeight() > imageContainer.getWidth() ? outerPadding : imageContainer.getWidth() - imageContainer.getHeight() + outerPadding;
                imageContainer.setPadding(paddingLeft, paddingTop, paddingLeft, paddingTop);
                BMAUIUtil.setBackgroundRect(imageContainer, circularViewBGColor, radius + outerPadding);
            }
        });
    }

    public void changeDirection(int orientation) {
        this.orientation = orientation;
        switch (orientation) {
            case LinearLayout.HORIZONTAL:
                titleHorizontal.setVisibility(View.VISIBLE);
                titleView.setVisibility(View.GONE);
                break;
            case LinearLayout.VERTICAL:
                titleHorizontal.setVisibility(View.GONE);
                titleView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private TextView getTileView() {
        switch (orientation) {
            case LinearLayout.HORIZONTAL:
                return titleHorizontal;
            case LinearLayout.VERTICAL:
                return titleView;
        }
        return null;
    }

    public void setCardViewBg(int colorId) {
        if (this.cardViewLayout != null) {
            cardViewLayout.setCardBackgroundColor(BMAUIUtil.getColor(colorId));
        }
    }

    public void setTitleBgColor(@ColorRes int colorId) {
        TextView titleView = getTileView();
        if (titleView == null) {
            return;
        }
        int resId = BMAUIUtil.getColor(colorId);
        if (resId != 0) {
            titleView.setBackgroundColor(resId);
        }
    }

    public void setTitleTextFontSize(@DimenRes int textSize) {
        TextView titleView = getTileView();
        if (titleView == null) {
            return;
        }
        titleView.setTextSize(textSize);
    }

    public void setBoaderViewVisible() {
        this.titleHorizontal.setVisibility(VISIBLE);
        // BMAUIUtil.setBackgroundRound(this.titleHorizontal);
    }

    public void setBoaderViewColor(int color) {
        this.titleHorizontal.setBackgroundColor(color);
        // BMAUIUtil.setBackgroundRound(this.titleHorizontal);
    }


}
