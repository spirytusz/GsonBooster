package com.spirytusz.booster.processor.scan.kapt

import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner
import kotlinx.metadata.*
import java.util.regex.Pattern
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

class KaptClassScanner(
    private val processingEnvironment: ProcessingEnvironment,
    private val belongingClass: TypeElement,
    private val kmClassCacheHolder: KmClassCacheHolder,
    private val logger: MessageLogger
) : ClassScanner {

    companion object {
        private val PATTERN_DELEGATE_FIELD = Pattern.compile("\\\$delegate\$")
    }

    override val ktFields by lazy {
        scanPrimaryConstructor() + scanBody() + scanSupers()
    }

    private val kmClass: KmClass by lazy {
        kmClassCacheHolder.get(belongingClass)
    }

    private fun scanPrimaryConstructor(): List<KtField> {
        val primaryConstructor = kmClass.constructors.single {
            !Flag.Constructor.IS_SECONDARY(it.flags)
        }
        return primaryConstructor.valueParameters.asSequence().filter {
            val aptVariableElement = findAptVariableElement(it.name)
            preCheckDelegate(aptVariableElement)
            aptVariableElement != null && !aptVariableElement.modifiers.contains(Modifier.TRANSIENT)
        }.map {
            resolveKtField(it)
        }.toList()
    }

    private fun scanBody(): List<KtField> {
        val constructorFieldNames = kmClass.constructors.map { kmConstructor ->
            kmConstructor.valueParameters.map { it.name }
        }.flatten().distinct()
        return kmClass.properties.asSequence().filter {
            it.name !in constructorFieldNames
        }.filter {
            val aptVariableElement = findAptVariableElement(it.name)
            preCheckDelegate(aptVariableElement)
            aptVariableElement != null && !aptVariableElement.modifiers.contains(Modifier.TRANSIENT)
        }.map {
            resolveKtField(it)
        }.toList()
    }

    private fun scanSupers(): List<KtField> {
        return kmClass.supertypes.asSequence().mapNotNull {
            val classifier = it.classifier as? KmClassifier.Class
            if (classifier == null) {
                logger.error(
                    "Unexpected super type ${belongingClass.qualifiedName}",
                    belongingClass
                )
                throw IllegalStateException(
                    "Unexpected super type ${belongingClass.qualifiedName}"
                )
            }
            classifier.name.replace("/", ".")
        }.filter {
            it != Any::class.qualifiedName
        }.map { name ->
            val typeElement = processingEnvironment.elementUtils.getTypeElement(name)
            if (typeElement == null) {
                logger.error(
                    "Unexpected super type ${belongingClass.qualifiedName}",
                    belongingClass
                )
                throw IllegalStateException(
                    "Unexpected super type ${belongingClass.qualifiedName}"
                )
            } else {
                typeElement
            }
        }.map { superTypeElement ->
            KaptClassScanner(
                processingEnvironment,
                superTypeElement,
                kmClassCacheHolder,
                logger
            ).ktFields
        }.flatten().map {
            it.copy(declarationScope = DeclarationScope.SUPERS)
        }.toList()
    }

    private fun resolveKtField(kmProperty: KmProperty): KtField {
        return KmPropertyResolver(
            processingEnvironment,
            belongingClass,
            findAptVariableElement(kmProperty.name),
            kmProperty
        ).resolveKmProperty()
    }

    private fun resolveKtField(kmValueParameter: KmValueParameter): KtField {
        return KmValueParameterResolver(
            processingEnvironment,
            belongingClass,
            findAptVariableElement(kmValueParameter.name),
            kmValueParameter,
            logger
        ).resolveKmValueParameter()
    }

    private fun findAptVariableElement(fieldName: String): VariableElement? {
        return belongingClass.enclosedElements.asSequence().filter {
            it.kind == ElementKind.FIELD
        }.filterNot {
            it.modifiers.contains(Modifier.STATIC) || it.modifiers.contains(Modifier.TRANSIENT)
        }.find { it.simpleName.toString() == fieldName } as? VariableElement
    }

    private fun preCheckDelegate(aptVariableElement: VariableElement?) {

        fun isDelegateField(aptVariableElement: VariableElement): Boolean {
            val fieldName = aptVariableElement.simpleName.toString()
            return PATTERN_DELEGATE_FIELD.matcher(fieldName).find()
        }

        if (aptVariableElement != null && isDelegateField(aptVariableElement)) {
            logger.error("Unexpected delegate field", aptVariableElement)
            throw IllegalArgumentException(
                "Unexpected delegate field ${belongingClass.qualifiedName}.${aptVariableElement.simpleName}"
            )
        }
    }
}