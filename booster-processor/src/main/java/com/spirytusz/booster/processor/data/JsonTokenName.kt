package com.spirytusz.booster.processor.data

enum class JsonTokenName(val token: String, val nextFunExp: String) {
    INT("NUMBER", "nextInt()"),
    LONG("NUMBER", "nextLong()"),
    FLOAT("NUMBER", "nextDouble().toFloat()"),
    DOUBLE("NUMBER", "nextDouble()"),
    STRING("STRING", "nextString()"),
    BOOLEAN("BOOLEAN", "nextBoolean()"),
    LIST("BEGIN_ARRAY", "beginArray()"),
    SET("BEGIN_ARRAY", "beginArray()"),
    OBJECT("BEGIN_OBJECT", "beginObject()"),
    ENUM("STRING", "nextString()")
}