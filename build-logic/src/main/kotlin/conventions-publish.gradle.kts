//import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `maven-publish`
    com.vanniktech.maven.publish
}

mavenPublishing {
    //publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
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
        inceptionYear = "2026"

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