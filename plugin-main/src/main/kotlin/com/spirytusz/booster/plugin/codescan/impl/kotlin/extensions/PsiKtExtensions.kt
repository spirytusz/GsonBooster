package com.spirytusz.booster.plugin.codescan.impl.kotlin.extensions

import com.intellij.openapi.project.Project
import com.spirytusz.booster.plugin.base.data.PsiTypeDescriptor
import com.spirytusz.booster.plugin.base.data.PsiTypeVarianceDescriptor
import com.spirytusz.booster.plugin.codescan.impl.kotlin.KtJsonTokenNameResolver
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.data.type.KtVariance
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.typeUtil.TypeNullability
import org.jetbrains.kotlin.types.typeUtil.nullability

private val sUserNotifier: Project.() -> Unit = {}

fun KtDeclaration.typeOrThrow(userNotifier: (Project.() -> Unit)? = sUserNotifier): KotlinType {
    return type() ?: run {
        userNotifier?.invoke(project)
        throw IllegalArgumentException("Can not resolve KotlinType for KtDeclaration: $this")
    }
}

fun <T : KtDeclaration> T.tryGetAnnotation(annotationName: String): KtAnnotationEntry? {
    return annotationEntries.find { annotationEntry ->
        val bindingContext = annotationEntry.analyze()
        val annotationDesc = bindingContext.get(BindingContext.ANNOTATION, annotationEntry)
        val fqName = annotationDesc?.fqName?.toString()
        fqName == annotationName
    }
}

fun KotlinType.toPsiTypeDescriptor(): PsiTypeDescriptor {
    val generics = arguments.map {
        val variance = when (it.projectionKind) {
            Variance.IN_VARIANCE -> KtVariance.IN
            Variance.OUT_VARIANCE -> KtVariance.OUT
            else -> KtVariance.INVARIANT
        }
        it.type.toPsiTypeDescriptor().copy(variance = variance)
    }

    return PsiTypeDescriptor(
        rawType = fqName.toString(),
        nullable = nullability() == TypeNullability.NULLABLE,
        jsonTokenName = KtJsonTokenNameResolver(this).resolvedJsonTokenName,
        variance = KtVariance.INVARIANT,
        generics = generics,
        target = null
    )
}

fun KtClassOrObject.toPsiTypeDescriptor(): PsiTypeDescriptor {
    val generics = typeParameters.map {
        val variance = when (it.variance) {
            Variance.IN_VARIANCE -> KtVariance.IN
            Variance.OUT_VARIANCE -> KtVariance.OUT
            else -> KtVariance.INVARIANT
        }
        PsiTypeVarianceDescriptor(
            rawType = it.name.toString(),
            variance = variance,
            target = it
        )
    }
    return PsiTypeDescriptor(
        rawType = fqName.toString(),
        nullable = false,
        variance = KtVariance.INVARIANT,
        jsonTokenName = JsonTokenName.OBJECT,
        generics = generics,
        target = this
    )
}