package com.spirytusz.booster.bean

import com.google.gson.annotations.SerializedName
import com.spirytusz.booster.annotation.Boost

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