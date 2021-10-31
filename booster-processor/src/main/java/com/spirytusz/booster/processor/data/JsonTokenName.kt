package com.spirytusz.booster.processor.data

enum class JsonTokenName(val token: String, val nextFunExp: String) {
    INT("NUMBER", "nextInt()"),
    LONG("NUMBER", "nextLong()"),
    FLOAT("NUMBER", "nextDouble().toFloat()"),
    DOUBLE("NUMBER", "nextDouble()"),
    STRING("STRING", "nextString()"),
    BOOLEAN("BOOLEAN", "nextBoolean()"),
    LIST("BEGIN_ARRAY", ""),
    SET("BEGIN_ARRAY", ""),
    OBJECT("BEGIN_OBJECT", "")
}