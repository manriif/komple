plugins {
    `java-gradle-plugin`
    id("conventions-gradle")
    //alias(libs.plugins.gradle.plugin.publish)
}

dependencies {
    api(project(":${projectNamespace.replace('.', '-')}"))
}

gradlePlugin {
    website = projectWebsite
    vcsUrl = projectGitUrl

    plugins {
        create("komple-${localPluginId}") {
            id = "${projectGroup}.${localPluginId}"
            implementationClass = localPluginClass
            displayName = localName
            description = localDescription
            tags = localTags.split(',')
        }
    }
}