package com.ssafy.chelitalk.api.historydetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ssafy.chelitalk.R;
import java.util.List;

public class HistoryDetailAdapter extends RecyclerView.Adapter<HistoryDetailAdapter.ViewHolder> {

    private List<HistoryDetailResponseDto> historyDetails;
    private static final int MESSAGE_SENT = 1;
    private static final int MESSAGE_RECEIVED = 2;

    public HistoryDetailAdapter(List<HistoryDetailResponseDto> historyDetails){
        this.historyDetails = historyDetails;
    }

    @Override
    public int getItemViewType(int position) {
        HistoryDetailResponseDto message = historyDetails.get(position);
        if (message.getSentenceSender().equals("gpt")) {  // Assuming there's a method to determine if the message was sent or received
            return MESSAGE_RECEIVED;
        } else {
            return MESSAGE_SENT;
        }
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryDetailResponseDto message = historyDetails.get(position);
        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).textViewMessage.setText(message.getSentenceContent());
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).textViewMessage.setText(message.getSentenceContent());
        }
    }

    @Override
    public int getItemCount() {
        return historyDetails != null ? historyDetails.size() : 0;
    }

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class SentMessageViewHolder extends ViewHolder {
        TextView textViewMessage;

        public SentMessageViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessageSent);
        }
    }

    public static class ReceivedMessageViewHolder extends ViewHolder {
        TextView textViewMessage;

        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessageReceived);
        }
    }
}
