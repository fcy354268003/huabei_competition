package com.example.huabei_competition.ui.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huabei_competition.R;
import com.example.huabei_competition.callback.ShopCallback;
import com.example.huabei_competition.databinding.CustomerDialogBinding;
import com.example.huabei_competition.databinding.FragmentShopBinding;
import com.example.huabei_competition.databinding.ItemProductBinding;
import com.example.huabei_competition.db.Product;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.WidgetUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by FanChenYang at 2021/2/20
 */
public class ShopFragment extends Fragment implements TabLayout.OnTabSelectedListener, ShopCallback {


    private FragmentShopBinding binding;

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
//        binding.tabsShop.addTab(binding.tabsShop.newTab().setText("礼包"));
        binding.tabsShop.addOnTabSelectedListener(this);
        List<Product> list = new ArrayList<>();
        list.add(new Product().setName("罐头").setPrice(22).setDes("据说能哄好花木兰的罐头"));
        list.add(new Product().setName("粽子").setPrice(10).setDes("能够让屈原流泪的粽子"));
        list.add(new Product());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
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
                            binding1.btnCancel.setOnClickListener(v -> {
                                customerDialog.dismiss();
                            });

                        });
                        customerDialog.show(getActivity().getSupportFragmentManager(), "");

                    }
                });
            }


        });
        return binding.getRoot();
    }

    private static final String TAG = "ShopFragment";

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

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
        //
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: fragment暂停了");
    }
}