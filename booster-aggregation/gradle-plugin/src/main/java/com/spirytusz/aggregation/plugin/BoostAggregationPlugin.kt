package com.spirytusz.aggregation.plugin

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.devtools.ksp.gradle.KspExtension
import com.spirytusz.aggregation.plugin.data.BoostAggregationExtension
import com.spirytusz.aggregation.plugin.sourceset.KspGeneratedCollector
import com.spirytusz.aggregation.plugin.task.GsonBoosterBundleClassGenerateTask
import com.spirytusz.booster.contract.Constants.BoosterKeys.KEY_IS_MAIN_PROJECT
import com.spirytusz.booster.contract.Constants.BoosterKeys.KEY_PROJECT_NAME
import com.spirytusz.booster.contract.Constants.PluginIds.ANDROID_PLUGIN
import com.spirytusz.booster.contract.Constants.PluginIds.KSP_PLUGIN
import org.gradle.api.Plugin
import org.gradle.api.Project

class BoostAggregationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("boostAggregation", BoostAggregationExtension::class.java)

        configureKsp(project)
        configureDependency(project)
        registerTaskIfNeed(project)
    }

    private fun configureKsp(project: Project) {
        project.extensions.configure(KspExtension::class.java) {
            it.arg(KEY_PROJECT_NAME, project.name)
            it.arg(KEY_IS_MAIN_PROJECT, project.plugins.hasPlugin(ANDROID_PLUGIN).toString())
        }
        includeKspGeneratedIfNeed(project)
    }

    private fun includeKspGeneratedIfNeed(project: Project) {
        project.afterEvaluate {
            val boostAggregation =
                project.extensions.getByType(BoostAggregationExtension::class.java)
            if (boostAggregation.includeKspGenerated.get()) {
                KspGeneratedCollector().includeKspGenerated(project)
            }
        }
    }

    private fun configureDependency(project: Project) {
        project.pluginManager.withPlugin(KSP_PLUGIN) {
            project.dependencies.add("ksp", "com.spirytusz:booster-aggregation-processor:1.4.0")
        }
    }

    private fun registerTaskIfNeed(project: Project) {
        val necessaryPlugins = listOf(ANDROID_PLUGIN, KSP_PLUGIN)
        if (necessaryPlugins.any { !project.plugins.hasPlugin(it) }) {
            return
        }

        fun registerTaskVariantAware(project: Project, variantName: String) {
            val variant = variantName.replaceFirstChar { it.uppercaseChar() }
            val variantTaskName = "generate${variant}GsonBoosterBundleClass"
            val bundleTask = project.tasks.register(
                variantTaskName,
                GsonBoosterBundleClassGenerateTask::class.java
            ) {
                val inputFile = project.layout.buildDirectory.file(
                    "generated/ksp/$variantName/resources/boost-aggregated.json"
                )
                val outputFile = project.layout.buildDirectory.dir(
                    "tmp/kotlin-classes/${variantName}/"
                )
                it.aggregatedJsonFile.set(inputFile)
                it.bundleKotlinSourceDirectory.set(outputFile)
            }
            val kspTask = project.tasks.named("ksp${variant}Kotlin")
            val dexBuilderTask = project.tasks.named("dexBuilder${variant}")

            bundleTask.dependsOn(kspTask)
            dexBuilderTask.dependsOn(bundleTask)
        }

        project.afterEvaluate {
            project.extensions.getByType(BaseAppModuleExtension::class.java)
                .applicationVariants
                .forEach {
                    registerTaskVariantAware(project, it.name)
                }
        }
    }
}