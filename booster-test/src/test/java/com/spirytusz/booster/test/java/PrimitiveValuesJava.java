package com.spirytusz.booster.test.java;

import com.google.gson.annotations.SerializedName;
import com.spirytusz.booster.annotation.Boost;

import java.util.Objects;

@Boost
public class PrimitiveValuesJava {

    @SerializedName("primitive_str")
    public String stringValue = "";

    @SerializedName("primitive_int")
    public int intValue = 0;

    @SerializedName("primitive_long")
    public long longValue = 0L;

    @SerializedName("primitive_float")
    public float floatValue = 0f;

    @SerializedName("primitive_double")
    public double doubleValue = 0.0;

    @SerializedName("primitive_bool")
    public boolean booleanValue = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimitiveValuesJava that = (PrimitiveValuesJava) o;
        return intValue == that.intValue && longValue == that.longValue && Float.compare(that.floatValue, floatValue) == 0 && Double.compare(that.doubleValue, doubleValue) == 0 && booleanValue == that.booleanValue && Objects.equals(stringValue, that.stringValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringValue, intValue, longValue, floatValue, doubleValue, booleanValue);
    }
}
