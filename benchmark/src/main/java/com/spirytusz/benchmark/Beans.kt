package com.spirytusz.benchmark

import com.google.gson.annotations.SerializedName
import com.spirytusz.booster.annotation.Boost

@Boost
data class FooTest(
    @SerializedName("foo_test_long")
    val longValue: Long = 0L,
    @SerializedName("foo_test_nullable_long")
    val nullableLong: Long? = 0L,
    @SerializedName("foo_test_list_bar_test")
    val listBarTest: List<BarTest> = listOf(),
    @SerializedName("foo_test_nullable_list_bar_test")
    val nullListBar: List<BarTest>? = listOf(),
    @SerializedName("foo_test_list_gusha")
    val listGuSha: List<GuSha> = listOf(),
    @SerializedName("foo_test_primitive_bean")
    val primitiveBean: PrimitiveBean = PrimitiveBean(),
    @SerializedName("foo_test_list_primitive_bean")
    val listPrimitiveBean: List<PrimitiveBean> = listOf(),
    @SerializedName("foo_test_complex_struct_bean")
    val complexStructureBean: ComplexStructureBean = ComplexStructureBean()
)

@Boost
data class BarTest(
    @SerializedName("bar_test_long")
    val longValue: Long = 0L,
    @SerializedName("bar_test_class_test")
    val classTest: ClassTest = ClassTest()
) {

    @SerializedName("bar_test_var_outer_constructor_list_long")
    var outerConstructorListLong = listOf<Long>()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BarTest

        if (longValue != other.longValue) return false
        if (outerConstructorListLong != other.outerConstructorListLong) return false

        return true
    }

    override fun hashCode(): Int {
        var result = longValue.hashCode()
        result = 31 * result + outerConstructorListLong.hashCode()
        return result
    }
}

@Boost
class ClassTest(
    @SerializedName("class_test_constructor_long")
    val constructorLong: Long = 0L
) {

    @SerializedName("class_test_int")
    var intValue: Int = 0

    @SerializedName("class_test_long")
    var longValue: Long = 0L

    @SerializedName("class_test_float")
    var floatValue: Float = 0f

    @SerializedName("class_test_double")
    var doubleValue: Double = 0.0

    @SerializedName("class_test_string")
    var stringValue: String = ""

    @SerializedName("class_test_boolean")
    var booleanValue: Boolean = false
}

@Boost
data class GuSha(
    @SerializedName("gusha_int")
    val intValue: Int = 0,
    @SerializedName("gusha_long")
    val longValue: Long = 0L,
    @SerializedName("gusha_float")
    val floatValue: Float = 0f,
    @SerializedName("gusha_double")
    val doubleValue: Double = 0.0,
    @SerializedName("gusha_string")
    val stringValue: String = "",
    @SerializedName("gusha_boolean")
    val booleanValue: Boolean = false,
    @SerializedName("gusha_gu")
    val gu: Gu = Gu(),
    @SerializedName("gusha_sha")
    val sha: Sha = Sha()
)

@Boost
data class Gu(
    @SerializedName("gu_int")
    val intValue: Int = 0,
    @SerializedName("gu_long")
    val longValue: Long = 0L,
    @SerializedName("gu_float")
    val floatValue: Float = 0f,
    @SerializedName("gu_double")
    val doubleValue: Double = 0.0,
    @SerializedName("gu_string")
    val stringValue: String = "",
    @SerializedName("gu_boolean")
    val booleanValue: Boolean = false,
    @SerializedName("gu_pata")
    val pata: PaTa = PaTa()
)

@Boost
data class Sha(
    @SerializedName("sha_int")
    val intValue: Int = 0,
    @SerializedName("sha_long")
    val longValue: Long = 0L,
    @SerializedName("sha_float")
    val floatValue: Float = 0f,
    @SerializedName("sha_double")
    val doubleValue: Double = 0.0,
    @SerializedName("sha_string")
    val stringValue: String = "",
    @SerializedName("sha_boolean")
    val booleanValue: Boolean = false,
    @SerializedName("sha_pata")
    val pata: PaTa = PaTa()
)

@Boost
data class PaTa(
    @SerializedName("pata_int")
    val intValue: Int = 0,
    @SerializedName("pata_long")
    val longValue: Long = 0L,
    @SerializedName("pata_float")
    val floatValue: Float = 0f,
    @SerializedName("pata_double")
    val doubleValue: Double = 0.0,
    @SerializedName("pata_string")
    val stringValue: String = "",
    @SerializedName("pata_boolean")
    val booleanValue: Boolean = false
)

@Boost
data class StringBean(
    @SerializedName("string_bean_string")
    val stringValue: String = ""
)

@Boost
data class IntBean(
    @SerializedName("int_bean_int")
    val intValue: Int = 0
)

@Boost
data class LongBean(
    @SerializedName("long_bean_long")
    val longValue: Long = 0L
)

@Boost
data class FloatBean(
    @SerializedName("float_bean_float")
    val floatValue: Float = 0f
)

@Boost
data class DoubleBean(
    @SerializedName("double_bean_double")
    val doubleValue: Double = 0.0
)

@Boost
data class BooleanBean(
    @SerializedName("boolean_bean_boolean")
    val booleanValue: Boolean = false
)

@Boost
data class PrimitiveBean(
    @SerializedName("primitive_bean_int_bean")
    val intBean: IntBean = IntBean(),
    @SerializedName("primitive_bean_long_bean")
    val longBean: LongBean = LongBean(),
    @SerializedName("primitive_bean_float_bean")
    val floatBean: FloatBean = FloatBean(),
    @SerializedName("primitive_bean_double_bean")
    val doubleBean: DoubleBean = DoubleBean(),
    @SerializedName("primitive_bean_string_bean")
    val stringBean: StringBean = StringBean(),
    @SerializedName("primitive_bean_boolean_bean")
    val booleanBean: BooleanBean = BooleanBean()
)

@Boost
data class ComplexStructureBean(
    @SerializedName("complex_struct_complex_collection_bean")
    val complexCollectionBean: ComplexCollectionBean = ComplexCollectionBean(),
    @SerializedName("complex_struct_complex_map_bean")
    val complexMapBean: ComplexMapBean = ComplexMapBean(),
    @SerializedName("complex_struct_complex_fusion_bean")
    val complexFusionBean: ComplexFusionBean = ComplexFusionBean()
)

@Boost
data class ComplexCollectionBean(
    @SerializedName("complex_collection_bean_list_set_list_long")
    val listSetListLong: List<Set<List<Long?>?>?> = listOf()
)

@Boost
data class ComplexMapBean(
    @SerializedName("complex_map_bean_map_str_map_str_map_str_str")
    val mapStringMapStringMapStringString: Map<String, Map<String, Map<String, String?>?>?> = mapOf()
)

@Boost
data class ComplexFusionBean(
    @SerializedName("complex_fusion_bean_map_str_list_map_str_set_str")
    val mapStringListMapStringSetString: Map<String, List<Map<String, Set<String?>?>?>?> = mapOf()
)