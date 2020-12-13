package com.example.huabei_competition.fcyUtil;

import android.app.Application;
import android.util.Log;

import com.example.huabei_competition.R;
import com.example.huabei_competition.db.Blink;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;


// fcy
public class MyApplication extends Application {
    // 在登陆成功后 更新次user
    private User user;
    private String mQuote;
    public static final String URL = "http://192.168.115.60:8080";
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    public OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                    cookieStore.put(httpUrl.host(), list);
                    Log.d(TAG, "saveFromResponse: " + "asdddddddddddddddddddddddd" );
                    Log.d(TAG, "saveFromResponse: " + httpUrl + " " + list.get(0));
                }
                @NotNull
                @Override
                public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                    Log.d(TAG, "loadForRequest: " + "asdddddddddddddddddddddaaaa");
                    List<Cookie> cookies = cookieStore.get(httpUrl.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();

    @Override
    public void onCreate() {
        super.onCreate();

        LitePal.initialize(this);
        loadOneQuote();
        LocalDateTime localDateTime = LocalDateTime.now();
        Log.d(TAG, "onCreate: "+localDateTime);
        List<Blink> all = LitePal.findAll(Blink.class);
        for (Blink blink : all) {
            Log.d(TAG, "onCreate: " + blink.toString());
        }
//        if (LitePal.findAll(Blink.class).size() == 0)
//            initBlink();
//        List<PlotCache> all = LitePal.findAll(PlotCache.class);
//        Log.d(TAG, "onCreate: " + all.size());
//        if (all.size() == 0) {
//            for (int i = 0; i < 5; i++) {
//                PlotCache c = new PlotCache();
//                c.setStory(0);
//                c.setContent("(*.*)" + i);
//                c.setPlot_id("" + i);
//                c.setWhose(i % 2);
//                c.save();
//            }
//        }
    }

//    private void initBlink() {
//        Log.d(TAG, "initBlink: ");
//        String[] stringArray = getResources().getStringArray(R.array.blinkWhose);
//        String[] stringArrayContent = getResources().getStringArray(R.array.blinkContent);
//        for (int i = 0; i < 10; i++) {
//            Blink blink = new Blink();
//            switch (i) {
//                case 0:
//                    blink.setId_img(R.drawable.blink_1);
//                    break;
//                case 2:
//                    blink.setId_img(R.drawable.blink_2);
//                    break;
//                case 3:
//                    blink.setId_img(R.drawable.blink_3);
//                    break;
//                case 6:
//                    blink.setId_img(R.drawable.blink_4);
//                    break;
//                case 7:
//                    blink.setId_img(R.drawable.blink_5);
//                    break;
//                case 9:
//                    blink.setId_img(R.drawable.blink_6);
//                    break;
//            }
//            blink.setStory(stringArray[i]);
//            blink.setContent(stringArrayContent[i]);
//            if (i <= 5)
//                blink.setTime("10/09");
//            else blink.setTime("10/10");
//            blink.setOrder(i);
//            blink.save();
//        }
//    }

    private static final String TAG = "MyApplication";

    private void loadOneQuote() {
        try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.quote)))) {
            Random random = new Random();
            int i1 = random.nextInt(50);
            for (int i = 0; i < 50; i++) {
                inputStream.readLine();
                if (i == i1) {
                    mQuote = inputStream.readLine();
                    break;
                }
            }
            Log.d(TAG, "loadOneQuote: " + mQuote + i1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "loadOneQuote: " + "加载失败");
        }
    }

    public User getUser() {
        return user;
    }

    // "data"{
//                "id":"1"
//                "name":"陈青云",
//                "password":"123456",
//                "status":"0",
//                "favor":"0",
//                "time":"0"
//    }
    public static class User {
        @SerializedName("name")
        private String userName;
        @SerializedName("password")
        private String passWord;
        // 学习时长 单位 分钟
        @SerializedName("time")
        private int studyTime;
        @SerializedName("favor")
        private int likingValue;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassWord() {
            return passWord;
        }

        public void setPassWord(String passWord) {
            this.passWord = passWord;
        }

        public int getStudyTime() {
            return studyTime;
        }

        public void setStudyTime(int studyTime) {
            this.studyTime = studyTime;
        }

        public int getLikingValue() {
            return likingValue;
        }

        public void setLikingValue(int likingValue) {
            this.likingValue = likingValue;
        }

        @NotNull
        @Override
        public String toString() {
            return "User{" +
                    "userName='" + userName + '\'' +
                    ", passWord='" + passWord + '\'' +
                    ", studyTime='" + studyTime + '\'' +
                    ", likingValue=" + likingValue +
                    '}';
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getmQuote() {
        return mQuote;
    }

    public void setmQuote(String mQuote) {
        this.mQuote = mQuote;
    }

    public static String getTAG() {
        return TAG;
    }
}
