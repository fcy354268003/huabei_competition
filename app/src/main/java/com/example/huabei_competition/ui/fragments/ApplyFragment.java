package com.example.huabei_competition.ui.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentApplyBinding;
import com.example.huabei_competition.db.FriendApply;
import com.example.huabei_competition.event.FriendManager;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyToast;

import cn.jpush.im.api.BasicCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ApplyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ApplyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ApplyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ApplyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ApplyFragment newInstance(String param1, String param2) {
        ApplyFragment fragment = new ApplyFragment();
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

    LiveData<FriendApply> liveData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentApplyBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apply, container, false);
        binding.setLifecycleOwner(this);
        binding.setCallBack(this);

        liveData = LiveDataManager.getInstance().with(this.getClass().getSimpleName());
        liveData.observe(getViewLifecycleOwner(), new Observer<FriendApply>() {
            @Override
            public void onChanged(FriendApply friendApply) {
                String reason = friendApply.getReason();
                String userName = friendApply.getUserName();
                Log.d(TAG, "onChanged: " + reason + "\n" + userName);
            }
        });
        return binding.getRoot();
    }

    private static final String TAG = "----------------";

    public void onClickApplyFriend() {
        final CustomerDialog customerDialog = new CustomerDialog();
        customerDialog.setLayoutId(R.layout.apply_fragment_dialog);
        customerDialog.setCallback(new CustomerDialog.InitCallback() {
            @Override
            public void initWidget(View rootView) {
                EditText reason = rootView.findViewById(R.id.et_reason);
                EditText userName = rootView.findViewById(R.id.et_userInfo);

                rootView.findViewById(R.id.et_reason);
                rootView.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String res = reason.getText().toString();
                        final String user = userName.getText().toString();
                        // 调用添加好友的办法
                        FriendManager.sendInvitationRequest(user, null, res, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                Log.d(TAG, "gotResult: " + s);
                                if (i == 0) {
                                    MyToast.showMessage("请求发送成功");
                                } else {
                                    MyToast.showMessage("请求发送失败");
                                }
                            }
                        });
                    }
                });
                rootView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customerDialog.dismiss();
                    }
                });
            }
        });
        customerDialog.show(getActivity().getSupportFragmentManager(), "apply");
    }

    public void back() {

    }

}