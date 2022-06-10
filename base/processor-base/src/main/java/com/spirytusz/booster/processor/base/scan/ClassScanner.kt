package com.spirytusz.booster.processor.base.scan

import com.spirytusz.booster.processor.base.data.BoosterClassKind
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.data.type.KtType

/**
 * 类扫描器
 */
interface ClassScanner {

    /**
     * 类别，class、object、interface、interface、annotation或者是enum
     */
    val classKind: BoosterClassKind

    /**
     * 类型
     */
    val classKtType: KtType

    /**
     * 变量
     */
    val ktFields: List<KtField>
}