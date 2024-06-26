package com.ssafy.chelitalk.activity.carousel;

import android.graphics.Color;
import android.graphics.Typeface;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.ssafy.chelitalk.R;

public class FragmentThird extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_3p, container,false
        );
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        TextView tvName3 = view.findViewById(R.id.tvName3);
        String text = getResources().getString(R.string.default_text3);
        SpannableString spannableString = new SpannableString(text);

        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.galmuri9);

        int start = text.indexOf("ADVICES FOR");
        int end = text.indexOf("매일");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#DE3163")), start, end , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(25, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new CustomTypefaceScan(typeface), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int restStart = text.indexOf("매일");
        int restEnd = text.length();
        spannableString.setSpan(new AbsoluteSizeSpan(18, true), restStart, restEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvName3.setText(spannableString);

    }
}
