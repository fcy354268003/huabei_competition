package com.example.huabei_competition.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.ActivityStroyChoiceBinding;
import com.example.huabei_competition.db.Blink;
import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class StoryChoiceActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    public Intent intent = new Intent();
    public int type;
    private NavController controller;
    private VelocityTracker tracker;
    private ActivityStroyChoiceBinding dataBinding;
    private MyRecyclerAdapter<Blink> adapter;
    private Handler mHandler = new Handler();
    private int currentFragment = 1;
    private int monthValue;
    private int dayOfMonth;
    private int year;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalDateTime now = LocalDateTime.now();
        monthValue = now.getMonthValue();
        dayOfMonth = now.getDayOfMonth();
        year = now.getYear();
        int type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            intent.putExtra("type", 0);
        } else {
            intent.putExtra("type", 1);
        }
        initAdapter();
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_stroy_choice);
        dataBinding.setMContext(this);
        dataBinding.setLifecycleOwner(this);
        controller = Navigation.findNavController(findViewById(R.id.fragment2));
        dataBinding.bnvChoice.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.friends:
                        if (currentFragment == 2) {
                            currentFragment = 1;
                            controller.navigate(R.id.action_commentFragment_to_friendsFragment);

                        }
                        break;
                    case R.id.circle:
                        if (currentFragment == 1) {
                            currentFragment = 2;
                            controller.navigate(R.id.action_friendsFragment_to_commentFragment);
                        }
                        break;
                }
                return true;
            }
        });

    }

    private static final String TAG = "StoryChoiceActivity";


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private final String URL_GET_COMMENTS = MyApplication.URL + "/app/download";

    private void updateInfo() {

//        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(URL_GET_COMMENTS + "?year=" + year + "&month=" + monthValue + "&day=" + dayOfMonth).build();
        ((MyApplication) getApplication()).okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    Gson gson = new Gson();
                    Info info = gson.fromJson(string, Info.class);
                    Log.d(TAG, "onResponse: " + info.comments.size());
                    for (Blink comment : info.comments) {
                        Log.d(TAG, "onResponse: " + comment.toString());
                        String time = comment.getTime();
                        String substring = time.substring(0, 10);
                        comment.setEasyTime(substring);
                        try {
                            comment.saveThrows();
                        } catch (Exception e) {
                            e.printStackTrace();
                            comment.updateAll("order = ?", comment.getOrder() + "");
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initAdapter();
                        }
                    });
                }
            }
        });
    }

    private static class Info {
        @SerializedName("data")
        public List<Blink> comments = new ArrayList<>();

    }

    private void initAdapter() {
        LocalDateTime localDateTime = LocalDateTime.now();
//        int monthValue = localDateTime.getMonthValue();
//        int dayOfMonth = localDateTime.getDayOfMonth();
//        String v = null;
//        if (dayOfMonth < 10) {
//            v = monthValue + "/0" + dayOfMonth;
//        } else v = monthValue + "/" + dayOfMonth;
        final List<Blink> blinks = LitePal.where("time <= ?", localDateTime.toString()).find(Blink.class);
        if (blinks.size() == 0 || (!blinks.get(0).getTime().substring(0, 10).equals(localDateTime.toString().substring(0, 10)))) {
            updateInfo();
        }
        Collections.reverse(blinks);
        adapter = new MyRecyclerAdapter<Blink>(blinks) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_circle_friend;
            }

            @Override
            public void bindView(MyHolder holder, int position, Blink blink) {
                // 设置发布时间
                holder.setText(blink.getEasyTime(), R.id.tv_sendTime);
                // 设置用户头像
                ImageView imageView1 = holder.getView(R.id.iv_thumb);
                switch (blink.getStory()) {
                    case "李白":
                        imageView1.setImageResource(R.drawable.head1);
                        break;
                    case "杜甫":
                        imageView1.setImageResource(R.drawable.head2);
                        break;
                    case "苏轼":
                        imageView1.setImageResource(R.drawable.head3);
                        break;
                    case "花木兰":
                        imageView1.setImageResource(R.drawable.head4);
                        break;
                }
                // 设置昵称
                holder.setText(blink.getStory(), R.id.petName);
                // 设置动态内容
                holder.setText(blink.getContent(), R.id.content);
                // 设置图片
                if (blink.getId_img() != null) {
                    Log.d(TAG, "bindView: " + MyApplication.URL + "/image/get/" + blink.getId_img());
                    ImageView imageView = holder.getView(R.id.iv_annex);
                    imageView.setVisibility(View.VISIBLE);
//                    imageView.setImageResource(blink.getId_img());
                    Glide.with(StoryChoiceActivity.this).load(MyApplication.URL + "/image/get/" + blink.getId_img()).into(imageView);
                }
            }

            @Override
            public void addResource(Blink data) {

            }
        };
    }

    public MyRecyclerAdapter<Blink> getAdapter() {
        return adapter;
    }

}


