package com.example.huabei_competition.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.huabei_competition.R;
import com.example.huabei_competition.ui.activity.StoryChoiceActivity;
import com.example.huabei_competition.databinding.FragmentFriendsBinding;
import com.example.huabei_competition.db.Item;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.ui.activity.TalkActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SharedPreferences userData;

    public FriendsFragment() {
        // Required empty public constructor
    }

    private int type;
    private Intent intent;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        StoryChoiceActivity choiceActivity = (StoryChoiceActivity) context;
        type = choiceActivity.type;
        intent = choiceActivity.intent;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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


    public int[] icons = new int[]{R.drawable.head1, R.drawable.head2, R.drawable.head3, R.drawable.head4};
    public String[] names = new String[]{"李白", "杜甫", "苏轼", "花木兰"};
    public String[] briefIntroduce;
    private int currentPro;
    private MyRecyclerAdapter<Item> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userData = getActivity().getSharedPreferences("userData", Context.MODE_PRIVATE);
        briefIntroduce = getActivity().getResources().getStringArray(R.array.briefIntroduction);
        FragmentFriendsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false);

        binding.setLifecycleOwner(getActivity());
        binding.tvNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "------ ");
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, new ApplyFragment()).commit();
            }
        });
        binding.tvFriends.setOnClickListener(view -> {
            adapter.changeState();
        });

        binding.rvFriends.setAdapter(getFriendsAdapter(binding));

        return binding.getRoot();
    }

    private static final String TAG = "FriendsFragment";

    /**
     * @param binding binding
     * @return 构造好的friends adapter
     */
    private MyRecyclerAdapter<Item> getFriendsAdapter(FragmentFriendsBinding binding) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            Item item = new Item();
            item.setName(names[i]);
            item.setBrief(briefIntroduce[i]);
            items.add(item);
        }
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.rvFriends.setLayoutManager(manager);
        adapter = new MyRecyclerAdapter<Item>(items) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_circle_friend;
            }

            @Override
            public void bindView(MyHolder holder, final int position, Item item) {
                ImageView imageView = holder.getView(R.id.iv_thumb);
                imageView.setImageResource(icons[position]);
                holder.getView(R.id.tv_sendTime).setVisibility(View.GONE);
                holder.setText(item.getName(), R.id.petName);
                holder.setText(briefIntroduce[position], R.id.content);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(getContext(), TalkActivity.class);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }
                });
            }

        };
        return adapter;
    }


}