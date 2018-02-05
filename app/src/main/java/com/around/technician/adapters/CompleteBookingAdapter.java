package com.around.technician.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.around.technician.BookingGetterSetter;
import com.around.technician.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by abhay on 2/1/18.
 */

public class CompleteBookingAdapter extends RecyclerView.Adapter {

    public final int VIEW_ITEM = 1;
    public List<BookingGetterSetter> list;
    public Context context;
    View v1;
    RecyclerView.ViewHolder vh;
    PriceTagsAdapter mAdapter;
    BookingGetterSetter customUnitList;
    ViewHolder viewHolder;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Activity activity;
    String SN_DIRECTORY = Environment.getExternalStorageDirectory() + "/AroundSerialNO/";
    String bookingID;


    public CompleteBookingAdapter(List<BookingGetterSetter> list, RecyclerView recyclerView) {

        this.list = list;
        context = recyclerView.getContext();
        activity = (Activity) context;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {

            final BookingGetterSetter unitList = list.get(position);

            bookingID = unitList.getBookingID();
            ((ViewHolder) holder).brand.setText(unitList.getBrand());
            ((ViewHolder) holder).category.setText(unitList.getCategory());
            ((ViewHolder) holder).capacity.setText(unitList.getCapacity());


            LinearLayoutManager linearLayout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true);
            ((ViewHolder) holder).recyclerview.setLayoutManager(linearLayout);


            mAdapter = new PriceTagsAdapter(unitList.getUnitList(), ((ViewHolder) holder).recyclerview);
            ((ViewHolder) holder).recyclerview.setAdapter(mAdapter);
            boolean isRepairBooking = unitList.getRepairBooking();
            if (isRepairBooking) {
                ((ViewHolder) holder).brokenText.setVisibility(View.GONE);
                ((ViewHolder) holder).brokenRadio.setVisibility(View.GONE);
                unitList.setApplianceBroken(false);
                for (int i = 0; i < unitList.getUnitList().size(); i++) {

                    unitList.getUnitList().get(i).setApplianceBroken(false);

                }
                //notifyDataSetChanged();
            } else {
                ((ViewHolder) holder).brokenRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton rb = group.findViewById(checkedId);
                        if (null != rb && checkedId > -1) {
                            //Toast.makeText(context, rb.getText() + "" +checkedId, Toast.LENGTH_SHORT).show();
                            if (rb.getText().equals("Yes")) {
                                unitList.setApplianceBroken(true);

                                for (int i = 0; i < unitList.getUnitList().size(); i++) {
                                    if(Float.parseFloat( unitList.getUnitList().get(i).getCustomerNetPayable()) == 0 &&
                                            unitList.getUnitList().get(i).getPriceTags().equals(context.getResources().getString(R.string.wall_mount_stand))    ){
                                        unitList.getUnitList().get(i).setApplianceBroken(true);
                                        unitList.getUnitList().get(i).setDelivered(false);
                                    }

                                }

                                notifyDataSetChanged();

                            } else {
                                unitList.setApplianceBroken(false);
                                for (int i = 0; i < unitList.getUnitList().size(); i++) {

                                    unitList.getUnitList().get(i).setApplianceBroken(false);
                                }
                                notifyDataSetChanged();
                            }
                        }
                    }
                });
            }


            if (unitList.getPod().equals("1")) {

                ((ViewHolder) holder).sNCamera.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).serialNo.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).SnText.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).SnPic.setVisibility(View.VISIBLE);


                ((ViewHolder) holder).sNCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (((ViewHolder) holder).serialNo.getText().toString().isEmpty()) {

                            Toast.makeText(context, R.string.serialNumberRequiredMsg, Toast.LENGTH_SHORT).show();
                            Snackbar.make(v, R.string.serialNumberRequiredMsg, Snackbar.LENGTH_LONG).show();
                        } else if(TextUtils.isDigitsOnly(((ViewHolder) holder).serialNo.getText()) &&
                                Double.parseDouble(((ViewHolder) holder).serialNo.getText().toString()) <= 0){
                            Snackbar.make(v, R.string.validSerialNumberMsg, Snackbar.LENGTH_LONG).show();
                        } else {


                            viewHolder = ((ViewHolder) holder);
                            unitList.setSerialNo(((ViewHolder) holder).serialNo.getText().toString());
                            customUnitList = unitList;
                            notifyDataSetChanged();
                            //Start Camera Activity
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            ((Activity) context).startActivityForResult(intent, REQUEST_CAMERA);
                        }
                    }
                });
            } else {

                ((ViewHolder) holder).sNCamera.setVisibility(View.GONE);
                ((ViewHolder) holder).serialNo.setVisibility(View.GONE);
                ((ViewHolder) holder).SnText.setVisibility(View.GONE);
                ((ViewHolder) holder).SnPic.setVisibility(View.GONE);
            }


        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public List<BookingGetterSetter> getUnitData() {
        return list;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // Log.d("MyAdapter", "onActivityResult");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                // onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {

                onCaptureImageResult(data);
            }
        }
    }

    /**
     * Capture Image from App
     * @param data
     */
    public void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 90, bytes);

        String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        String serialNoPath = bookingID + "_" + pic_name + ".png";
        File destination = new File(SN_DIRECTORY, serialNoPath);
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            String encodeImage = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
            customUnitList.setSerialNoUrl(encodeImage);
            customUnitList.setSerialNoBitmap(thumbnail);
            viewHolder.sNCamera.setImageBitmap(thumbnail);

            notifyDataSetChanged();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        if (viewType == VIEW_ITEM) {

            v1 = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.appliace_list, parent, false);
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
        TextView bookingID, services, brand, category, capacity, brokenText, SnText, SnPic;
        RecyclerView recyclerview;
        RadioGroup brokenRadio;
        EditText serialNo;
        ImageView sNCamera;

        public ViewHolder(View view) {
            super(view);
            brokenText = view.findViewById(R.id.broken);
            SnText = view.findViewById(R.id.sN);
            SnPic = view.findViewById(R.id.sNPic);
            bookingID = view.findViewById(R.id.bookingID);
            services = view.findViewById(R.id.services);
            bookingID = view.findViewById(R.id.bookingID);
            brand = view.findViewById(R.id.brand);
            category = view.findViewById(R.id.category);
            capacity = view.findViewById(R.id.capacity);
            recyclerview = view.findViewById(R.id.child_recyclerview);
            brokenRadio = view.findViewById(R.id.brokenRadio);
            sNCamera = view.findViewById(R.id.sNCamera);
            serialNo = view.findViewById(R.id.serialNo);

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

