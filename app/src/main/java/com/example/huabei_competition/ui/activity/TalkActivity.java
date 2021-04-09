package com.example.huabei_competition.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.huabei_competition.R;
import com.example.huabei_competition.db.Dialogue;
import com.example.huabei_competition.db.FriendCircle;
import com.example.huabei_competition.db.NPC;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.network.api.NPCRel;
import com.example.huabei_competition.ui.fragments.CommentFragment;
import com.example.huabei_competition.base.BaseActivity;
import com.example.huabei_competition.util.DatabaseUtil;
import com.example.huabei_competition.util.MyHandler;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Create by FanChenYang
 */
public class TalkActivity extends BaseActivity implements View.OnClickListener {
    private NPC mNpc;
    private RecyclerView recyclerView;
    private MyRecyclerAdapter<DD> adapter;
    private RequestManager glideManager;
    private UserInfo myInfo;
    private Bitmap mineHead;
    private MyHandler mHandler = MyHandler.obtain(this, null);
    private TextView choice_1, choice_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        myInfo = JMessageClient.getMyInfo();
        myInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if (i == 0) {
                    mineHead = bitmap;
                }
            }
        });
        choice_1 = findViewById(R.id.tv_choice_1);
        choice_2 = findViewById(R.id.tv_choice_2);
        choice_1.setOnClickListener(this);
        choice_2.setOnClickListener(this);
        recyclerView = findViewById(R.id.talk_window);
        glideManager = Glide.with(this);
        findViewById(R.id.btn_personInfo).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        LiveDataManager.getInstance().<NPC>with(TalkActivity.class.getSimpleName()).observe(this, new Observer<NPC>() {
            @Override
            public void onChanged(NPC npc) {
                if (npc == null) {
                    MyToast.showMessage("数据加载出错");
                    finish();
                    return;
                }
                mNpc = npc;
                TextView textView = findViewById(R.id.tv_name);
                textView.setText(mNpc.getName());
                initAdapter();
                getDialogue();
            }
        });
    }

    // TODO 查找以前的 缓存 加载历史
    private void initAdapter() {
        List<Dialogue> story = DatabaseUtil.getStory(mNpc.getNPCID());
        List<DD> dds = changeStoryToDD(story);
        adapter = new MyRecyclerAdapter<DD>(dds) {
            @Override
            public int getItemViewType(int position) {
                if (getResources().get(position).type == 0)
                    return R.layout.item_friend_view_talk;
                else return R.layout.item_user_view_talk;
            }

            @Override
            public int getLayoutId(int viewType) {
                return viewType;
            }

            @Override
            public void bindView(MyHolder holder, int position, DD dialogue) {
                ImageView headImg = holder.getView(R.id.img_head);
                if (dialogue.type == 0) {
                    glideManager.load(mNpc.getHeadPicture()).into(headImg);
                } else {
                    if (mineHead != null) {
                        headImg.setImageBitmap(mineHead);
                    } else {
                        headImg.setImageResource(R.drawable.head3);
                    }
                }
                holder.setText(dialogue.content, R.id.content_wrapper);
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        refreshPosition();
    }

    private void refreshPosition() {
        int i = adapter.getResources().size() - 1;
        if (i <= 4) {
            return;
        }
        recyclerView.scrollToPosition(i);
    }

    private static class DD {
        // 用户还是对方 0:对方 1：我
        int type = 0;
        // 内容
        String content;

        public DD(int type, String content) {
            this.type = type;
            this.content = content;
        }
    }

    private String isContinue;
    private String reid;

    // 尝试获取
    private void getDialogue() {
        NPCRel.getDialogue(mNpc.getNPCID(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    NPCRel.DialogueResponse dialogueResponse = gson.fromJson(response.body().string(), NPCRel.DialogueResponse.class);
                    if (dialogueResponse.getCode().equals(LogIn.OK)) {
                        isContinue = dialogueResponse.getData().getIsContinue();
                        Dialogue info = dialogueResponse.getData().getInfo();
                        info.setNPCId(mNpc.getNPCID());
                        DatabaseUtil.saveOrUpdateDialogue(info);
                        reid = dialogueResponse.getData().getInfo().getReid();
                        if (isContinue.equals("false")) {
                            //TODO 刷新朋友圈并缓存
                            try {
                                FriendCircle pyc = dialogueResponse.getData().getPyc();
                                Calendar instance = Calendar.getInstance();
                                int year = instance.get(Calendar.YEAR);
                                int month = instance.get(Calendar.MONTH) + 1;
                                int day = instance.get(Calendar.DAY_OF_MONTH);
                                String time = year + "-" + month + "-" + day;
                                pyc.setTime(time);
                                DatabaseUtil.saveOrUpdateFriendCircle(pyc);
                                LiveDataManager.getInstance().<FriendCircle>with(CommentFragment.class.getSimpleName()).postValue(pyc);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        addToTalker(info);
                    }
                }
                response.close();
            }
        });
    }

    private void addToTalker(Dialogue dialogue) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.ll_allChoice).setVisibility(View.GONE);
            }
        });
        choice_1.setClickable(false);
        choice_2.setClickable(false);
        List<String> content = dialogue.getContent();
        for (int i = 0; i < content.size(); i++) {
            String s = content.get(i);
            if (TextUtils.isEmpty(s))
                break;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.addResource(new DD(0, s));
                    refreshPosition();
                }
            }, 1500 * i);
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //TODO 显示选项
                choice_1.setClickable(true);
                choice_2.setClickable(true);
                findViewById(R.id.ll_allChoice).setVisibility(View.VISIBLE);
                List<String> reply = dialogue.getReply();
                choice_1.setText(reply.get(0));
                if (reply.size() == 2) {
                    choice_2.setText(reply.get(1));
                    choice_2.setVisibility(View.VISIBLE);
                } else choice_2.setVisibility(View.GONE);
            }
        }, 1500 * (content.size()));
    }

    private static List<DD> changeStoryToDD(List<Dialogue> dialogues) {
        List<DD> res = new ArrayList<>();
        for (Dialogue dialogue : dialogues) {
            List<String> content = dialogue.getContent();
            List<String> reply = dialogue.getReply();
            int whichOne = dialogue.getWhichOne();
            if (whichOne == -1)
                continue;
            for (String s : content) {
                res.add(new DD(0, s));
            }
            res.add(new DD(1, reply.get(whichOne)));
        }
        return res;
    }

    private static final String TAG = "TalkActivity";

    @Override
    public void onClick(View v) {
        int which = -1;
        switch (v.getId()) {
            case R.id.btn_personInfo:
                LiveDataManager.getInstance().with(IntroduceActivity.class.getSimpleName()).setValue(mNpc);
                Intent intent = new Intent(this, IntroduceActivity.class);
//                Log.d(TAG, "onClick: " + UserUtil.sUserName);
                startActivity(intent);
                break;
            case R.id.tv_choice_1:
                if (isContinue.equals("true")) {
                    which = 0;
                }
                break;
            case R.id.tv_choice_2:
                if (isContinue.equals("true")) {
                    which = 1;
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
        if (which != -1 && !TextUtils.isEmpty(reid)) {
            List<Dialogue> byReid = DatabaseUtil.findByReid(reid);
            if (byReid.size() == 0) {
                finish();
                MyToast.showMessage("数据异常");
            }
            Dialogue dialogue = byReid.get(0);
            dialogue.setWhichOne(which);
            DatabaseUtil.saveOrUpdateDialogue(dialogue);
            String s = dialogue.getReply().get(which);
            adapter.addResource(new DD(1, s));
            refreshPosition();

            NPCRel.replyDialogue(mNpc.getNPCID(), which, reid, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        NPCRel.DialogueResponse dialogueResponse = gson.fromJson(response.body().string(), NPCRel.DialogueResponse.class);
                        if (dialogueResponse.getCode().equals(LogIn.OK)) {
                            isContinue = dialogueResponse.getData().getIsContinue();
                            Dialogue info = dialogueResponse.getData().getInfo();
                            info.setNPCId(mNpc.getNPCID());
                            DatabaseUtil.saveOrUpdateDialogue(info);
                            reid = dialogueResponse.getData().getInfo().getReid();
                            if (TextUtils.equals(isContinue, "false")) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        findViewById(R.id.ll_allChoice).setVisibility(View.GONE);
                                    }
                                });
                                //TODO 刷新朋友圈并缓存
                                FriendCircle pyc = dialogueResponse.getData().getPyc();
                                Calendar instance = Calendar.getInstance();
                                int year = instance.get(Calendar.YEAR);
                                int month = instance.get(Calendar.MONTH) + 1;
                                int day = instance.get(Calendar.DAY_OF_MONTH);
                                String time = year + "-" + month + "-" + day;
                                pyc.setTime(time);
                                DatabaseUtil.saveOrUpdateFriendCircle(pyc);
                                LiveDataManager.getInstance().<FriendCircle>with(CommentFragment.class.getSimpleName()).postValue(pyc);
                            } else
                                addToTalker(info);
                        }
                    }
                    response.close();
                }
            });
        }
    }
}