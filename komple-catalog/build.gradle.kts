/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
        library("kompleGradlePlugin", group, "komple-gradle-plugin").versionRef(komple)

        rootProject.subprojects.forEach { project ->
            if (project.path.startsWith(":komple-tools:")) {
                plugin(project.toolName.toPluginAlias(), project.toolPluginId)
                    .versionRef(komple)
            }
        }
    }
}