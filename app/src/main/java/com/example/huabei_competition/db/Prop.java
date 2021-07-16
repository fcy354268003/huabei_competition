package com.example.huabei_competition.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prop prop = (Prop) o;
        return PropId.equals(prop.PropId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(PropId);
    }

    @Override
    public String toString() {
        return "Prop{" +
                "PropId='" + PropId + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
