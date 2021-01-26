package com.example.huabei_competition.db;

import com.example.huabei_competition.widget.DiffDataCallback;


import org.litepal.crud.LitePalSupport;

public class Product extends LitePalSupport implements DiffDataCallback.Differ<Product> {
    private int price;
    private String name;
    private String des;
    private String iconUrl;

    public Product(int price, String name, String des, String iconUrl) {
        this.price = price;
        this.name = name;
        this.des = des;
        this.iconUrl = iconUrl;
    }

    public Product() {
    }

    public int getPrice() {
        return price;
    }

    public Product setPrice(int price) {
        this.price = price;
        return this;
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public String getDes() {
        return des;
    }

    public Product setDes(String des) {
        this.des = des;
        return this;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public Product setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    @Override
    public boolean isSame(Product o) {
        return this.name.equals(o.name);
    }

    @Override
    public boolean isContentSame(Product o) {
        return this.price == o.price && this.des.equals(o.des) && this.iconUrl.equals(o.iconUrl);
    }
}
