package com.example.huabei_competition.ui.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.huabei_competition.R;
import com.example.huabei_competition.db.PersonDetail;
import com.example.huabei_competition.util.BaseActivity;

import java.util.List;

public class IntroduceActivity extends BaseActivity {
    private static final String TAG = "IntroduceActivity";
    List<PersonDetail> detailList;
    private String Id;
    private SQLiteDatabase db;
    private PersonDetail personDetail;
    private ImageView mHead, imageView2;
    private TextView mName, tvZihao, tvDynasty, tvSummary;
    private int myStory, imgIdHead,imgIdBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myStory = getIntent().getIntExtra("story", 0);
        imgIdHead = getIntent().getIntExtra("imgIdHead", 0);
        imgIdBody = getIntent().getIntExtra("imgIdBody",0);
        setContentView(R.layout.activity_introduce);
        Log.d(TAG, "onCreate: " + myStory );
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        String name = getResources().getStringArray(R.array.storyName)[myStory];
        String petName = getResources().getStringArray(R.array.petName)[myStory];
        String dynasty = getResources().getStringArray(R.array.dynasty)[myStory];
        String briefIntroduction = getResources().getStringArray(R.array.briefIntroduction)[myStory];
        View viewById1 = findViewById(R.id.detail1);
        mHead = viewById1.findViewById(R.id.bighead);
        mHead.setImageResource(imgIdHead);
        mName = viewById1.findViewById(R.id.textview_name);
        mName.setText(name);
        tvZihao = viewById1.findViewById(R.id.textview_zihao);
        tvZihao.setText(petName);
        tvDynasty = viewById1.findViewById(R.id.textview_dynasty);
        tvDynasty.setText(dynasty);
        View viewById2 = findViewById(R.id.detail2);
        tvSummary = viewById2.findViewById(R.id.basic_all);
        tvSummary.setText(briefIntroduction);
        ImageView i = findViewById(R.id.detail3).findViewById(R.id.iv);
        i.setImageResource(imgIdBody);
    }
    public void back(View v){
        finish();
    }
}