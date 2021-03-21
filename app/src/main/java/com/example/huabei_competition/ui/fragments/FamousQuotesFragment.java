package com.example.huabei_competition.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.R;


public class FamousQuotesFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_famous_quotes, container, false);
        inflate.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(FamousQuotesFragment.this).commit();
            }
        });
        TextView textView = inflate.findViewById(R.id.tv_quote);
        MyApplication application = (MyApplication) getActivity().getApplication();
        String s = application.loadOneQuote();
        Log.d(TAG, "onCreateView: " + s);
        textView.setText(s);
        return inflate;
    }

    private static final String TAG = "FamousQuotesFragment";
}