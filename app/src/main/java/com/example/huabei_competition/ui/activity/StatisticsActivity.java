package com.example.huabei_competition.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.db.StatisticsData;
import com.example.huabei_competition.db.StatisticsLike;
import com.example.huabei_competition.util.BaseActivity;
import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.ui.fragments.QuestionFragment;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StatisticsActivity extends BaseActivity {
    private StatisticsLike mStatisticsLike;
    private StatisticsData mStatisticsData;
    private ListView mListView;
    private TextView Nametv, Liketv, Yeartv;
    private String URL_7day = MyApplication.URL + "/app/getTimes/sevenDay";
    private String URL_GET_TIMES = MyApplication.URL + "/app/getTimes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

    }

    public void StartMainActivity(View view) {
        finish();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        getInfo();
        findViewById(R.id.img_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.question).setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().add(R.id.question,new QuestionFragment()).commit();
            }
        });
    }

    private static final String TAG = "StatisticsActivity";
    public void getInfo() {
        OkHttpClient client = ((MyApplication) getApplication()).okHttpClient;
        Request request = new Request.Builder().url(URL_GET_TIMES).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showMessage("获取信息失败");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    Gson gson = new Gson();
                    final Data data = gson.fromJson(string, Data.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView count = findViewById(R.id.cishu_tv);
                            TextView total = findViewById(R.id.total_tv);
                            TextView week_total = findViewById(R.id.week_tv);
                            count.setText(String.valueOf(data.items.size()));
                            total.setText(String.valueOf(((MyApplication) getApplication()).getUser().getStudyTime()));
                            int weekly = 0;
                            for (Data.Item item : data.items) {
                                weekly += item.timeLen;
                            }
                            week_total.setText(String.valueOf(weekly));
                            RecyclerView recyclerView = findViewById(R.id.statistics_lv);
                            recyclerView.setLayoutManager(new LinearLayoutManager(StatisticsActivity.this));
                            Log.d(TAG, "run: " + data.items.size());
                            recyclerView.setAdapter(new MyRecyclerAdapter<Data.Item>(data.items) {
                                @Override
                                public int getLayoutId(int viewType) {
                                    return R.layout.item_statistics_layout;
                                }
                                @Override
                                public void bindView(MyHolder holder, int position, Data.Item item) {
                                    String year = item.time.substring(0, 4);
                                    String mounth = item.time.substring(5, 7);
                                    String day = item.time.substring(8, 10);
                                    holder.setText(year, R.id.record_data1_lv);
                                    holder.setText(mounth, R.id.record_data2_lv);
                                    holder.setText(day, R.id.record_data3_lv);
                                    holder.setText(String.valueOf(item.timeLen), R.id.tv_len);
                                }

                                @Override
                                public void addResource(Data.Item data) {

                                }


                            });

                        }
                    });
                }
            }
        });
    }

    private static class Data {
        @SerializedName("data")
        private List<Item> items = new ArrayList<>();

        private static class Item {
            @SerializedName("timeLen")
            int timeLen;
            @SerializedName("createTime")
            String time;
        }
    }
}