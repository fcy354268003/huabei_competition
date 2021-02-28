package com.example.huabei_competition.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.huabei_competition.R;
import com.example.huabei_competition.db.NPC;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.util.BaseActivity;

public class IntroduceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        findViewById(R.id.iv_back).setOnClickListener(view -> {
            finish();
        });
        LiveDataManager.getInstance().<NPC>with(TalkActivity.class.getSimpleName()).observe(this, new Observer<NPC>() {
            @Override
            public void onChanged(NPC npc) {
                View v1 = findViewById(R.id.detail1);
                ImageView head = v1.findViewById(R.id.bighead);
                Glide.with(head).load(npc.getPortrait()).into(head);
                TextView name = v1.findViewById(R.id.textview_name);
                name.setText(npc.getName());
                ((TextView) v1.findViewById(R.id.textview_zihao)).setText(npc.getTradeName());
                ((TextView) v1.findViewById(R.id.textview_dynasty)).setText(npc.getDynasty());
                ((TextView) findViewById(R.id.detail2).findViewById(R.id.basic_all)).setText(npc.getDescription());
                ImageView img = findViewById(R.id.detail3).findViewById(R.id.iv);
                Glide.with(img).load(npc.getPortrait()).into(img);
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}