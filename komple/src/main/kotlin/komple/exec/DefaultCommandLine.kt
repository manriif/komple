package komple.exec

/**
 * Default implementation of [CommandLine].
 */
@JvmInline
internal value class DefaultCommandLine(override val args: Array<String>) : CommandLine {

    override fun toString(): String {
        return args.contentDeepToString()
    }
}