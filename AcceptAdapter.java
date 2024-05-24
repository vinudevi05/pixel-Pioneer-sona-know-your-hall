package com.example.helloworld;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AcceptAdapter extends FirestoreRecyclerAdapter<request_fetch, AcceptAdapter.AcceptHolder> {

    public AcceptAdapter(@NonNull FirestoreRecyclerOptions<request_fetch> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AcceptHolder holder, int position, @NonNull request_fetch model) {
        holder.tv_dept.setText(model.getDept());
        holder.tv_date.setText(model.getDate());
        holder.tv_name.setText(model.getName());
        holder.tv_venue.setText(model.getVenue());
        holder.tv_time.setText(model.getTime());

    }

    @NonNull
    @Override
    public AcceptHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accept_card,parent,false);
        return new AcceptHolder(view);
    }

    class AcceptHolder extends RecyclerView.ViewHolder{
        TextView tv_name,tv_dept,tv_venue,tv_date,tv_time;
        public AcceptHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.acc_date);
            tv_name = itemView.findViewById(R.id.acc_name);
            tv_time = itemView.findViewById(R.id.acc_time);
            tv_dept = itemView.findViewById(R.id.acc_dept);
            tv_venue = itemView.findViewById(R.id.acc_venue);
        }
    }

}
