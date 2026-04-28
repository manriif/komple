plugins {
    `version-catalog`
    alias(libs.plugins.conventions.common)
}

private val SplitRegex = Regex("""[^a-zA-Z0-9]""")

private fun String.toPluginAlias(): String {
    val prefix: String

    val words = split(SplitRegex)
        .also { prefix = it[0] }
        .drop(1)

    val alias = if (words.size == 1) words.first() else {
        words
            .mapIndexed { index, word ->
                if (index == 0) {
                    word.lowercase()
                } else {
                    word.replaceFirstChar(Char::titlecase)
                }
            }
            .joinToString("")
    }

    return "$prefix-$alias"
}

catalog {
    versionCatalog {
        val komple = "komple"
        val group = projectGroup

        version(komple, libs.versions.komple.get())
        plugin(komple, group).versionRef(komple)
        library(komple, group, komple).versionRef(komple)

        rootProject.subprojects.forEach { project ->
            if (project.path.startsWith(":komple-tools:")) {
                plugin(project.toolName.toPluginAlias(), project.toolPluginId)
                    .versionRef(komple)
            }
        }
    }
}