package com.example.huabei_competition.db;

import java.util.ArrayList;
import java.util.List;

public class City {
    List<Cn> list = new ArrayList<>();

    public static class Cn {
        String name;

        public String getName() {
            return name;
        }
    }

    public List<Cn> getList() {
        return list;
    }
}
