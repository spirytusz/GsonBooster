package com.spirytusz.aggregation.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.spirytusz.aggregation.processor.data.AggregatedResult
import com.spirytusz.aggregation.processor.data.merge
import com.spirytusz.aggregation.processor.data.mergeAggregatedList
import com.spirytusz.aggregation.processor.global.gson
import com.spirytusz.booster.annotation.BoostAggregated
import com.spirytusz.booster.annotation.BoostProcessed
import com.spirytusz.booster.contract.Constants.BoosterKeys.KEY_IS_MAIN_PROJECT
import com.spirytusz.booster.contract.Constants.BoosterKeys.KEY_PROJECT_NAME
import com.spirytusz.booster.contract.Constants.Naming.GSON
import com.spirytusz.booster.contract.Constants.PackageNames.AGGREGATED_PACKAGE_NAME
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo

class BoostProcessedCollector(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    companion object {
        private const val QUALIFIED_NAME_TYPE_ADAPTER = "com.google.gson.TypeAdapter"

        private const val QUALIFIED_NAME_TYPE_ADAPTER_FACTORY = "com.google.gson.TypeAdapterFactory"

        private const val QUALIFIED_NAME_GSON = "com.google.gson.Gson"
    }

    private val Resolver.boostProcessedAnnotatedClasses: Sequence<KSClassDeclaration>
        get() = this.getSymbolsWithAnnotation(BoostProcessed::class.java.canonicalName)
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                it.classKind == ClassKind.CLASS
            }.filter {
                it.getVisibility() == Visibility.PUBLIC
            }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val typeAdapterNames = mutableMapOf<String, String>()
        val typeAdapterFactoryNames = mutableSetOf<String>()

        val annotatedClasses = resolver.boostProcessedAnnotatedClasses
        annotatedClasses.forEach { ksDeclaration ->
            when {
                // TypeAdapter
                ksDeclaration.isGeneratedTypeAdapter() -> {
                    environment.logger.warn("find TypeAdapter", ksDeclaration)
                    val superType = ksDeclaration.superTypes.first().resolve()
                    val superTypeGenericType = superType.arguments.first().type?.resolve()
                    val superTypeGenericName = superTypeGenericType?.declaration?.qualifiedName?.asString().toString()
                    val typeAdapterName = ksDeclaration.qualifiedName?.asString().toString()
                    typeAdapterNames[superTypeGenericName] = typeAdapterName
                }
                // TypeAdapterFactory
                ksDeclaration.isGeneratedTypeAdapterFactory() -> {
                    environment.logger.warn("find TypeAdapterFactory", ksDeclaration)
                    typeAdapterFactoryNames.add(ksDeclaration.qualifiedName?.asString().toString())
                }
            }
        }

        if (typeAdapterNames.isEmpty() && typeAdapterFactoryNames.isEmpty()) {
            return emptyList()
        }
        val aggregated = AggregatedResult(
            typeAdapterNames = typeAdapterNames,
            typeAdapterFactoryNames = typeAdapterFactoryNames
        )
        writeAggregated(
            annotatedClasses.mapNotNull { it.containingFile }.toList(),
            aggregated
        )
        return emptyList()
    }

    private fun KSDeclaration.isGeneratedTypeAdapter(): Boolean {
        if (this !is KSClassDeclaration) {
            return false
        }
        val primaryConstructor = this.primaryConstructor ?: return false
        val hasGeneratedGsonValueParameter = primaryConstructor.parameters.none {
            val valueParameterName = it.name?.asString()
            if (valueParameterName != GSON) {
                return@none false
            }
            val valueParameterTypeDeclaration = it.type.resolve().declaration
            if (valueParameterTypeDeclaration !is KSClassDeclaration) {
                return@none false
            }
            return valueParameterTypeDeclaration.qualifiedName?.asString() == QUALIFIED_NAME_GSON
        }
        if (!hasGeneratedGsonValueParameter) {
            return false
        }

        return superTypes.any {
            val superTypeDeclaration = it.resolve().declaration
            superTypeDeclaration.qualifiedName?.asString() == QUALIFIED_NAME_TYPE_ADAPTER
        }
    }

    private fun KSDeclaration.isGeneratedTypeAdapterFactory(): Boolean {
        if (this !is KSClassDeclaration) {
            return false
        }

        return superTypes.any {
            val superTypeDeclaration = it.resolve().declaration
            superTypeDeclaration.qualifiedName?.asString() == QUALIFIED_NAME_TYPE_ADAPTER_FACTORY
        }
    }

    private fun writeAggregated(
        originatingKSFiles: Iterable<KSFile>,
        aggregatedResult: AggregatedResult
    ) {
        val projectName = environment.options[KEY_PROJECT_NAME]
        FileSpec.builder(
            AGGREGATED_PACKAGE_NAME,
            "boost-aggregated-$projectName"
        ).addProperty(
            PropertySpec.builder(
                "aggregated_${projectName?.replace("-", "_")}",
                String::class.asClassName().copy(nullable = true),
                KModifier.PRIVATE
            ).addAnnotation(
                AnnotationSpec.builder(BoostAggregated::class)
                    .addMember("aggregatedResult = %P", gson.toJson(aggregatedResult))
                    .build()
            ).initializer("null").build()
        ).build().writeTo(
            environment.codeGenerator,
            aggregating = false,
            originatingKSFiles = originatingKSFiles
        )
    }
}

class BoostAggregationProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    private var round = 0

    private val processedResultCollector = BoostProcessedCollector(environment)

    private var aggregated: AggregatedResult = AggregatedResult()
    private val originatingKSFiles = mutableSetOf<KSFile>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        environment.logger.warn("round=${++round}")
        processedResultCollector.process(resolver)
        collectAggregationOnMainProject(resolver)
        return emptyList()
    }

    override fun finish() {
        if (!isMainProject()) {
            return
        }
        writeAggregatedIfNeed()
    }

    private fun isMainProject(): Boolean {
        val isMainProject = environment.options[KEY_IS_MAIN_PROJECT]
        return isMainProject == true.toString()
    }

    @OptIn(KspExperimental::class)
    private fun collectAggregationOnMainProject(resolver: Resolver) {
        if (!isMainProject()) {
            return
        }

        val classWithAggregatedJson = resolver.getDeclarationsFromPackage(AGGREGATED_PACKAGE_NAME)
            .filterIsInstance<KSPropertyDeclaration>()
            .mapNotNull {
                val aggregateJson = it.tryGetAggregatedJson()
                if (aggregateJson != null) {
                    it to aggregateJson
                } else {
                    null
                }
            }

        originatingKSFiles += classWithAggregatedJson.mapNotNull { (clazz, _) ->
            clazz.containingFile
        }.distinct()

        aggregated = classWithAggregatedJson.map { (_, json) ->
            gson.fromJson(json, AggregatedResult::class.java)
        }.toList().mergeAggregatedList().merge(aggregated)
    }

    private fun KSDeclaration.tryGetAggregatedJson(): String? {
        val targetAnnotation = annotations.find { ksAnnotation ->
            ksAnnotation.shortName.asString() == BoostAggregated::class.java.simpleName
        } ?: run {
            environment.logger.warn("tryGetAggregatedJson() not found annotation ${simpleName.asString()}")
            return null
        }

        val targetValueParameter = targetAnnotation.arguments.find { ksValueArgument ->
            ksValueArgument.name?.asString() == "aggregatedResult"
        } ?: run {
            environment.logger.warn("tryGetAggregatedJson() not found aggregatedResult ${simpleName.asString()}")
            return null
        }

        return targetValueParameter.value?.toString()
    }

    private fun writeAggregatedIfNeed() {
        if (aggregated.isEmpty()) {
            return
        }

        environment.codeGenerator.createNewFile(
            dependencies = Dependencies(
                aggregating = false,
                sources = originatingKSFiles.toTypedArray()
            ),
            packageName = "",
            fileName = "boost-aggregated",
            extensionName = "json"
        ).bufferedWriter().use {
            it.write(gson.toJson(aggregated))
        }
    }
}