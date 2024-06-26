package com.ssafy.chelitalk.activity.carousel;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyAdapter extends FragmentStateAdapter {
    public int mCount;

    public MyAdapter(FragmentActivity fa, int count){
        super(fa);
        mCount=count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);

        if(index==0){
            return new FragmentFirst();
        }else if(index==1){
            return new FragmentSecond();
        }else{
            return new FragmentThird();
        }

    }

    @Override
    public int getItemCount() {
        return 2000;
    }

    private int getRealPosition(int position) {
        return position % mCount;
    }
}
