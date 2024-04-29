package com.ssafy.chelitalk.carousel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.ssafy.chelitalk.R;

public class FragmentFirst extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_1p, container,false
        );
        return rootView;
    }
}
