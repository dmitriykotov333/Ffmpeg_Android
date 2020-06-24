package com.kotov.ffmpeg.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kotov.ffmpeg.R;
import androidx.fragment.app.Fragment;


public class TabFragment1 extends Fragment {


    public TabFragment1() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment, container, false);
    }

}