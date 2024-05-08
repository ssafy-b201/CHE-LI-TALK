package com.ssafy.chelitalk.api.history;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.activity.english.HistoryDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context context;
    private List<History> historyList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

    public HistoryAdapter(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        History history = historyList.get(position);
        String dateText = dateFormat.format(history.getCreatedAt());
        String timeText = "\n" + timeFormat.format(history.getCreatedAt());

        SpannableString spannable = new SpannableString(dateText + timeText);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, dateText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(1.2f), 0, dateText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(0.8f), dateText.length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



        holder.historyText.setText(spannable);

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    History clickedHistory = historyList.get(position);
                    Date createdAt = clickedHistory.getCreatedAt();

                    if(createdAt != null){
                        long timestamp = createdAt.getTime();
                        Intent intent = new Intent(context, HistoryDetailActivity.class);
                        intent.putExtra("history-createdAt", timestamp);
                        context.startActivity(intent);
                    }else{
                        Log.e("HistoryAdapter", "createdAt이 비었습니다.");
                    }

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView historyText;

        public ViewHolder(View itemView) {
            super(itemView);
            historyText = itemView.findViewById(R.id.historyText);
        }
    }
}
