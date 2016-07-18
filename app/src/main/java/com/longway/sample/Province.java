package com.longway.sample;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by longway
 */

public class Province {
    public static final String TAG = Province.class.getSimpleName();
    public String name;
    public ArrayList<City> cities;
    public String code;
    public HashMap<String, String> map;
    public Introduce introduce;

    @Override
    public String toString() {
        return "Province{" +
                "name='" + name + '\'' +
                ", cities=" + cities +
                ", code='" + code + '\'' +
                ", map=" + map +
                ", introduce=" + introduce +
                '}';
    }
}
