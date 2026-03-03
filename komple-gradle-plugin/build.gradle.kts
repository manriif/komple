plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.conventions.common)
    //alias(libs.plugins.gradle.plugin.publish)
}

dependencies {
    api(projects.komple)
}

kotlin {
    explicitApi()

    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }

    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.jvm.toolchain.get())
    }

    sourceSets.main {
        languageSettings.optIn("komple.KompleInternalApi")
    }
}

gradlePlugin {
    website = projectWebsite
    vcsUrl = projectGitUrl

    plugins {
        create("komple") {
            id = projectGroup
            implementationClass = "komple.gradle.KomplePlugin"
            displayName = "Komple"
            description = localDescription
            tags = localTags.split(',')
        }

        create("komple-root") {
            id = "$projectGroup-root"
            implementationClass = "komple.gradle.KompleRootPlugin"
            displayName = "Komple Root"
            description = localDescription
            tags = localTags.split(',')
        }

        create("komple-settings") {
            id = "$projectGroup-settings"
            displayName = "Komple Settings"
            description = localDescription
            implementationClass = "komple.gradle.KompleSettingsPlugin"
            tags = localTags.split(',')
        }
    }
}

tasks {
    val generateVersionFile = register("generateVersionFile") {
        val kompleVersion = libs.versions.komple.get()
        val versionFile = layout.buildDirectory.file("generated/resources/version.txt")

        doLast {
            versionFile.get().asFile.let { file ->
                check(file.parentFile.mkdirs())
                file.writeText(kompleVersion)
            }
        }
    }

    // Include generated version file in resources
    processResources {
        from(generateVersionFile) {
            into(".")
        }
    }
}