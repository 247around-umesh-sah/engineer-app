package com.around.engineerbuddy.component;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.around.engineerbuddy.MainActivityHelper;
import com.around.engineerbuddy.R;
import com.around.engineerbuddy.util.BMAObjectUtil;
import com.around.engineerbuddy.util.BMAUIUtil;

public class BMAOTPDialog extends Dialog implements View.OnClickListener {

    Context context;
    public Button okButton, cancelButton;
    LinearLayout cancelBtnLayout, optionalBtnLayout;
    private String message = "";
    private boolean isConfirmation;
    private boolean isWarning;
    private int layoutId = R.layout.fndilog_alert_n;
    private boolean isOptionalBtnVisible;
    private Button optionalBtn;
    private String positiveBtnTxt, negativeBtnTxt;
    private TextView alertTextView;
    TableRow tableInputRow, tableRow2, tableRow1;
    EditText txt_input;
    boolean isInformation;
    boolean isShowInputField,isShowCancel;

    public BMAOTPDialog(Context context) {
        this(context, false, false);
        //super(context);

    }
    public BMAOTPDialog(Context context, boolean isShowInputField,boolean isShowCancel) {
        super(context);
        this.context = context;
        this.isShowInputField = isShowInputField;
        this.isShowCancel = isShowCancel;
    }

//    public BMAOTPDialog(Context context, boolean isConfirmation, boolean isWarning) {
//        super(context);
//        this.context = context;
//        this.isConfirmation = isConfirmation;
//        this.isWarning = isWarning;
//    }

//    public BMAOTPDialog(Context context, boolean isConfirmation, boolean isWarning, boolean isOptionalBtnVisible, boolean isInformation) {
//        super(context);
//        this.context = context;
//        this.isConfirmation = isConfirmation;
//        this.isWarning = isWarning;
//        this.isInformation = isInformation;
//    }
//
//    public BMAOTPDialog(Context context, boolean isConfirmation, boolean isWarning, boolean isOptionalBtnVisible) {
//        super(context);
//        this.isConfirmation = isConfirmation;
//        this.isWarning = isWarning;
//        this.isOptionalBtnVisible = isOptionalBtnVisible;
//    }

    public void show(@StringRes int message) {
        this.show(getContext().getString(message));
    }

    public void show(String message) {
        this.message = message;
        super.show();
    }

    protected void loadHeader() {
        float dialogRadius = 10f;// this.getDimension(R.dimen._10dp);
        BMAUIUtil.setBackgroundRound(this.findViewById(R.id.DialogNLayout), R.color.bg_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
//        ImageView titleIcon = findViewById(R.id.titleIcon);
//        titleIcon.setImageResource(this.getHeaderIcon());
    }

    protected void loadBody() {
        this.alertTextView = this.findViewById(R.id.txt_alert_message);
        this.alertTextView.setText(Html.fromHtml(message));
        if(isShowInputField){
            tableInputRow.setVisibility(View.VISIBLE);
        }
    }

    protected void loadFooter() {
        float dialogRadius = 10f;//this.getDimension(R.dimen._10dp);
        BMAUIUtil.setBackgroundRound(this.findViewById(R.id.pop_up_footer), android.R.color.white, new float[]{0, 0, 0, 0, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        this.findViewById(R.id.footerDivider).setVisibility(View.GONE);
        this.cancelBtnLayout = this.findViewById(R.id.btn_cancel_layout);
        this.optionalBtnLayout = this.findViewById(R.id.optionalBtn_layout);
        this.okButton = this.findViewById(R.id.submitButton);
        this.cancelButton = this.findViewById(R.id.cancelButton);
        this.optionalBtn = this.findViewById(R.id.disapprove);
        this.tableInputRow = this.findViewById(R.id.tableInputRow);
        this.tableRow2 = this.findViewById(R.id.tableRow2);
        this.tableRow1 = this.findViewById(R.id.tableRow1);

        this.txt_input = this.findViewById(R.id.txt_input);
        if (BMAObjectUtil.isNonEmptyStr(positiveBtnTxt)) {
            this.okButton.setText(positiveBtnTxt);
        }
        if (BMAObjectUtil.isNonEmptyStr(negativeBtnTxt)) {
            this.cancelButton.setText(negativeBtnTxt);
        }
        this.okButton.setOnClickListener(this);
        this.cancelButton.setOnClickListener(this);
        final float buttonRadius = 50f;//this.getDimension(R.dimen._50dp);
       // BMAUIUtil.setBackgroundRect(okButton, R.color.dark_gray, buttonRadius);
       // if (this.isConfirmation || this.isOptionalBtnVisible) {

            BMAUIUtil.setBackgroundRect(okButton, isInformation ? R.color.orangeLight : R.color.new_green_color, buttonRadius);
            BMAUIUtil.setBackgroundRect(cancelBtnLayout, R.color.transparentWhite, buttonRadius);
            cancelBtnLayout.setVisibility(View.VISIBLE);
            cancelButton.setTextColor(BMAUIUtil.getColor(R.color.blackMedium));
       // }
        if (this.isOptionalBtnVisible) {
            optionalBtnLayout.setVisibility(View.VISIBLE);
            optionalBtn.setTextColor(BMAUIUtil.getColor(R.color.blackMedium));
            optionalBtn.setOnClickListener(this);
            cancelButton.setTextColor(BMAUIUtil.getColor(android.R.color.white));
            BMAUIUtil.setBackgroundRect(cancelButton, R.color.gray, buttonRadius);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getAttributes().windowAnimations = R.style.ScaleFromCenter;
        this.setContentView(this.layoutID());
        this.setCanceledOnTouchOutside(false);
        loadHeader();
        loadBody();
        loadFooter();
        Resources resources = getContext().getResources();
        int scrWidth = resources.getConfiguration().screenWidthDp;
        if ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrWidth, MainActivityHelper.application().getResources().getDisplayMetrics()) <= 700) {
            this.getWindow().setLayout((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrWidth - 50, MainActivityHelper.application().getResources().getDisplayMetrics()), LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            this.getWindow().setLayout((3 * (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrWidth, MainActivityHelper.application().getResources().getDisplayMetrics())) / 4, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    protected int layoutID() {
        return layoutId;
    }

    public void setPositiveBtnTxt(String positiveBtnTxt) {
        this.positiveBtnTxt = positiveBtnTxt;
    }

    public void setNegativeBtnTxt(String negativeBtnTxt) {
        this.negativeBtnTxt = negativeBtnTxt;
    }

    @DrawableRes
    protected int getHeaderIcon() {
        if (isConfirmation) {
            if (isInformation) {
                return R.drawable.icon_info_sign;
            } else {
                return R.drawable.icon_check_circle;
            }
        } else if (isWarning) {
            return R.drawable.icon_exclamation_triangle;
        }
        return R.drawable.icon_check_circle;
    }

    private @ColorRes
    int getHeaderColor() {
        if (isConfirmation)
            return R.color.new_green_color;
        else if (isWarning) {
            return R.color.darkRed;
        }
        return R.color.orangeLight;
    }

    public boolean isInputVisible;

    public void showInputField() {
        isInputVisible = true;
        tableRow1.setVisibility(View.GONE);
        alertTextView.setText(getContext().getResources().getString(R.string.enterAmount));
        this.tableInputRow.setVisibility(View.VISIBLE);
    }

    public void fillInputField(String amount) {
        txt_input.setText(amount);
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
        if(v.getId()==okButton.getId()){
            onConfirmation();
        }
        else if(v.getId()==cancelButton.getId()){

        }
//        if (isConfirmation || isOptionalBtnVisible) {
//            if (v.getId() == okButton.getId()) {
//                if (isInputVisible) {
//                    onConfirmation(txt_input.getText().toString().trim());
//                } else {
//                    onConfirmation();
//                }
//            } else if (v.getId() == cancelButton.getId()) {
//                if (isConfirmation) {
//                    onCancelConfirmation();
//                } else {
//                    onNegativeConfirmation();
//                }
//            } else if (v.getId() == optionalBtn.getId()) {
//                onCancelConfirmation();
//            }
//        } else if (isWarning) {
//            onWarningDismiss();
//        } else {
//            onDefault();
//        }
   }

    public void onDefault() {

    }

    public void onNegativeConfirmation() {

    }

    public void onCancelConfirmation() {
    }

    public void onWarningDismiss() {
    }

    public void onConfirmation() {
    }

    public void onConfirmation(String inputValue) {
    }

    @Override
    public void onBackPressed() {
    }
}