import org.gradle.api.tasks.SourceSet
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/**
 * Common Kotlin configuration for all projects.
 */
fun KotlinJvmProjectExtension.configureKotlin() {
    explicitApi()

    compilerOptions {
        freeCompilerArgs.run {
            add("-Xreturn-value-checker=full")
            add("-Xcontext-parameters")
            add("-Xcontext-sensitive-resolution")
        }
    }

    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(project.libs.versions.jvm.toolchain.get())
    }

    sourceSets.named(SourceSet.MAIN_SOURCE_SET_NAME).configure {
        languageSettings.optIn("komple.KompleInternalApi")
    }
}