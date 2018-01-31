package com.around.technician.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.around.technician.BookingGetterSetter;
import com.around.technician.R;

import java.util.List;

/**
 * Created by abhay on 4/1/18.
 */
@SuppressWarnings("ALL")
public class CancelBookingAdapter extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    public Context context;
    View v1;
    RecyclerView.ViewHolder vh;
    private List<BookingGetterSetter> list;

    public CancelBookingAdapter(List<BookingGetterSetter> list, RecyclerView recyclerView) {

        this.list = list;
        context = recyclerView.getContext();

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {
            final BookingGetterSetter statusList = list.get(position);
            ((ViewHolder) holder).status.setText(statusList.getCancellationReason());
            if (statusList.getCheckedCancellationReason()) {
                ((ViewHolder) holder).radioButton.setChecked(true);
            } else {
                ((ViewHolder) holder).radioButton.setChecked(false);
            }
            ((ViewHolder) holder).radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getCheckedCancellationReason()) {
                            list.get(i).setCheckedCancellationReason(false);
                        }
                    }

                    statusList.setCheckedCancellationReason(true);
                    notifyDataSetChanged();
                }
            });

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<BookingGetterSetter> getCancellationReason() {
        return list;
    }


    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        if (viewType == VIEW_ITEM) {

            v1 = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.cancel_status_list, parent, false);
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
        TextView status;
        AppCompatRadioButton radioButton;

        public ViewHolder(View view) {
            super(view);
            status = view.findViewById(R.id.staus);
            radioButton = view.findViewById(R.id.radioButton);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);

            progressBar = v.findViewById(R.id.progressBar1);
        }
    }
}


