package com.spirytusz.booster.test.java;

import com.google.gson.annotations.SerializedName;
import com.spirytusz.booster.annotation.Boost;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Boost
public class CollectionValuesJava {

    @SerializedName("collection_list_long")
    public List<Long> listLong = new ArrayList<>();

    @SerializedName("collection_set_long")
    public Set<Long> setLong = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionValuesJava that = (CollectionValuesJava) o;
        return Objects.equals(listLong, that.listLong) && Objects.equals(setLong, that.setLong) && Objects.equals(listObject, that.listObject) && Objects.equals(setObject, that.setObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listLong, setLong, listObject, setObject);
    }

    @SerializedName("collection_list_object")
    public List<ObjectValueJava> listObject = new ArrayList<>();

    @SerializedName("collection_set_object")
    public Set<ObjectValueJava> setObject = new LinkedHashSet<>();
}
