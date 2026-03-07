package komple.tool.jext

/**
 * SHA-256 checksums for Jextract integrity check.
 */
public class JextractChecksums(
    public val linuxAarch64: String,
    public val linuxX64: String,
    public val macosAarch64: String,
    public val macosX64: String,
    public val windowsX64: String,
)