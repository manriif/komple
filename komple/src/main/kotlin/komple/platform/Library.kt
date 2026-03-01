package komple.platform

/**
 * Library generation information.
 */
public enum class Library(
    public val sharedPrefix: String,
    public val sharedSuffix: String,
    public val staticPrefix: String,
    public val staticSuffix: String
) {
    Darwin("lib", "dylib", "lib", "a"),
    Linux("lib", "so", "lib", "a"),
    MinGw("", "dll", "lib", "a");
}
