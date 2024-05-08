package com.ssafy.chelitalk.api.check;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ssafy.chelitalk.R;

import java.util.List;

public class GrammarAdapter extends RecyclerView.Adapter<GrammarAdapter.ViewHolder> {

    private List<String> grammarDetails;

    public GrammarAdapter(List<String> grammarDetails) {
        this.grammarDetails = grammarDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grammar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String detail = (position + 1) + "번 문장에 대한 피드백 : " + grammarDetails.get(position);
        holder.grammarText.setText(detail);
    }

    @Override
    public int getItemCount() {
        return grammarDetails.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView grammarText;

        public ViewHolder(View itemView) {
            super(itemView);
            grammarText = itemView.findViewById(R.id.grammarText);
        }
    }
}