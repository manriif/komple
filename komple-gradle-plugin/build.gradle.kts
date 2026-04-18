plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.conventions.common)
    //alias(libs.plugins.gradle.plugin.publish)
}

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
    api(projects.komple)
}

kotlin {
    configureKotlin()
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
    }
}

tasks {
    // Include tools.properties at the root project root
    processResources {
        from(rootProject.layout.projectDirectory.file("tools.properties")) {
            into(".")
        }
    }
}