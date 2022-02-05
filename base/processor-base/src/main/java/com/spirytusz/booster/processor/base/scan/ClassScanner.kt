package com.spirytusz.booster.processor.base.scan

import com.spirytusz.booster.processor.base.data.KtField

/**
 * 类扫描器
 */
interface ClassScanner {

    /**
     * 变量
     */
    val ktFields: List<KtField>
}