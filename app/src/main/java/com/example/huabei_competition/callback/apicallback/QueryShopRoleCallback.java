package com.example.huabei_competition.callback.apicallback;


import com.example.huabei_competition.db.ShopRole;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.network.api.NPCRel;
import com.example.huabei_competition.ui.shop.ShopFragment;
import com.example.huabei_competition.util.DatabaseUtil;
import com.example.huabei_competition.widget.MyToast;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class QueryShopRoleCallback implements Callback {

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        MyToast.showMessage("网络出错");
        // todo 从本地获取数据 填充界面
        // 图片使用默认的本地图片
        showData(DatabaseUtil.getMineShopRole());
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        if (response.isSuccessful()) {
            Gson gson = new Gson();
            String json = response.body().string();
            NPCRel.ShopRoleResponse shopRoleResponse = gson.fromJson(json, NPCRel.ShopRoleResponse.class);
            if (shopRoleResponse.getCode().equals(LogIn.OK)) {
                ArrayList<ShopRole> info = shopRoleResponse.getData().getInfo();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (ShopRole shopRole : info) {
                            shopRole.setUserName(UserUtil.sUserName);
                            shopRole.saveOrUpdate("ShopRoleId = ? and userName = ?", shopRole.getShopRoleId(), UserUtil.sUserName);
                        }
                    }
                }).start();
                LiveDataManager.getInstance()
                        .with(ShopFragment.class.getSimpleName() + "role")
                        .postValue(info);
            }
        } else {
            onFailure(call, new IOException());
        }
        response.close();
    }

    private void showData(List<ShopRole> prop) {
        LiveDataManager.getInstance()
                .with(ShopFragment.class.getSimpleName() + "prop")
                .postValue(prop);
    }
}
