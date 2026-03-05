plugins {
    `version-catalog`
    alias(libs.plugins.conventions.common)
}

catalog {
    versionCatalog {
        val komple = "komple"
        val group = projectGroup

        version(komple, libs.versions.komple.get())
        plugin(komple, group).versionRef(komple)

        rootProject.subprojects.forEach { project ->
            if (project.path.startsWith(":tools:")) {
                plugin(project.toolName, project.toolPluginId).versionRef(komple)
            }
        }
    }
}