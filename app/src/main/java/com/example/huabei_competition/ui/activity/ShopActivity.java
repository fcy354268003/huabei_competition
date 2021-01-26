package com.example.huabei_competition.ui.activity;



import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;


import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.ActivityShopBinding;
import com.example.huabei_competition.databinding.CustomerDialogBinding;
import com.example.huabei_competition.databinding.ItemProductBinding;
import com.example.huabei_competition.db.Product;
import com.example.huabei_competition.util.BaseActivity;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.WidgetUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityShopBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_shop);
        binding.tabs.addTab(binding.tabs.newTab().setText("道具"));
        binding.tabs.addTab(binding.tabs.newTab().setText("角色"));
        binding.tabs.addTab(binding.tabs.newTab().setText("礼包"));
        binding.tabs.addOnTabSelectedListener(this);
        List<Product> list = new ArrayList<>();
        list.add(new Product().setName("罐头").setPrice(22).setDes("据说能哄好花木兰的罐头"));
        list.add(new Product().setName("粽子").setPrice(10).setDes("能够让屈原流泪的粽子"));
        list.add(new Product());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.content.setLayoutManager(gridLayoutManager);
        binding.content.setAdapter(new MyRecyclerAdapter<Product>(list) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_product;
            }

            @Override
            public void bindView(MyHolder holder, int position, Product o) {
                ItemProductBinding itemBinding = (ItemProductBinding) holder.getBinding();
                WidgetUtil.setCustomerText(itemBinding.tvPrice, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
                itemBinding.setData(o);
                itemBinding.ivPicture.setOnClickListener(view -> {
                    String name = list.get(position).getName();
                    if (name != null) {
                        CustomerDialog customerDialog = new CustomerDialog();
                        customerDialog.setLayoutId(R.layout.customer_dialog);
                        customerDialog.setCancelable(false);
                        // 判断现在 剩余的铜钱 是否足够购买

//                        ------------钱够
                        customerDialog.setCallback((root) -> {
                            CustomerDialogBinding binding1 = DataBindingUtil.bind(root);
                            if (binding1 == null) {
                                Log.d(TAG, "-----------------------------: ");
                                return;
                            }
                            binding1.tvTitle.setText("太白罐头");
                            binding1.tvContent.setText("确认花费**铜钱买入并使用吗？");
                            WidgetUtil.setCustomerText(binding1.tvContent, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
                            binding1.btnGot.setVisibility(View.GONE);
                        });
//                        -------------结束
//
////                        -------------钱不够
//                            customerDialog.setCallback(root -> {
//
//                            });
//
//
////                        ——--------------结束
                        customerDialog.show(getSupportFragmentManager(), "");

                    }
                });
            }

            @Override
            public void addResource(Product data) {

            }
        });
    }

    private static final String TAG = "ShopActivity";

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}