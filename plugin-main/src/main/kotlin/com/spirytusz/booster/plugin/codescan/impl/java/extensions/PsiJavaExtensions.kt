package com.spirytusz.booster.plugin.codescan.impl.java.extensions

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.spirytusz.booster.plugin.base.data.PsiTypeDescriptor
import com.spirytusz.booster.plugin.base.data.PsiTypeVarianceDescriptor
import com.spirytusz.booster.plugin.codescan.impl.java.JavaJsonTokenNameResolver
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.data.type.KtVariance
import org.jetbrains.kotlin.idea.search.getKotlinFqName
import java.util.regex.Pattern

private val sUserNotifier: Project.() -> Unit = {}

fun PsiField.typeOrThrow(userNotifier: (Project.() -> Unit)? = sUserNotifier): PsiTypeElement {
    return typeElement ?: run {
        userNotifier?.invoke(project)
        throw IllegalArgumentException("Can not resolve PsiTypeElement for PsiField: $this")
    }
}

fun PsiTypeElement.toPsiTypeDescriptor(): PsiTypeDescriptor {
    // ? extends java.util.List<java.lang.Number>
    val patternCovariance = Pattern.compile("\\?\\s?extends\\s?(.*)")

    // ? super java.util.List<java.lang.Number>
    val patternContravariance = Pattern.compile("\\?\\s?super\\s?(.*)")

    fun String.myTrim(): String {
        return this.replace(" ", "").replace("\n", "")
    }

    fun resolveRawType(): Pair<KtVariance, String> {
        // java.util.List<java.util.List<java.lang.Number>>
        // java.util.Map<java.lang.String, java.util.List<java.lang.Long>>
        // java.lang.Long
        val typeText = this.type.internalCanonicalText.myTrim()
        val indexOfOpenRBrace = typeText.indexOfFirst { it == '<' }
        val text = if (indexOfOpenRBrace > 0) {
            typeText.substring(0, indexOfOpenRBrace)
        } else {
            typeText
        }
        val covarianceMatcher = patternCovariance.matcher(text)
        if (covarianceMatcher.find()) {
            return KtVariance.OUT to covarianceMatcher.group(1)
        }

        val contravarianceMatcher = patternContravariance.matcher(text)
        if (contravarianceMatcher.find()) {
            return KtVariance.IN to contravarianceMatcher.group(1)
        }

        return KtVariance.INVARIANT to text
    }

    fun resolveNullability(): Boolean {
        return true
    }

    fun resolveTypeArguments(): List<PsiTypeDescriptor> {
        val containQuest = this.children.any { it is PsiJavaToken && it.text == "?" }
        val containVarianceKeyword = this.children.any { it is PsiKeyword && it.text in listOf("extends", "super") }
        val realJavaType = if (containQuest && containVarianceKeyword) {
            this.children.find { it is PsiTypeElement } as? PsiTypeElement
        } else {
            this
        }
        return realJavaType?.innermostComponentReferenceElement?.parameterList?.typeParameterElements?.map { genericTypeElement ->
            genericTypeElement.toPsiTypeDescriptor()
        } ?: listOf()
    }

    val rawTypeWithTypeVariance = resolveRawType()
    return PsiTypeDescriptor(
        rawType = rawTypeWithTypeVariance.second,
        jsonTokenName = JavaJsonTokenNameResolver(this).resolvedJsonTokenName,
        nullable = resolveNullability(),
        variance = rawTypeWithTypeVariance.first,
        generics = resolveTypeArguments(),
        target = this
    )
}

fun <T : PsiField> Iterable<T>.filterModifier(): List<T> {
    return asSequence().filterNot { field ->
        field.hasModifierStable("static")
    }.filter { field ->
        field.hasModifierStable("public")
    }.toList()
}

fun <T : PsiModifierListOwner> T.hasModifierStable(modifier: String): Boolean {
    return modifierList?.children?.any { it is PsiKeyword && it.text == modifier } == true
}

fun PsiClass.toPsiTypeDescriptor(): PsiTypeDescriptor {
    val generics = typeParameters.map {
        PsiTypeVarianceDescriptor(
            rawType = it.name.toString(),
            variance = KtVariance.INVARIANT,
            target = it
        )
    }
    return PsiTypeDescriptor(
        rawType = this.getKotlinFqName().toString(),
        nullable = false,
        variance = KtVariance.INVARIANT,
        jsonTokenName = JsonTokenName.OBJECT,
        generics = generics,
        target = this
    )
}