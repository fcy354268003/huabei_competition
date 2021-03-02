package com.example.huabei_competition.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

public class Prop extends LitePalSupport {
    @SerializedName("id")
    private String PropId;
    private String name;
    private String picture;
    private String description;
    private String price;

    public String getPropId() {
        return PropId;
    }

    public void setPropId(String propId) {
        this.PropId = propId;
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
}
