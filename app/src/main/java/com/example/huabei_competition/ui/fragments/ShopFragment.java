package com.example.huabei_competition.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.huabei_competition.R;
import com.example.huabei_competition.callback.ShopCallback;
import com.example.huabei_competition.databinding.CustomerDialogBinding;
import com.example.huabei_competition.databinding.FragmentShopBinding;
import com.example.huabei_competition.databinding.ItemProductBinding;
import com.example.huabei_competition.databinding.ItemShopRoleBinding;
import com.example.huabei_competition.db.Prop;
import com.example.huabei_competition.db.ShopRole;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.network.api.NPCRel;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.util.DatabaseUtil;
import com.example.huabei_competition.util.MyHandler;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.WidgetUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Create by FanChenYang at 2021/2/20
 */
public class ShopFragment extends Fragment implements TabLayout.OnTabSelectedListener, ShopCallback {


    private FragmentShopBinding binding;
    private MyHandler handler;
    private RequestManager glideManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        handler = MyHandler.obtain(context, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding != null) {
            ViewGroup parent = (ViewGroup) binding.getRoot().getParent();
            if (parent != null)
                parent.removeView(binding.getRoot());
            return binding.getRoot();
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shop, container, false);
        binding.setLifecycleOwner(this);
        binding.setCallBcak(this);
        binding.tabsShop.addTab(binding.tabsShop.newTab().setText("道具"));
        binding.tabsShop.addTab(binding.tabsShop.newTab().setText("角色"));
        binding.tabsShop.addOnTabSelectedListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.content.setLayoutManager(gridLayoutManager);
        observe();
        glideManager = Glide.with(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMyMoney();
    }

    private MyRecyclerAdapter<Prop> propAdapter;
    private MyRecyclerAdapter<ShopRole> shopRoleAdapter;
    private int prePosition = 0;

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        if (prePosition == position)
            return;
        switch (position) {
            case 0:
                binding.content.setAdapter(propAdapter);
                break;
            case 1:
                binding.content.setAdapter(shopRoleAdapter);
                break;
        }
        prePosition = position;
    }

    private void observe() {
        LiveDataManager.getInstance()
                .<Object>with(ShopFragment.class.getSimpleName() + "prop")
                .observe(getViewLifecycleOwner(), new Observer<Object>() {
                    @Override
                    public void onChanged(Object prop) {
                        //TODO 初始化
                        List<Prop> props = DatabaseUtil.getProps();
                        propAdapter = new MyRecyclerAdapter<Prop>(props) {
                            @Override
                            public int getLayoutId(int viewType) {
                                return R.layout.item_product;
                            }

                            @Override
                            public void bindView(MyHolder holder, int position, Prop prop) {
                                ItemProductBinding itemBinding = (ItemProductBinding) holder.getBinding();
                                WidgetUtil.setCustomerText(itemBinding.tvPrice, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
                                itemBinding.setData(prop);
                                glideManager.load(prop.getPicture()).into(itemBinding.ivPicture);
                                itemBinding.ivPicture.setOnClickListener(view -> {
                                    String name = getResources().get(position).getName();
                                    String id = getResources().get(position).getPropId();
                                    String price = getResources().get(position).getPrice();
                                    Log.d(TAG, "bindView: " + price);
                                    if (!TextUtils.isEmpty(id)) {
                                        areYouSure(name, id, price, 0);
                                    }
                                });
                            }


                        };
                        binding.content.setAdapter(propAdapter);
                    }
                });
        LiveDataManager.getInstance()
                .<Object>with(ShopFragment.class.getSimpleName() + "role").observe(getViewLifecycleOwner(), new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                List<ShopRole> roles = DatabaseUtil.getRoles();
                shopRoleAdapter = new MyRecyclerAdapter<ShopRole>(roles) {
                    @Override
                    public int getLayoutId(int viewType) {
                        return R.layout.item_shop_role;
                    }

                    @Override
                    public void bindView(MyHolder holder, int position, ShopRole shopRole) {
                        ItemShopRoleBinding binding = (ItemShopRoleBinding) holder.getBinding();
                        binding.setData(shopRole);
                        Log.d(TAG, "bindView: " + shopRole.getPrice());
                        glideManager.load(shopRole.getPicture()).override(140, 140).into(binding.ivPicture);
//                        Glide.with(binding.ivPicture).load(shopRole.getPicture()).into(binding.ivPicture);
                        if (TextUtils.equals(shopRole.getIsHaving(), "true")) {
                            binding.tvShopProp.setVisibility(View.VISIBLE);
                            binding.ivPicture.setAlpha(0.5f);
                        } else {
                            binding.tvShopProp.setVisibility(View.GONE);
                            binding.ivPicture.setAlpha(1f);
                            //给图片设置点击事件
                            binding.ivPicture.setOnClickListener(view -> {
                                areYouSure(shopRole.getName(), shopRole.getShopRoleId(), shopRole.getPrice(), 1);
                            });
                        }
                    }
                };
            }
        });
    }

    private void getData() {
        NPCRel.queryProduct(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Snackbar.make(binding.getRoot(), "网络出错", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    NPCRel.PropResponse propResponse = gson.fromJson(response.body().string(), NPCRel.PropResponse.class);
                    if (propResponse.getCode().equals(LogIn.OK)) {
                        ArrayList<Prop> info = propResponse.getData().getInfo();
                        for (Prop prop : info) {
                            DatabaseUtil.saveOrUpdateShopItem(prop);
                        }
                        LiveDataManager.getInstance()
                                .with(ShopFragment.class.getSimpleName() + "prop")
                                .postValue(new Object());
                    }
                } else {
                    onFailure(call, new IOException());
                }
                response.close();
            }
        });
        NPCRel.getRoleList(new Callback() {
                               @Override
                               public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                   Snackbar.make(binding.getRoot(), "网络出错", Snackbar.LENGTH_LONG).show();
                               }

                               @Override
                               public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                   if (response.isSuccessful()) {
                                       Gson gson = new Gson();
                                       String json = response.body().string();
                                       NPCRel.ShopRoleResponse shopRoleResponse = gson.fromJson(json, NPCRel.ShopRoleResponse.class);
                                       Log.d(TAG, "onResponse: " + json);
                                       if (shopRoleResponse.getCode().equals(LogIn.OK)) {
                                           ArrayList<ShopRole> info = shopRoleResponse.getData().getInfo();
                                           for (ShopRole role : info) {
                                               role.setUserName(UserUtil.sUserName);
                                               DatabaseUtil.saveOrUpdateShopRoleItem(role);
                                           }
                                           LiveDataManager.getInstance()
                                                   .with(ShopFragment.class.getSimpleName() + "role")
                                                   .postValue(new Object());
                                       }
                                   } else {
                                       onFailure(call, new IOException());
                                   }
                                   response.close();
                               }
                           }
        );
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onBackClick() {
        ((MainActivity) getActivity()).getController().navigateUp();
    }

    @Override
    public void onProductClick() {

    }

    private void areYouSure(String name, String id, String price, int type) {
        CustomerDialog customerDialog = new CustomerDialog();
        customerDialog.setLayoutId(R.layout.customer_dialog);
        customerDialog.setCancelable(false);
        customerDialog.setCallback((root) -> {
            CustomerDialogBinding binding1 = DataBindingUtil.bind(root);
            if (binding1 == null) {
                return;
            }
            binding1.tvTitle.setText(name);
            String con = "确认花费price铜钱买入并使用吗？";
            String pri = con.replace("price", price);
            binding1.tvContent.setText(pri);
            WidgetUtil.setCustomerText(binding1.tvContent, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
            binding1.btnGot.setVisibility(View.GONE);
            binding1.btnCancel.setOnClickListener(v -> {
                customerDialog.dismiss();
            });
            binding1.btnConfirm.setOnClickListener(confirm -> {
                NPCRel.buyProp(id, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                        Snackbar.make(confirm, "购买出现异常", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Gson g = new Gson();
                            NPCRel.Buy_1 buy_1 = g.fromJson(response.body().string(), NPCRel.Buy_1.class);
                            if (buy_1.getCode().equals(LogIn.OK)) {
                                Snackbar.make(confirm, "购买成功", Snackbar.LENGTH_SHORT).show();
                                if (type == 1) {
                                    NPCRel.getNPCList();
                                    List<ShopRole> shopRoles = LitePal.where("ShopRoleId = ? and userName = ?", id, UserUtil.sUserName).find(ShopRole.class);
                                    if (shopRoles != null && shopRoles.size() > 0) {
                                        shopRoles.get(0).setIsHaving("true");
                                        DatabaseUtil.saveOrUpdateShopRoleItem(shopRoles.get(0));
                                    }
                                    LiveDataManager.getInstance()
                                            .with(ShopFragment.class.getSimpleName() + "role")
                                            .postValue(new Object());
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String money = buy_1.getData().getMoney();
                                        binding.tvMyMoney.setText(money);
                                    }
                                });
                            } else
                                Snackbar.make(confirm, buy_1.getMessage(), Snackbar.LENGTH_SHORT).show();
                        } else {
                            onFailure(call, new IOException());
                        }
                        response.close();
                    }
                }, type);
            });
        });
        customerDialog.show(getActivity().getSupportFragmentManager(), "");
    }

    private static final String TAG = "ShopFragment";

    private void getMyMoney() {
        NPCRel.queryMoney(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                Log.d(TAG, "onResponse: " + json);
                Gson gson = new Gson();
                NPCRel.MoneyGet_1 moneyGet_1 = gson.fromJson(json, NPCRel.MoneyGet_1.class);
                if (moneyGet_1.getCode().equals(LogIn.OK)) {
                    Log.d(TAG, "onResponse: +++++" + json);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String money = moneyGet_1.getData().getMoney();
                            binding.tvMyMoney.setText(money);
                        }
                    });
                }
                response.close();
            }
        });
    }
}