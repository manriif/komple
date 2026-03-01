import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.`java-library`
import org.gradle.kotlin.dsl.kotlin

plugins {
    `java-library`
    org.jetbrains.kotlin.jvm
    id("conventions-common")
}

dependencies {
    implementation(gradleApi())
}

kotlin {
    explicitApi()

    compilerOptions {
        freeCompilerArgs.run {
            add("-Xreturn-value-checker=full")
            add("-Xexplicit-backing-fields")
            add("-Xcontext-parameters")
        }
    }

    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.jvm.toolchain.get())
    }

    sourceSets.all {
        languageSettings.optIn("komple.KompleInternalApi")
    }
}