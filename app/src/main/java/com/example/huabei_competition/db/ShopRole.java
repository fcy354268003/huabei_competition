package com.example.huabei_competition.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

import java.util.Objects;

public class ShopRole extends LitePalSupport {
    @SerializedName("id")
    private String ShopRoleId;
    private String name;
    private String picture;
    private String description;
    private String price;
    private String isHaving;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getShopRoleId() {
        return ShopRoleId;
    }

    public void setShopRoleId(String shopRoleId) {
        this.ShopRoleId = shopRoleId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopRole shopRole = (ShopRole) o;
        return Objects.equals(ShopRoleId, shopRole.ShopRoleId) &&
                Objects.equals(userName, shopRole.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ShopRoleId, userName);
    }

    @Override
    public String toString() {
        return "ShopRole{" +
                "ShopRoleId='" + ShopRoleId + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", isHaving='" + isHaving + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
