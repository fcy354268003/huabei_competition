package com.example.huabei_competition.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FamousQuotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FamousQuotesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FamousQuotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FamousQuotesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FamousQuotesFragment newInstance(String param1, String param2) {
        FamousQuotesFragment fragment = new FamousQuotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
        textView.setText(application.getmQuote());
        return inflate;
    }

}