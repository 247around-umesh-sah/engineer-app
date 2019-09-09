package com.around.engineerbuddy.component;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.around.engineerbuddy.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BMADatePicker extends LinearLayout implements View.OnClickListener{

    protected LinearLayout startDateContainer;
    protected LinearLayout endDateContainer;
    protected LinearLayout timePickerLayout;
    protected Date _startDate;
    protected Date _endDate;
    protected int _startTimeIndex;
    protected boolean _isReadOnly; // is view only mode
    ArrayList<?> _customDateArray;
    boolean isBackDateSelectionAllowed = true;
    private View fnDatePickerView;
    private Fragment _fragment;
//    FNOptionChooser.OnItemSelectionChangeListener dateSelectotListener = new FNOptionChooser.OnItemSelectionChangeListener() {
//        @Override
//        public void onItemChange(int selectedIndex, Object selectedItem, Dialog dialog) {
//            onDatePickerResult(selectedItem);
//        }
//    };
    private TextView sDateView;
    private TextView _txtStartDate;
    private int _endTimeIndex;
    Context context;
    public BMADatePicker(Context context) {
        this(context, null);
    }

    public BMADatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BMADatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BMADatePicker);
        if (a.hasValue(R.styleable.BMADatePicker_isBackDateSelectionAllowed)) {
            isBackDateSelectionAllowed = a.getBoolean(R.styleable.BMADatePicker_isBackDateSelectionAllowed, true);
        }
        a.recycle();
        if (this.isInEditMode()) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fnDatePickerView = inflater.inflate(R.layout.date_picker, this, true);
        this.startDateContainer = fnDatePickerView.findViewById(R.id.startDateContainer);
        this.sDateView = fnDatePickerView.findViewById(R.id.startDateView);
        this._txtStartDate = fnDatePickerView.findViewById(R.id.txtStartDate);
        this.startDateContainer.setOnClickListener(this);
        fnDatePickerView.findViewById(R.id.imageButton1).setOnClickListener(this);
    }
Calendar mcalendar;
    private int day,month,year;
        public void DateDialog() {
            mcalendar=Calendar.getInstance();
            day =   mcalendar.get(Calendar.DAY_OF_MONTH);
            year =   mcalendar.get(Calendar.YEAR);
            month =   mcalendar.get(Calendar.MONTH);


            DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    sDateView.setText(dayOfMonth + "/" + month + "/" + year);
                }

            };
                DatePickerDialog dpDialog = new DatePickerDialog(context, listener, year, month, day);
            dpDialog.show();

        }
    @Override
    public void onClick(View v) {
        DateDialog();
    }
}
