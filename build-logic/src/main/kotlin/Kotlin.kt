import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation

/**
 * Common Kotlin configuration for all projects.
 */
fun KotlinJvmProjectExtension.configureKotlin() {
    explicitApi()

    compilerOptions {
        freeCompilerArgs.run {
            add("-Xreturn-value-checker=full")
            add("-Xexplicit-backing-fields")
            add("-Xcontext-parameters")
        }
    }

    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(project.libs.versions.jvm.toolchain.get())
    }

    sourceSets.named(KotlinCompilation.MAIN_COMPILATION_NAME) {
        languageSettings.optIn("komple.KompleInternalApi")
    }
}