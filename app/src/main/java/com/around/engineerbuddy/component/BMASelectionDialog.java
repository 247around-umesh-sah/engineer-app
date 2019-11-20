package com.around.engineerbuddy.component;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.entity.BMAUiEntity;
import com.around.engineerbuddy.util.BMAUIUtil;
import com.around.engineerbuddy.util.BMAUtil;

import java.util.ArrayList;
import java.util.List;

public class BMASelectionDialog extends AppCompatDialog {

    RecyclerView popUpRecyclerView;
    TextView titleText, noDataToDisplay;
    Context context;
    Button okButton, cancelButton;
    List<BMAUiEntity> itemList = new ArrayList<>();
    BMAUiEntity selectedObject;
    EditText searchBox;
    boolean isSerachEnable;
    //List<BMAUiEntity> copyList = new ArrayList<>();

    List<BMAUiEntity> filteredItemList = new ArrayList<>();

    public BMASelectionDialog(Context context) {
        // super(context);
        this(context, new ArrayList<BMAUiEntity>());
    }

    public BMASelectionDialog(Context context, ArrayList<BMAUiEntity> itemList) {
        super(context);
        this.context = context;
        this.itemList = itemList;
        //this.copyList=itemList;
    }

    public BMASelectionDialog(Context context, ArrayList<BMAUiEntity> itemList, boolean isSerachEnable) {
        super(context);
        this.context = context;
        this.itemList = itemList;
        this.isSerachEnable = isSerachEnable;
        //this.copyList=itemList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().windowAnimations = R.style.ScaleFromCenter;
        this.setContentView(R.layout.list_pop_up);
        this.init();
        this.setCanceledOnTouchOutside(false);
        // this.listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        float radius = context.getResources().getDimension(R.dimen._15dp);
        int scrWidth = this.getContext().getApplicationContext().getResources().getConfiguration().screenWidthDp;
        this.getWindow().setLayout((BMAUtil.getDipFromPixel(this.getContext(), scrWidth - 45)), LinearLayout.LayoutParams.WRAP_CONTENT);
        BMAUIUtil.setBackgroundRound(this.findViewById(R.id.DialogLayout), R.color.bg_color, new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
        BMAUIUtil.setBackgroundRound(this.findViewById(R.id.pop_up_footer), android.R.color.white, new float[]{0, 0, 0, 0, radius, radius, radius, radius});
        this.findViewById(R.id.footerDivider).setVisibility(View.GONE);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView noRecordView = this.findViewById(R.id.noRecord);
        noRecordView.setText("No Record");
        //  this.popUpRecyclerView.setEmptyView(noRecordView);
        //  this.invalidateListView();
        getContext().setTheme(R.style.AppTheme);
        loaddata();
    }

    LinearLayout headerLayout;

    public void init() {
        float buttonRadius = context.getResources().getDimension(R.dimen._50dp);
        this.okButton = this.findViewById(R.id.submitButton);
        this.searchBox = this.findViewById(R.id.serachBox);
        headerLayout = this.findViewById(R.id.headerLayout);
        this.noDataToDisplay = this.findViewById(R.id.noDataToDisplay);
        if (itemList.size() == 0) {
            //  noDataToDisplay.setVisibility(View.VISIBLE);
        }
        this.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BMASelectionDialog.this.dismiss();
                BMASelectionDialog.this.onDismiss(BMASelectionDialog.this.selectedObject, BMASelectionDialog.this.selectedObject, BMASelectionDialog.this.itemList);
            }
        });
        // if (this.isConfirmation) {
        this.findViewById(R.id.btn_cancel_layout).setVisibility(View.VISIBLE);
        this.cancelButton = this.findViewById(R.id.cancelButton);
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BMASelectionDialog.this.dismiss();
            }
        });
        // }headerLayout
        BMAUIUtil.setBackgroundRect(this.okButton, R.color.submitButtonColor, buttonRadius);
        this.popUpRecyclerView = this.findViewById(R.id.popupRecyclerview);
        this.titleText = this.findViewById(R.id.titleText);
        // this.findViewById(R.id.headerLayout).setVisibility(View.VISIBLE);
        this.titleText.setText("Select ");
        if (isSerachEnable) {
            this.headerLayout.setVisibility(View.VISIBLE);
        }
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();

                if (popUpAdapter != null) {
                    popUpAdapter.getFilter().filter(input);
                    Log.d("aaaaaa", "afterTextChange = " + input);
                }
            }
        });
//        if (this.title!=null && this.title.length()!=0) {
//            String noRecordParam = "";
//            String[] titleArr = this.title.split(" ");
//            for (int i = 1; i < titleArr.length; i++) {
//                noRecordParam += titleArr[i].toLowerCase() + " ";
//            }
//            this.noRecordText = String.format(this.getContext().getString(R.string.nothing_to_select), noRecordParam.trim());
//        }
    }

    PopUpAdapter popUpAdapter;

    private void loaddata() {
        this.popUpRecyclerView.setLayoutManager(new LinearLayoutManager(this.context));
        popUpAdapter = new PopUpAdapter(itemList, this.popUpRecyclerView);
        this.popUpRecyclerView.setAdapter(popUpAdapter);
    }

    public class PopUpAdapter extends RecyclerView.Adapter implements Filterable {

        private final int VIEW_ITEM = 1;
        public Context context;
        View v1;
        RecyclerView.ViewHolder vh;
        private List<BMAUiEntity> list;


        public PopUpAdapter(List<BMAUiEntity> list, RecyclerView recyclerView) {

            this.list = list;
            context = recyclerView.getContext();


        }


        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof ViewHolder) {
                final BMAUiEntity statusList = list.get(position);
                ((ViewHolder) holder).status.setText(statusList.getDetail1());
                if (statusList.isChecked()) {
                    ((ViewHolder) holder).radioButton.setChecked(true);
                } else {
                    ((ViewHolder) holder).radioButton.setChecked(false);
                }
                ((ViewHolder) holder).radioButtonLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        RadioButton radioButton=(RadioButton) v;
//                        radioButton.setChecked(((RadioButton) v).isChecked());
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).isChecked()) {
                                list.get(i).setChecked(false);

                            }
                        }
                        selectedObject = statusList;

                        statusList.setChecked(true);
                        notifyDataSetChanged();
                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public List<BMAUiEntity> getList() {
            return list;
        }


        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
            v1 = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.cancel_status_list, parent, false);
            return new ViewHolder(v1);

        }

        @Override
        public Filter getFilter() {
            Log.d("aaaaaa", "Upper charSequence = ");
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults results = new FilterResults();
                    //Implement filter logic
                    // if edittext is null return the actual list
                    if (charSequence == null || charSequence.length() == 0) {
                        //No need for filter
                        results.values = itemList;
                        results.count = itemList.size();

                    } else {

                        //Need Filter
                        // it matches the text  entered in the edittext and set the data in adapter list
                        ArrayList<BMAUiEntity> fRecords = new ArrayList<>();

                        for (BMAUiEntity s : itemList) {
                            if (s.getDetail1().toUpperCase().trim().contains(charSequence.toString().toUpperCase().trim())) {
                                fRecords.add(s);
                            }
                        }
                        results.values = fRecords;
                        results.count = fRecords.size();
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    list = (List<BMAUiEntity>) filterResults.values;
                    notifyDataSetChanged();
//                    if(filterResults.count>0) {
//                        itemList.addAll(filteredItemList);
//                    }
                    // refresh the list with filtered data

                }
            };
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView status;
            RadioButton radioButton;
            RelativeLayout radioButtonLayout;

            public ViewHolder(View view) {
                super(view);
                status = view.findViewById(R.id.staus);
                radioButton = view.findViewById(R.id.radioButton);
                radioButton.setClickable(false);
                radioButtonLayout = view.findViewById(R.id.radioButtonLayout);

            }
        }

    }

    public void onDismiss(BMAUiEntity selectedItems, BMAUiEntity deletedItems, List<BMAUiEntity> updatedItems) {
    }


}
