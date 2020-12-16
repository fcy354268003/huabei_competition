package com.example.huabei_competition.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.db.PlotCache;
import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Create by FanChenYang
 */
public class TalkActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int TYPE_CALLBACK = 0;
    public static final int TYPE_TALK = 1;
    public static final String URL_GET_PLOTS = MyApplication.URL + "/app/getPlots?";
    public static final String URL_PUT_FAVOR = MyApplication.URL + "/app/updateFavor";
    private RecyclerView mRecyclerView;
    private Button mPersonInfo;
    private Gson gson = new Gson();
    private Adapter adapter;
    private MyHandler handler = new MyHandler(this);
    private TextView mChoice1, mChoice2;
    private int mType, mStory, mCurrentProgress;
    private int imgIdHead, imgIdBody;
    private int currentProgress;
    private List<PlotCache> currentContent;
    private int data_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        SharedPreferences userData = getSharedPreferences("userData", MODE_PRIVATE);
        currentProgress = userData.getInt("" + mStory, getResources().getIntArray(R.array.array_plot)[mStory]);
        mType = getIntent().getIntExtra("type", 0);
        mStory = getIntent().getIntExtra("position", 0);
        Log.d(TAG, "onContentChanged: " + mStory + "type:" + mType + "currentProgress:" + currentProgress);
        initComponents();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 点击空白 隐藏软键盘
        InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (event.getAction() == KeyEvent.ACTION_DOWN && getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            methodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_personInfo:
                // 跳转到角色详情界面
                Intent intent = new Intent(this, IntroduceActivity.class);
                intent.putExtra("story", mStory);
                intent.putExtra("imgIdHead", imgIdHead);
                intent.putExtra("imgIdBody", imgIdBody);
                startActivity(intent);
                break;
            case R.id.tv_choice_1:
            case R.id.tv_choice_2:
                if (v.getTag() != null) {
                    Parser.Plot plotId = (Parser.Plot) v.getTag();
                    putFavor(plotId);
                    mChoice1.setTag(null);
                    mChoice2.setTag(null);
                }
                break;
            default:
        }
    }

    private void putFavor(final Parser.Plot plotId) {
        FormBody formBody = new FormBody.Builder().add("plotId", plotId.plotId).build();
        Request request = new Request.Builder().put(formBody).url(URL_PUT_FAVOR).build();
        ((MyApplication) getApplication()).okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                Log.d(TAG, "onResponse: " + "putFavor success");
                if (response.isSuccessful()) {
                    response.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PlotCache plotCache = new PlotCache();
                            plotCache.setWhose(2);
                            plotCache.setPlot_id(plotId.plotId);
                            plotCache.setContent(plotId.content);
                            plotCache.setStory(mStory);
                            plotCache.save();
                            adapter.addResource(plotCache);
                            currentProgress = data_1;
                            handler.sendEmptyMessage(11);

                        }
                    });
                }
            }
        });
    }

    private void setBackPIC() {
        Log.d(TAG, "setBackPIC: ");
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try (InputStream inputStream = new URL("http://wtkoss.weituk.com/wp-content/uploads/2020/01/IMG_3982.jpg").openStream()) {
                    final Drawable drawable = Drawable.createFromStream(inputStream, "stream");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.ll_back).setBackground(drawable);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void progressForward() {
        int a = currentProgress + 1;
        Request request = new Request.Builder().url(URL_GET_PLOTS + "num=" + a).build();
        ((MyApplication) getApplication()).okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    //解析数据并构造成 PlotCache对象 用handler传送
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String data1 = null;
                        data1 = jsonObject.getString("data");
                        Gson gson = new Gson();
                        final Parser parser = gson.fromJson(data1, Parser.class);
                        // 是角色的语言
                        if (parser.status == 2) {
                            handler.sendEmptyMessage(22);
                        }
                        if (parser.plots.get(0).whose.equals("李白") || parser.plots.get(0).whose.equals("杜甫")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (Parser.Plot plot : parser.plots) {
                                        PlotCache plotCache = new PlotCache();
                                        plotCache.setContent(plot.content);
                                        plotCache.setPlot_id(plot.plotId);
                                        plotCache.setWhose(1);
                                        adapter.addResource(plotCache);
                                        plotCache.save();
                                        currentProgress = parser.id;
                                        progressForward();
                                    }
                                }
                            });
                        } else if (parser.plots.get(0).whose.equals("我")) {
                            data_1 = parser.id;
                            Log.d(TAG, "onResponse: " + parser.status + "id:" + parser.id);
                            // 发送来的 内容 是 用户的
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch (parser.status) {
                                        case 0:
                                            mChoice1.setText(parser.plots.get(0).content);
                                            mChoice2.setVisibility(View.GONE);
                                            mChoice1.setTag(parser.plots.get(0));
                                            break;
                                        case 1:
                                            mChoice2.setVisibility(View.VISIBLE);
                                            mChoice1.setText(parser.plots.get(0).content);
                                            mChoice2.setText(parser.plots.get(1).content);
                                            mChoice1.setTag(parser.plots.get(0));
                                            mChoice2.setTag(parser.plots.get(1));
                                            break;
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor userData = getSharedPreferences("userData", MODE_PRIVATE).edit();
        userData.putInt("" + mStory, currentProgress);
        userData.apply();
    }

    private static class Parser {
        @SerializedName("id")
        public int id;
        @SerializedName("status")
        public int status;
        @SerializedName("plot_list")
        public List<Plot> plots = new ArrayList<>();

        private static class Plot {
            @SerializedName("plot_id")
            public String plotId;
            @SerializedName("name")
            public String whose;
            @SerializedName("string")
            public String content;
        }
    }

    private void initComponents() {
        TextView tvName = findViewById(R.id.tv_name);
        tvName.setText(getResources().getStringArray(R.array.storyName)[mStory]);
        Log.d(TAG, "initComponents: ");
        mChoice1 = findViewById(R.id.tv_choice_1);
        mChoice2 = findViewById(R.id.tv_choice_2);
        mPersonInfo = findViewById(R.id.btn_personInfo);
        mRecyclerView = findViewById(R.id.talk_window);
        mPersonInfo.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        showMemory();
        if (mType == TYPE_CALLBACK) {
            Log.d(TAG, "initComponents: " + "开始回忆");
            findViewById(R.id.ll_allChoice).setVisibility(View.GONE);
        } else if (mType == TYPE_TALK) {
            progressForward();
        }
        setBackPIC();
        mChoice1.setOnClickListener(this);
        mChoice2.setOnClickListener(this);
        mCurrentProgress = initData();
        switch (mStory) {
            case 0:
                imgIdHead = R.drawable.head1;
                imgIdBody = R.drawable.body1;
                break;
            case 1:
                imgIdHead = R.drawable.head2;
                imgIdBody = R.drawable.body2;
                break;
            case 2:
                imgIdHead = R.drawable.head3;
                imgIdBody = R.drawable.body3;
                break;
            case 3:
                imgIdHead = R.drawable.head4;
                imgIdBody = R.drawable.body4;
                break;
        }
    }

    private void showMemory() {
        Log.d(TAG, "showMemory: ");
        try {
            currentContent = LitePal.where("story = ?", mStory + "").find(PlotCache.class);
            Log.d(TAG, "showMemory: " + currentContent.size());
            adapter = new Adapter(currentContent);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.scrollToPosition(currentContent.size() - 1);
        } catch (Exception e) {
            // 抛出异常就从后端加载聊天历史
            Executors.newCachedThreadPool().submit(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(URL_GET_PLOTS + "num=" + currentProgress).build();
                    try {
                        Response execute = client.newCall(request).execute();
                        if (execute.isSuccessful()) {
                            String response = execute.body().string();
                            final ArrayList<PlotCache> arrayList = gson.fromJson(response, ArrayList.class);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Adapter adapter = new Adapter(arrayList);
                                    mRecyclerView.setAdapter(adapter);
                                    mRecyclerView.scrollToPosition(adapter.getResources().size() - 1);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // 返回当前数据库中剧情的进度
    private int initData() {
        List<PlotCache> plotCaches = LitePal.where("story = ?", "" + mStory).find(PlotCache.class);
        List<PlotCache> users = LitePal.where("story = ? and whose = ?", "" + mStory, "2").find(PlotCache.class);
        Log.d(TAG, "initData: " + plotCaches.size() + "  " + users.size());
        if (users.size() > 0) {
            PlotCache plotCache1 = users.get(users.size() - 1);
            PlotCache plotCache2 = users.get(users.size() - 2);
            mChoice1.setTag(plotCache1);
            mChoice2.setTag(plotCache2);
        }
        return plotCaches.size() - 1;
    }

    private static final String TAG = "TalkActivity";


    private static class MyHandler extends Handler {
        private WeakReference<TalkActivity> contextWeakReference;

        public MyHandler(TalkActivity talkActivity) {
            contextWeakReference = new WeakReference<>(talkActivity);
        }

        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            if (contextWeakReference.get() == null) return;
            switch (msg.what) {
                case 0:
                    Toast.makeText(contextWeakReference.get(), "信息发送失败", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(contextWeakReference.get(), "获取信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    PlotCache message = (PlotCache) msg.obj;
                    contextWeakReference.get().adapter.addResource(message);
                    message.save();
                    break;
                case 11:
                    contextWeakReference.get().progressForward();
                    break;
                case 22:
                    Toast.makeText(contextWeakReference.get(), "您已完成本次剧情", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    private class Adapter extends MyRecyclerAdapter<PlotCache> {

        public Adapter(List<PlotCache> resources) {
            super(resources);
        }

        public void addResource(PlotCache data) {
            List<PlotCache> resources = getResources();
            resources.add(data);
            notifyItemInserted(resources.size() - 1);
            mRecyclerView.scrollToPosition(adapter.getResources().size() - 1);
        }

        @Override
        public int getLayoutId(int viewType) {
            return viewType == 1 ? R.layout.item_system_view_talk : R.layout.item_user_view_talk;
        }

        @Override
        public void bindView(MyHolder holder, int position, PlotCache s) {
            holder.setText(s.getContent(), R.id.content_wrapper);
            final ImageView imageView = holder.getView(R.id.img_head);
            if (s.getWhose() == 1) {
                imageView.setImageResource(imgIdHead);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TalkActivity.this, IntroduceActivity.class);
                        intent.putExtra("story", mStory);
                        intent.putExtra("imgIdHead", imgIdHead);
                        intent.putExtra("imgIdBody", imgIdBody);
                        startActivity(intent);
                    }
                });
            } else imageView.setImageResource(R.drawable.img_mine);

        }

        @Override
        public int getItemViewType(int position) {
            return super.getResources().get(position).getWhose();
        }


    }

}