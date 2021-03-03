package com.example.huabei_competition.ui.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;

import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.huabei_competition.R;
import com.example.huabei_competition.callback.RankListCallback;
import com.example.huabei_competition.databinding.FragmentRankListBinding;
import com.example.huabei_competition.databinding.ItemRanklistBinding;
import com.example.huabei_competition.db.Dialogue;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.network.api.RankList;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.util.MyHandler;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Create by FanChenYang at 2021/2/8
 */
public class RankListFragment extends Fragment implements RankListCallback {


    private FragmentRankListBinding binding;
    private MyHandler myHandler;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myHandler = MyHandler.obtain(context, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding != null) {
            ViewGroup group = (ViewGroup) binding.getRoot().getParent();
            if (group != null) {
                group.removeView(binding.getRoot());
                return binding.getRoot();
            }
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rank_list, container, false);
        binding.setLifecycleOwner(this);
        binding.setCallback(this);
        initSelectedIndicator();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvRank.setLayoutManager(linearLayoutManager);
        return binding.getRoot();
    }

    private int selected = 1;
    private MyRecyclerAdapter<RankList.Emerging.DataDTO.InfoDTO> emergingAdapter;
    private MyRecyclerAdapter<RankList.General.DataDTO.InfoDTO> generalAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
    }

    /**
     * 初始化列表的adapter，同时加载两条内容，缓存第二条内容
     */
    private void initAdapter() {
        RankList.getEmerging(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showMessage("加载数据失败");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                Log.d(TAG, "onResponse: " + resp);
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    RankList.Emerging emerging = gson.fromJson(resp, RankList.Emerging.class);
                    if (emerging.getCode().equals(LogIn.OK)) {
                        myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                initEmerging(emerging);
                            }
                        });
                    } else onFailure(call, new IOException());
                } else {
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.showMessage("加载数据失败");
                        }
                    });
                }
                response.close();
            }
        });
        RankList.getGeneralList(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showMessage("加载数据失败");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                Gson gson = new Gson();
                Log.d(TAG, "onResponse: " + resp);
                if (response.isSuccessful()) {
                    RankList.General general = gson.fromJson(resp, RankList.General.class);
                    if (general.getCode().equals(LogIn.OK)) {
                        myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                initGeneral(general);
                            }
                        });
                    } else onFailure(call, new IOException());
                }
                response.close();

            }
        });
    }

    /**
     * 初始化总排行榜
     */
    private void initGeneral(RankList.General general) {
        generalAdapter = new MyRecyclerAdapter<RankList.General.DataDTO.InfoDTO>(general.getData().getInfo()) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_ranklist;
            }

            @Override
            public void bindView(MyHolder holder, int position, RankList.General.DataDTO.InfoDTO emerging) {
                ItemRanklistBinding binding = (ItemRanklistBinding) holder.getBinding();
                JMessageClient.getGroupInfo(Long.parseLong(emerging.getGroupId()), new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, GroupInfo groupInfo) {
                        if (i == 0) {
                            binding.btnJoin.setOnClickListener(view -> {
                                LiveDataManager.getInstance()
                                        .<GroupInfo>with(GroupInfoFragment.class.getSimpleName())
                                        .setValue(groupInfo);
                                ((MainActivity) getActivity()).getController().navigate(R.id.action_mainFragment_to_groupInfoFragment);
                            });
                            groupInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                                @Override
                                public void gotResult(int i, String s, Bitmap bitmap) {
                                    if (i == 0) {
                                        binding.ivPortrait.setImageBitmap(bitmap);
                                    }
                                }
                            });
                            String groupName = groupInfo.getGroupName();
                            binding.tvGroupN.setText(groupName);
                        }
                    }
                });
                int i = position + 1;
                binding.tvRanking.setText(String.valueOf(i));
                WidgetUtil.setCustomerText(binding.tvRanking, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
                String yesterday = getActivity().getResources().getString(R.string.sum_time_group);
                Log.d(TAG, "bindView: pppppppppp" + yesterday);
                binding.tvAverYes.setText(yesterday.replace("p", emerging.getAllTime()));
                JMessageClient.getGroupMembers(Long.parseLong(emerging.getGroupId()), new RequestCallback<List<GroupMemberInfo>>() {
                    @Override
                    public void gotResult(int i, String s, List<GroupMemberInfo> groupMemberInfos) {
                        if (i == 0) {
                            binding.tvPeopNum.setText(String.valueOf(groupMemberInfos.size()));
                        }
                    }
                });

            }
        };
    }

    /**
     * 初始化新晋榜
     *
     */
    private void initEmerging(RankList.Emerging emerging) {
        emergingAdapter = new MyRecyclerAdapter<RankList.Emerging.DataDTO.InfoDTO>(emerging.getData().getInfo()) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_ranklist;
            }

            @Override
            public void bindView(MyHolder holder, int position, RankList.Emerging.DataDTO.InfoDTO emerging) {
                ItemRanklistBinding binding = (ItemRanklistBinding) holder.getBinding();
                JMessageClient.getGroupInfo(Long.parseLong(emerging.getId()), new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, GroupInfo groupInfo) {
                        if (i == 0) {
                            binding.btnJoin.setOnClickListener(view -> {
                                LiveDataManager.getInstance()
                                        .<GroupInfo>with(GroupInfoFragment.class.getSimpleName())
                                        .setValue(groupInfo);
                                ((MainActivity) getActivity()).getController().navigate(R.id.action_mainFragment_to_groupInfoFragment);
                            });
                            groupInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                                @Override
                                public void gotResult(int i, String s, Bitmap bitmap) {
                                    if (i == 0) {
                                        binding.ivPortrait.setImageBitmap(bitmap);
                                    }
                                }
                            });
                            String groupName = groupInfo.getGroupName();
                            binding.tvGroupN.setText(groupName);
                        }
                    }
                });
                int i = position + 1;
                binding.tvRanking.setText(String.valueOf(i));
                WidgetUtil.setCustomerText(binding.tvRanking, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
                String yesterday = getActivity().getResources().getString(R.string.yesterday);
                binding.tvAverYes.setText(yesterday.replace("?", emerging.getTime()));
                JMessageClient.getGroupMembers(Long.parseLong(emerging.getId()), new RequestCallback<List<GroupMemberInfo>>() {
                    @Override
                    public void gotResult(int i, String s, List<GroupMemberInfo> groupMemberInfos) {
                        if (i == 0) {
                            binding.tvPeopNum.setText(String.valueOf(groupMemberInfos.size()));
                        }
                    }
                });

            }
        };
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                binding.rvRank.setAdapter(emergingAdapter);
            }
        });
    }

    private void initSelectedIndicator() {
        binding.tvNewRank.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        binding.tvNewRank.setTextColor(getResources().getColor(R.color.tabSelected_group));
        binding.tvNewRank.setOnClickListener(new_ -> {
            if (selected == 1)
                return;
            if (emergingAdapter != null)
                binding.rvRank.setAdapter(emergingAdapter);
            selected = 1;
            binding.tvRank.setTextSize(18);
            binding.tvNewRank.setTextSize(40);
            binding.tvNewRank.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            binding.tvNewRank.setTextColor(getResources().getColor(R.color.tabSelected_group));
            binding.tvRank.setTextColor(getResources().getColor(R.color.tabUnSelected_group));
            binding.tvRank.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
        });
        binding.tvRank.setOnClickListener(rank -> {
            if (selected == 2)
                return;
            if (generalAdapter != null)
                binding.rvRank.setAdapter(generalAdapter);
            selected = 2;
            binding.tvNewRank.setTextSize(18);
            binding.tvRank.setTextSize(40);
            binding.tvRank.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            binding.tvRank.setTextColor(getResources().getColor(R.color.tabSelected_group));
            binding.tvNewRank.setTextColor(getResources().getColor(R.color.tabUnSelected_group));
            binding.tvNewRank.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
        });
    }

    private static final String TAG = "RankListFragment";

    @Override
    public void onSearchClick() {
        ((MainActivity) getActivity()).getController().navigate(R.id.action_mainFragment_to_searchFragment);
    }

    @Override
    public void onAddClick() {
        ((MainActivity) getActivity()).getController().navigate(R.id.action_mainFragment_to_createStudyRoomFragment);
    }

    @Override
    public void onRuleClick() {
        CustomerDialog customerDialog = new CustomerDialog();
        customerDialog.setLayoutId(R.layout.dialog_rule);
        customerDialog.setCallback(new CustomerDialog.InitCallback() {
            @Override
            public void initWidget(View rootView) {
                rootView.findViewById(R.id.btn_iKnow).setOnClickListener(view -> {
                    customerDialog.dismiss();
                });
            }
        });
        customerDialog.show(getActivity().getSupportFragmentManager(), "rules");
    }
}