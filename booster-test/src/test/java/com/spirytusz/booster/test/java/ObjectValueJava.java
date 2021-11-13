package com.spirytusz.booster.test.java;

import com.google.gson.annotations.SerializedName;
import com.spirytusz.booster.annotation.Boost;

import java.util.Objects;

@Boost
public class ObjectValueJava {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectValueJava that = (ObjectValueJava) o;
        return Objects.equals(someMember, that.someMember);
    }

    @Override
    public int hashCode() {
        return Objects.hash(someMember);
    }

    @SerializedName("object_some_member")
    public String someMember = "";
}
