package com.spirytusz.aggregation.plugin.sourceset

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.spirytusz.booster.contract.Constants.PluginIds.ANDROID_LIB_PLUGIN
import com.spirytusz.booster.contract.Constants.PluginIds.ANDROID_PLUGIN
import com.spirytusz.booster.contract.Constants.PluginIds.KOTLIN_JVM
import com.spirytusz.booster.contract.Constants.PluginIds.KSP_PLUGIN
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer

abstract class AbstractKspGeneratedCollector {

    abstract fun includeKspGenerated(project: Project)
}

class KspGeneratedCollector : AbstractKspGeneratedCollector() {
    override fun includeKspGenerated(project: Project) {
        project.run {
            when {
                plugins.hasPlugin(ANDROID_PLUGIN) -> {
                    AndroidProjectKspGeneratedCollector().includeKspGenerated(this)
                }
                plugins.hasPlugin(ANDROID_LIB_PLUGIN) -> {
                    AndroidLibProjectKspGeneratedCollector().includeKspGenerated(this)
                }
                else -> {
                    KotlinJvmProjectKspGeneratedCollector().includeKspGenerated(this)
                }
            }
        }
    }
}

class AndroidProjectKspGeneratedCollector : AbstractKspGeneratedCollector() {
    override fun includeKspGenerated(project: Project) {
        project.run {
            pluginManager.withPlugin(KSP_PLUGIN) {
                extensions.getByType(BaseAppModuleExtension::class.java).sourceSets.configureEach {
                    it.kotlin.srcDir("$buildDir/generated/ksp/${it.name}/kotlin")
                }
            }
        }
    }
}

class AndroidLibProjectKspGeneratedCollector : AbstractKspGeneratedCollector() {
    override fun includeKspGenerated(project: Project) {
        project.run {
            pluginManager.withPlugin(ANDROID_LIB_PLUGIN) {
                extensions.getByType(LibraryExtension::class.java).sourceSets.configureEach {
                    it.kotlin.srcDir("$buildDir/generated/ksp/${it.name}/kotlin")
                }
            }
        }
    }
}

class KotlinJvmProjectKspGeneratedCollector : AbstractKspGeneratedCollector() {
    override fun includeKspGenerated(project: Project) {
        project.run {
            pluginManager.withPlugin(KOTLIN_JVM) {
                extensions.getByType(SourceSetContainer::class.java).configureEach {
                    it.java.srcDir("$buildDir/generated/ksp/${it.name}/kotlin")
                }
            }
        }
    }
}