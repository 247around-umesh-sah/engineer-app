package com.around.technician.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.around.technician.BookingGetterSetter;
import com.around.technician.CancelBookingActivity;
import com.around.technician.CompleteBookingActivity;
import com.around.technician.Misc;
import com.around.technician.R;

import java.util.List;

/**
 * Created by abhay on 29/12/17.
 */
@SuppressWarnings("DefaultFileTemplate")
public class SearchAdapter extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    private Context context;
    private final Misc misc;
    private final List<BookingGetterSetter> list;

    public SearchAdapter(List<BookingGetterSetter> list, RecyclerView recyclerView) {

        this.list = list;
        context = recyclerView.getContext();
        misc = new Misc(context);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {

            final BookingGetterSetter bookingList = list.get(position);
            ((ViewHolder) holder).address.setText(bookingList.getAddress());
            ((ViewHolder) holder).bookingID.setText(bookingList.getBookingID());
            ((ViewHolder) holder).services.setText(bookingList.getServices());
            ((ViewHolder) holder).customerName.setText(bookingList.getCustomerName());
            ((ViewHolder) holder).primaryContactNo.setText(bookingList.getPrimaryContactNo());
            // We will allow Engineer to take action when status has Pending Or Rescheduled
            if (bookingList.getCurrent_status().equals("Pending") ||
                    bookingList.getCurrent_status().equals("Rescheduled")) {
                ((ViewHolder) holder).buttonPanel.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).current_status.setVisibility(View.GONE);
            } else {
                ((ViewHolder) holder).buttonPanel.setVisibility(View.GONE);
                ((ViewHolder) holder).current_status.setText(bookingList.getCurrent_status());
                ((ViewHolder) holder).current_status.setVisibility(View.VISIBLE);
            }
            // Open Compete Booking Activity
            ((ViewHolder) holder).complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CompleteBookingActivity.class);
                    intent.putExtra("bookingID", bookingList.getBookingID());
                    intent.putExtra("customerName", bookingList.getCustomerName());
                    intent.putExtra("services", bookingList.getServices());
                    Log.e("Amount Due1",bookingList.getAmountDue());
                    intent.putExtra("amountDue", bookingList.getAmountDue());
                    context.startActivity(intent);
                }
            });
            //Open Cancel Booking Activity
            ((ViewHolder) holder).cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CancelBookingActivity.class);
                    intent.putExtra("bookingID", bookingList.getBookingID());
                    intent.putExtra("customerName", bookingList.getCustomerName());
                    intent.putExtra("services", bookingList.getServices());
                    context.startActivity(intent);
                }
            });
            // Call to Customer
            ((ViewHolder) holder).call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean per = misc.checkPhoneRequestPermissions();
                    if (per) {

                        try {
                            Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + bookingList.getPrimaryContactNo()));
                            context.startActivity(in);
                        } catch (SecurityException ex) {
                            Toast.makeText(context, "Could not find an activity to place the call.", Toast.LENGTH_SHORT).show();
                        }
                    }
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

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v1;
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {

            v1 = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.booking_list, parent, false);
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
        final TextView bookingID;
        final TextView services;
        final TextView customerName;
        final TextView primaryContactNo;
        final TextView address;
        final TextView current_status;
        final LinearLayout call;
        final LinearLayout complete;
        final LinearLayout cancel;
        final RelativeLayout buttonPanel;

        public ViewHolder(View view) {
            super(view);
            bookingID = view.findViewById(R.id.bookingID);
            services = view.findViewById(R.id.services);
            customerName = view.findViewById(R.id.customerName);
            primaryContactNo = view.findViewById(R.id.phonenumber);
            address = view.findViewById(R.id.address);
            call = view.findViewById(R.id.phone);
            complete = view.findViewById(R.id.complete);
            cancel = view.findViewById(R.id.cancel);
            current_status = view.findViewById(R.id.current_status);
            buttonPanel = view.findViewById(R.id.buttonPanel);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public final ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);

            progressBar = v.findViewById(R.id.progressBar1);
        }
    }
}
