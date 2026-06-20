package komple.tool.zig

import com.falsepattern.minisign.lib.PublicKey
import com.falsepattern.minisign.lib.Signature
import komple.KOMPLE_GROUP
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Task responsible for downloading Zig according to
 * [mirrors guidelines](https://ziglang.org/download/community-mirrors/).
 */
@CacheableTask
internal abstract class ZigDownloadTask : DefaultTask() {

    @get:Internal
    abstract val outputDirectory: DirectoryProperty

    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val publicKey: Property<String>

    @get:Input
    abstract val archiveFileName: Property<String>

    /**
     * Downloads the community mirror list.
     */
    private fun HttpClient.downloadCommunityMirrorList(): List<String> {
        val request = HttpRequest
            .newBuilder(URI.create("https://ziglang.org/download/community-mirrors.txt"))
            .build()

        return send(request, HttpResponse.BodyHandlers.ofLines())
            .body()
            .toList()
    }

    /**
     * Downloads a file, from [mirror], into [file].
     */
    private fun HttpClient.downloadFile(
        mirror: String,
        file: File
    ) {
        val fileUrl = "$mirror/${file.name}?source=${KOMPLE_GROUP}"

        val fileRequest = HttpRequest
            .newBuilder(URI.create(fileUrl))
            .build()

        val fileResponse = send(fileRequest, HttpResponse.BodyHandlers.ofFile(file.toPath()))
        val statusCode = fileResponse.statusCode()

        check(statusCode == 200) {
            "Downloading of $fileUrl failed with status code $statusCode"
        }
    }

    /**
     * Returns `true` if downloaded [tarballFile] is safe to use, `false` otherwise.
     */
    private fun checkDownloadedTarball(
        tarballFile: File,
        signatureFile: File
    ): Boolean {
        if (!tarballFile.exists() || !tarballFile.isFile) {
            return false
        }

        try {
            val signature = Signature.fromFile(signatureFile.toPath())
            val zigKey = PublicKey.decodeFromBase64(publicKey.get())

            zigKey.verifyFile(tarballFile.toPath(), signature, null)

            val trustedComment = signature.trustedComment
            val expectedCommentContent = "\tfile:${tarballFile.name}\t"

            check(trustedComment?.contains(expectedCommentContent) == true) {
                "Invalid file in trusted comment: $trustedComment"
            }
        } catch (error: Throwable) {
            tarballFile.delete()
            signatureFile.delete()
            logger.warn("Failed to verify downloaded zig tarball", error)
            return false
        }

        return true
    }

    @TaskAction
    fun download() {
        val outputDirectory = outputDirectory.get()
        val tarballFileName = archiveFileName.get()
        val signatureFileName = "$tarballFileName.minisig"
        val tarballFile = outputDirectory.file(tarballFileName).asFile
        val signatureFile = outputDirectory.file(signatureFileName).asFile

        if (checkDownloadedTarball(tarballFile, signatureFile)) {
            didWork = true
            return logger.lifecycle("Reusing cached zig download")
        }

        val client = HttpClient.newHttpClient()

        logger.lifecycle("Downloading zig mirror list")

        val communityMirrors = try {
            client.downloadCommunityMirrorList()
        } catch (cause: Throwable) {
            // No network, zig down, etc
            logger.warn("Failed to download zig community mirror list", cause)
            emptyList()
        }

        val downloadFiles = listOf(tarballFile, signatureFile)
        val mirrors = communityMirrors.shuffled() + "https://ziglang.org/download/${version.get()}"

        for (mirror in mirrors) {
            logger.lifecycle("Downloading zig from ($mirror)")

            try {
                downloadFiles.forEach { file ->
                    client.downloadFile(mirror, file)
                }

                if (checkDownloadedTarball(tarballFile, signatureFile)) {
                    didWork = true
                    return logger.lifecycle("Successfully downloaded zig from ($mirror)")
                }
            } catch (cause: Throwable) {
                logger.warn("Failed to download zig from ($mirror)", cause)
            }
        }

        logger.error("Failed to download zig from all mirrors and from the official download url")
        didWork = false
    }
}