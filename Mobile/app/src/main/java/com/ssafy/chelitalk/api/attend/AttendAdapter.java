package com.ssafy.chelitalk.api.attend;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ssafy.chelitalk.R;

import java.util.List;

public class AttendAdapter extends RecyclerView.Adapter<AttendAdapter.AttendViewHolder> {

    private List<AttendListDto> attendList;
    public AttendAdapter(List<AttendListDto> attendList){
        this.attendList = attendList;
    }

    @NonNull
    @Override
    public AttendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attend_item, parent, false);
        return new AttendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendViewHolder holder, int position) {
        AttendListDto item = attendList.get(position);
        holder.attendTextView.setText(item.toString()); // "T" or "F"
        Log.d("AttendAdapter", "Binding position " + position + " with value " + item.toString());
    }

    @Override
    public int getItemCount() {
        return attendList.size();
    }

    static class AttendViewHolder extends RecyclerView.ViewHolder {
        TextView attendTextView;

        AttendViewHolder(View itemView) {
            super(itemView);
            attendTextView = itemView.findViewById(R.id.attendTextView); // ID for the text view in your item layout
        }
    }
}
