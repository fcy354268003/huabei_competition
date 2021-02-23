package com.example.huabei_competition.db;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * Create by FanChenYang
 * <p>
 * 将用户与故事情节对象的聊天记录存储
 * </p>
 */
public class PlotCache extends LitePalSupport {
    @Column(unique = true)
    private String plot_id;
    private String content;
    // 1代表系统 2代表用户
    private int whose;
    private int story;

    public int getStory() {
        return story;
    }

    public void setStory(int story) {
        this.story = story;
    }

    public String getPlot_id() {
        return plot_id;
    }

    public void setPlot_id(String plot_id) {
        this.plot_id = plot_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getWhose() {
        return whose;
    }

    public void setWhose(int whose) {
        this.whose = whose;
    }
}
