package com.around.engineerbuddy.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.around.engineerbuddy.R;
import com.around.engineerbuddy.adapters.BMARecyclerAdapter;

import java.util.ArrayList;

public class CovidFragment extends BMAFragment {

    RecyclerView covidRecyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.covid_activity, container, false);
        this.covidRecyclerView = this.view.findViewById(R.id.covidRecyclerView);
        this.view.findViewById(R.id.backgroundView).setVisibility(View.INVISIBLE);

        this.view.findViewById(R.id.next).setVisibility(View.GONE);
        BMARecyclerAdapter bmaRecyclerAdapter = new BMARecyclerAdapter(getContext(), this.getCovidMessageList(), covidRecyclerView, this, R.layout.covid_item);
        covidRecyclerView.setHasFixedSize(true);
        covidRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        covidRecyclerView.setAdapter(bmaRecyclerAdapter);
        return this.view;
    }
    public ArrayList<String> getCovidMessageList() {
        ArrayList<String> covidMessageList = new ArrayList<>();

        covidMessageList.add("Technician Temperature to be checked before issuing calls.");
        covidMessageList.add("Face mask, Hand Gloves, Hand sanitizer are mandatory.");
        covidMessageList.add("No Sign to be taken on any document.");
        covidMessageList.add(" Call Customer on phone from door. Do not use Door bell.");
        covidMessageList.add("Wash hands before work start, Wash hands after work finishes.");
        covidMessageList.add("If customer looks unwell (Cough, fever) no work to be done just apologise and leave.");
        covidMessageList.add(" Customer to stand at a safe distance 3 feet from technician and helper.");
        covidMessageList.add("Leave all your belongings like helmet etc outside the customer house.");
        covidMessageList.add("Helper to follow same guidelines and technician to make sure all the above for helper.");
        return covidMessageList;
    }
    @Override
    public <T> void createRow(RecyclerView.ViewHolder viewHolder, View itemView, T rowObject, int position) {
        String message= (String) rowObject;
        TextView count=itemView.findViewById(R.id.count);
        TextView messagetext=itemView.findViewById(R.id.covidmessage);
        messagetext.setText(message);
        count.setText(position+1+". ");

    }

}
