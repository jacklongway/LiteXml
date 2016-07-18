package com.longway.sample;

import java.util.HashSet;

/**
 * Created by longway
 */

public class City {
    public String name;
    public String code;
    public HashSet<Region> regions;

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", regions=" + regions +
                '}';
    }
}
