package com.spirytusz.booster.processor.base

import com.spirytusz.booster.annotation.Boost
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

abstract class BaseProcessor : AbstractProcessor() {

    companion object {
        private const val CONSOLE_TAG = "[GsonBooster]"
    }

    protected val RoundEnvironment.boostAnnotatedClasses: Sequence<TypeElement>
        get() {
            return getElementsAnnotatedWith(Boost::class.java)
                .asSequence()
                .filter { it.kind == ElementKind.CLASS }
                .filterNot { processingEnv.elementUtils.getPackageOf(it).isUnnamed }
                .map {
                    it as TypeElement
                }
        }

    protected fun log(msg: String, kind: Diagnostic.Kind = Diagnostic.Kind.NOTE) {
        processingEnv.messager.printMessage(kind, "\n$CONSOLE_TAG ${javaClass.name}: $msg\n")
    }

    override fun process(annotations: MutableSet<out TypeElement>, env: RoundEnvironment): Boolean {
        if (annotations.isNotEmpty()) {
            process(env)
        }
        return false
    }

    abstract fun process(env: RoundEnvironment)
}