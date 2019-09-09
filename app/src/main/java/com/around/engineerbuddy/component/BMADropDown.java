package com.around.engineerbuddy.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.util.BMAObjectUtil;
import com.google.gson.internal.Primitives;

public class BMADropDown   extends LinearLayout {

   // private TextView titleView;
    private TextView mainTextView;
    private BMAFontViewField caretIcon;
    private BMAFontViewField rowIcon;
    private ImageView rowImage;
    private String title;
    private String hint;
    private boolean isEnable = true;
    private int bottomLineColor;
    private boolean isTitleVisibleAlways = false;
    private boolean showTitleView = true;

    private Object selectedObject;

    public BMADropDown(Context context) {
        this(context, null);
    }

    public BMADropDown(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public BMADropDown(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void init(AttributeSet attrs) {
        if (this.isInEditMode()) {
            return;
        }
        setFocusable(true);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fn_drop_down_n, this, true);
        loadAttributes(attrs);
       // titleView = view.findViewById(R.id.titleView);
        mainTextView = view.findViewById(R.id.mainTextView);
        caretIcon = view.findViewById(R.id.caretIcon);
        rowIcon = view.findViewById(R.id.rowIcon);
        rowImage = view.findViewById(R.id.rowImage);
        if (showTitleView && BMAObjectUtil.isNonEmptyStr(title)) {
          //  titleView.setText(Html.fromHtml(title));
        }
        if (BMAObjectUtil.isNonEmptyStr(hint)) {
            mainTextView.setHint(hint);
        }
        setEnabled(isEnable);
       //titleView.setVisibility(showTitleView ? (!this.isEnable || isTitleVisibleAlways ? VISIBLE : INVISIBLE) : GONE);
    }

    private void loadAttributes(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BMADropDown);
        if (a.hasValue(R.styleable.BMADropDown_dropDownLabel)) {
            title = a.getString(R.styleable.BMADropDown_dropDownLabel);
        }
        if (a.hasValue(R.styleable.BMADropDown_titleVisibile)) {
            isTitleVisibleAlways = a.getBoolean(R.styleable.BMADropDown_titleVisibile, false);
        }
        if (a.hasValue(R.styleable.BMADropDown_showTitle)) {
            showTitleView = a.getBoolean(R.styleable.BMADropDown_showTitle, false);
        }
        if (a.hasValue(R.styleable.BMADropDown_hint)) {
            hint = a.getString(R.styleable.BMADropDown_hint);
        }
        if (a.hasValue(R.styleable.BMADropDown_isReadOnly)) {
            isEnable = !a.getBoolean(R.styleable.BMADropDown_isReadOnly, false);
        }
        if (a.hasValue(R.styleable.BMADropDown_bottomLineColor)) {
            bottomLineColor = a.getColor(R.styleable.BMADropDown_bottomLineColor, getResources().getColor(R.color.colorPrimary));
        }
        a.recycle();
    }

    public void setIcon(String iconColor) {
        if (BMAObjectUtil.isEmptyStr(iconColor)) {
            rowIcon.setVisibility(GONE);
            return;
        }
        setIcon(Color.parseColor(iconColor));
    }

    public void setIcon(int iconColor) {
        try {
            rowIcon.setTextColor(iconColor);
            rowIcon.setVisibility(VISIBLE);
        } catch (Exception e) {
            rowIcon.setVisibility(GONE);
        }
    }

    public void setTitle(String title) {
        this.setTitle(title, 0);
    }

    public void setTitle(String title, @ColorInt int color) {
        if (!showTitleView) {
            return;
        }
        this.title = title;
//        this.titleView.setText(title);
//        this.titleView.setTextColor(color != 0 ? color : getResources().getColor(R.color.lightBlack));
    }

    public void setImage(Drawable drawable) {
        if (drawable == null) {
            rowImage.setVisibility(GONE);
            return;
        }
        rowImage.setImageDrawable(drawable);
        rowImage.setVisibility(VISIBLE);
    }

    public void setHint(String hint) {
        this.mainTextView.setHint(hint);
    }

    public void setHint(@StringRes int hint) {
        this.mainTextView.setHint(hint);
    }

    public <T> T getSelectedObject(Class<T> entity) {
        return Primitives.wrap(entity).cast(selectedObject);
    }

    public Object getSelectedObject() {
        return this.selectedObject;
    }

    public void setSelectedObject(Object selectedObject, String displayString) {
        setSelectedObject(selectedObject, displayString, getResources().getColor(R.color.lightBlack));
    }

    public void setSelectedObject(Object selectedObject, String displayString, int displayStringColor) {
        this.selectedObject = selectedObject;
        mainTextView.setText(displayString);
        mainTextView.setTextColor(displayStringColor);
        if (this.selectedObject == null) {
            this.selectedObject = displayString;
        }
      //  titleView.setVisibility(showTitleView ? (BMAObjectUtil.isNonEmptyStr(displayString) ? VISIBLE : INVISIBLE) : GONE);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.isEnable = enabled;
        caretIcon.setVisibility(this.isEnable ? VISIBLE : INVISIBLE);
    }

    public void resetDropDown() {
        selectedObject = null;
        mainTextView.setText(null);
        setEnabled(isEnable);
        if (showTitleView && BMAObjectUtil.isEmptyStr(title)) {
            //titleView.setVisibility(INVISIBLE);
        }
    }

    /**
     * To hide the title completely while creating the dropdown from Java Code.
     *
     * @param isTitleVisibleAlways
     */
    public void setVisibleTitle(boolean isTitleVisibleAlways) {
        this.showTitleView = isTitleVisibleAlways;
      //  this.titleView.setVisibility(isTitleVisibleAlways ? VISIBLE : GONE);
    }

}
