package com.example.huabei_competition.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.huabei_competition.R;
import com.example.huabei_competition.db.FriendCircle;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.databinding.FragmentCommentBinding;
import com.example.huabei_competition.util.DatabaseUtil;
import com.example.huabei_competition.widget.MyRecyclerAdapter;

import java.util.List;

/**
 * Create by FanChenYang at 2021/2/28
 */
public class CommentFragment extends Fragment {


    private FragmentCommentBinding binding;
    private RequestManager glideManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        glideManager = Glide.with(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (binding != null) {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent则从parent删除，防止发生这个rootview已经有parent的错误。
            ViewGroup mViewGroup = (ViewGroup) binding.getRoot().getParent();
            if (mViewGroup != null) {
                mViewGroup.removeView(binding.getRoot());
            }
            return binding.getRoot();
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false);
        binding.setLifecycleOwner(this);
        binding.rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        initAdapter();
        return binding.getRoot();
    }

    private MyRecyclerAdapter<FriendCircle> blinkAdapter;
    private static final String TAG = "CommentFragment";
    private void initAdapter() {
        List<FriendCircle> allBlink = DatabaseUtil.getAllBlink();
        blinkAdapter = new MyRecyclerAdapter<FriendCircle>(allBlink) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_circle_friend;
            }
            @Override
            public void bindView(MyHolder holder, int position, FriendCircle circle) {
                holder.setText(circle.getName(), R.id.petName);
                holder.setText(circle.getContent(), R.id.content);
                Log.d(TAG, "bindView: " + circle.getContent());
                holder.setText(circle.getTime(), R.id.tv_sendTime);
                ImageView thumb = holder.getView(R.id.iv_thumb);
                glideManager.load(circle.getHeadPicture()).into(thumb);
                ImageView annexView = holder.getView(R.id.iv_annex);
                if (!TextUtils.isEmpty(circle.getPicture())) {
                    annexView.setVisibility(View.VISIBLE);
                    glideManager.load(circle.getPicture()).into(annexView);
                }
            }
        };
        binding.rvComments.setAdapter(blinkAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LiveDataManager.getInstance().<FriendCircle>with(CommentFragment.class.getSimpleName()).observe(getViewLifecycleOwner(), new Observer<FriendCircle>() {
            @Override
            public void onChanged(FriendCircle circle) {
                initAdapter();
            }
        });
    }
}