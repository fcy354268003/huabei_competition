package com.example.huabei_competition.db;

import org.litepal.crud.LitePalSupport;

public class ShopRole extends LitePalSupport {
    private String id;
    private String name;
    private String picture;
    private String description;
    private String price;
    private String isHaving;
    private String userName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIsHaving() {
        return isHaving;
    }

    public void setIsHaving(String isHaving) {
        this.isHaving = isHaving;
    }
}
