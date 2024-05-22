package com.ssafy.chelitalk.api.check;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.api.historydetail.HistoryDetailResponseDto;

import java.util.List;

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.ViewHolder> {

    private List<HistoryDetailResponseDto> checkDetails;
    private static final int MESSAGE_SENT = 1;
    private static final int MESSAGE_RECEIVED = 2;

    public CheckAdapter(List<HistoryDetailResponseDto> checkDetails){
        this.checkDetails = checkDetails;
    }

    @Override
    public int getItemViewType(int position) {
        HistoryDetailResponseDto message = checkDetails.get(position);
        if (message.getSentenceSender().equals("gpt")) {
            return MESSAGE_RECEIVED;
        } else {
            return MESSAGE_SENT;
        }
    }

    @NonNull
    @Override
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
        HistoryDetailResponseDto message = checkDetails.get(position);
        if (holder instanceof CheckAdapter.SentMessageViewHolder) {
            ((CheckAdapter.SentMessageViewHolder) holder).textViewMessage.setText(message.getSentenceContent());
        } else if (holder instanceof CheckAdapter.ReceivedMessageViewHolder) {
            ((CheckAdapter.ReceivedMessageViewHolder) holder).textViewMessage.setText(message.getSentenceContent());
        }
    }

    @Override
    public int getItemCount() {
        return checkDetails != null ? checkDetails.size() : 0;
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
