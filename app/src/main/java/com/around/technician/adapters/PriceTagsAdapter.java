package com.around.technician.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.around.technician.BookingGetterSetter;
import com.around.technician.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhay on 2/1/18.
 */
@SuppressWarnings("ALL")
public class PriceTagsAdapter extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    public Context context;
    View v1;
    RecyclerView.ViewHolder vh;
    boolean IsDisable;
    List<String> checkedLineItem = new ArrayList<>();
    private List<BookingGetterSetter> list;

    public PriceTagsAdapter(List<BookingGetterSetter> list, RecyclerView recyclerView) {

        this.list = list;
        context = recyclerView.getContext();

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {

            final BookingGetterSetter unitList = (BookingGetterSetter) list.get(position);
            ((ViewHolder) holder).price_tags.setText(unitList.getPriceTags());

            // unitList.setDelivered(unitList.getDelivered());
            if (unitList.getDelivered()) {
                ((ViewHolder) holder).checkBox.setChecked(true);
            } else {
                ((ViewHolder) holder).checkBox.setChecked(false);
                if (unitList.getApplianceBroken() && unitList.getPriceTags().equals(context.getResources().getString(R.string.wall_mount_stand))) {

                    ((ViewHolder) holder).checkBox.setEnabled(false);
                }
            }

            ((ViewHolder) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isChecked()) {

                        checkedLineItem.add(unitList.getUnitID());
                        if (!unitList.getDelivered()) {
                            unitList.setDelivered(true);
                            notifyDataSetChanged();
                        }

                    } else {
                        checkedLineItem.remove(unitList.getUnitID());
                        if (unitList.getDelivered()) {
                            unitList.setDelivered(false);
                            notifyDataSetChanged();
                        }
                    }
                }
            });

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    public List<String> getCheckedLineItem() {
        return checkedLineItem;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        if (viewType == VIEW_ITEM) {

            v1 = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.price_tags_list, parent, false);
            vh = new ViewHolder(v1);
            this.context = v1.getContext();
            return vh;

        } else {

            v1 = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_bar_item, parent, false);
            vh = new ProgressViewHolder(v1);
            return vh;
        }

    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return list.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView price_tags;
        AppCompatCheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            price_tags = (TextView) view.findViewById(R.id.price_tags);
            checkBox = (AppCompatCheckBox) view.findViewById(R.id.unit_checkbox);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);

            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }
}

