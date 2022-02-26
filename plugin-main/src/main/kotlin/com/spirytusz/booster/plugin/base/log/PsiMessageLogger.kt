package com.spirytusz.booster.plugin.base.log

import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.spirytusz.booster.plugin.base.data.IPsiElementOwner
import com.spirytusz.booster.processor.base.log.MessageLogger
import org.jetbrains.kotlin.idea.search.getKotlinFqName

class PsiMessageLogger : MessageLogger {

    companion object {
        private const val TAG = "GsonBooster-Plugin"
    }

    private val logger by lazy {
        Logger.getInstance(this::class.java)
    }

    override fun debug(message: String, target: Any?) {
        logger.debug("[$TAG] $message${target.tryFindPsiElement().getBriefInfo()}")
    }

    override fun info(message: String, target: Any?) {
        logger.info("[$TAG] $message${target.tryFindPsiElement().getBriefInfo()}")
    }

    override fun warn(message: String, target: Any?) {
        logger.warn("[$TAG] $message${target.tryFindPsiElement().getBriefInfo()}")
    }

    override fun error(message: String, target: Any?) {
        logger.error("[$TAG] $message${target.tryFindPsiElement().getBriefInfo()}")
    }

    private fun Any?.tryFindPsiElement(): PsiElement? {
        return when (this) {
            is PsiElement -> this
            is IPsiElementOwner -> this.target
            else -> null
        }
    }

    private fun PsiElement?.getBriefInfo(): String {
        return if (this != null) {
            " ${getKotlinFqName()}"
        } else {
            ""
        }
    }
}