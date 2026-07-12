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
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import kotlin.properties.ReadOnlyProperty

internal val Project.libs: LibrariesForLibs
    inline get() = the()

///////////////////////////////////////////////////////////////////////////
// Properties
///////////////////////////////////////////////////////////////////////////

private const val ROOT_PROJECT_PROPERTY_PREFIX = "project"
private const val LOCAL_PROJECT_PROPERTY_PREFIX = "local"

private fun Project.getProperty(prefix: String, name: String): String {
    val propertyName = "$prefix.$name"

    if (!hasProperty(propertyName)) {
        error("property $propertyName not found in project `${path}`")
    }

    return property(propertyName).toString()
}

///////////////////////////////////////////////////////////////////////////
// Root project
///////////////////////////////////////////////////////////////////////////

private fun rootProjectProperty(name: String): ReadOnlyProperty<Project, String> {
    return ReadOnlyProperty { thisRef, _ ->
        thisRef.rootProject.getProperty(ROOT_PROJECT_PROPERTY_PREFIX, name)
    }
}

val Project.projectNamespace by rootProjectProperty("namespace")
val Project.projectGroup by rootProjectProperty("group")
val Project.projectWebsite by rootProjectProperty("website")
val Project.projectInceptionYear by rootProjectProperty("inceptionYear")
val Project.projectLicenseName by rootProjectProperty("license.name")
val Project.projectLicenseUrl by rootProjectProperty("license.url")
val Project.projectGitBase by rootProjectProperty("git.base")
val Project.projectGitUrl by rootProjectProperty("git.url")

val Project.projectDevId by rootProjectProperty("dev.id")
val Project.projectDevName by rootProjectProperty("dev.name")
val Project.projectDevUrl by rootProjectProperty("dev.url")

///////////////////////////////////////////////////////////////////////////
// Local project
///////////////////////////////////////////////////////////////////////////

private fun localProjectProperty(name: String): ReadOnlyProperty<Project, String> {
    return ReadOnlyProperty { thisRef, _ ->
        thisRef.getProperty(LOCAL_PROJECT_PROPERTY_PREFIX, name)
    }
}

val Project.localName: String by localProjectProperty("name")
val Project.localDescription: String by localProjectProperty("description")
val Project.localTags: String by localProjectProperty("tags")
val Project.localPluginClass: String by localProjectProperty("pluginClass")

///////////////////////////////////////////////////////////////////////////
// Tools
///////////////////////////////////////////////////////////////////////////

val Project.toolName: String
    get() = name.removePrefix("$projectNamespace-")

val Project.toolPluginId: String
    get() = "$projectGroup-$toolName"