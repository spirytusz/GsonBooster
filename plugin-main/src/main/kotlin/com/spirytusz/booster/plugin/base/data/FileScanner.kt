package com.spirytusz.booster.plugin.base.data

import com.spirytusz.booster.processor.base.scan.ClassScanner
import org.jetbrains.uast.UFile

/**
 * 代码文件扫描器，对指定的文件[sourceFile]，扫描这个文件下所有class，获取[classScanners]
 */
interface FileScanner {

    /**
     * 需要扫描的文件
     */
    val sourceFile: UFile

    /**
     * [sourceFile]下的所有类的类扫描器
     */
    val classScanners: Set<ClassScanner>
}