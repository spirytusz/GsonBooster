package com.spirytusz.gsonbooster.data;

import com.spirytusz.booster.annotation.Boost;

import java.util.ArrayList;
import java.util.List;


@Boost
public class BeansJava {

    public transient List<? super String> javaA = new ArrayList<>();

    public BeansJava(String b) {
    }
}
