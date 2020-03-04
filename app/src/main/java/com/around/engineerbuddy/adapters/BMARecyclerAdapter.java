package com.around.engineerbuddy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class BMARecyclerAdapter extends RecyclerView.Adapter   {
    Context context;
    List<?> list;
    RecyclerView recyclerView;
    BMAListRowCreator bmaListRowCreator;
    int layoutId;
    View convertedView;

    public BMARecyclerAdapter(Context context,List<?> list,RecyclerView recyclerView,BMAListRowCreator bmaListRowCreator,int layoutID){
        this.context=context;
        this.list=list;
        this.recyclerView=recyclerView;
        this.bmaListRowCreator=bmaListRowCreator;
        this.layoutId=layoutID;

    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        convertedView = LayoutInflater.from(context).inflate(layoutId, viewGroup, false);
        return new ViewHolder(convertedView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Object rowObject = this.list.get(i);
            bmaListRowCreator.createRow(viewHolder,viewHolder.itemView, rowObject, i);


    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        View itemView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public interface BMAListRowCreator {
        <T> void createRow(View convertView, T rowObject);

        <T> void createRow(RecyclerView.ViewHolder viewHolder,View itemView, T rowObject, int position);

        <T> void createHeader(View convertView, T rowObject);
    }
    public HashMap<Integer, RecyclerView.ViewHolder> holderHashMap = new HashMap<>();
    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        holderHashMap.put(holder.getAdapterPosition(),holder);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        holderHashMap.remove(holder.getAdapterPosition());
        super.onViewAttachedToWindow(holder);

    }
}
