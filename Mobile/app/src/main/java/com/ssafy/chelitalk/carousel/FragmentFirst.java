package com.ssafy.chelitalk.carousel;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ssafy.chelitalk.R;

public class FragmentFirst extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_1p, container,false
        );
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        TextView tvName1 = view.findViewById(R.id.tvName1);
        String text = getResources().getString(R.string.default_text);
        SpannableString spannableString = new SpannableString(text);

        int start = text.indexOf("CHECK");
        int end = text.indexOf("?")+1;
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#DE3163")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(25, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int restStart = text.indexOf("문법 체크");
        int restEnd = text.length();
        spannableString.setSpan(new AbsoluteSizeSpan(18, true), restStart, restEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvName1.setText(spannableString);
    }
}
