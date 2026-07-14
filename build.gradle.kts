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
    alias(libs.plugins.dokka) apply true
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.gradle.pluginPublish) apply false
}

allprojects {
    group = property("project.group").toString()
    version = rootProject.libs.versions.komple.get()
}

dependencies {
    dokka(projects.komple)
    dokka(projects.kompleGradlePlugin)
    dokka(projects.kompleTools.kompleToolAndroidNdk)
    dokka(projects.kompleTools.kompleToolAppleXcode)
    dokka(projects.kompleTools.kompleToolCmake)
    dokka(projects.kompleTools.kompleToolEmscripten)
    dokka(projects.kompleTools.kompleToolGnuSed)
    dokka(projects.kompleTools.kompleToolJextract)
    dokka(projects.kompleTools.kompleToolWabt)
    dokka(projects.kompleTools.kompleToolZig)
}

dokka {
    dokkaPublications.html {
        moduleName = providers.gradleProperty("project.name")
        moduleVersion = project.version.toString()
        outputDirectory = rootDir.resolve("docs/api")
        failOnWarning = true
        suppressInheritedMembers = false
        suppressObviousFunctions = true
        offlineMode = false
    }

    pluginsConfiguration.html {
        footerMessage = "© ${property("project.inceptionYear")} ${property("project.dev.name")}"
    }
}