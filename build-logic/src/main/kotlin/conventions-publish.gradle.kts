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
    `maven-publish`
    com.vanniktech.maven.publish
}

mavenPublishing {
    //publishToMavenCentral()
    //signAllPublications()

    coordinates(
        groupId = projectGroup,
        artifactId = project.name,
        version = libs.versions.komple.get()
    )

    pom {
        name = localName
        description = localDescription
        url = projectWebsite
        inceptionYear = projectInceptionYear

        licenses {
            license {
                name = projectLicenseName
                url = projectLicenseUrl
            }
        }

        developers {
            developer {
                id = projectDevId
                name = projectDevName
                url = projectDevUrl
            }
        }

        scm {
            url = projectGitUrl
            connection = "scm:git:git://${projectGitBase}.git"
            developerConnection = "scm:git:ssh://git@${projectGitBase}.git"
        }
    }
}