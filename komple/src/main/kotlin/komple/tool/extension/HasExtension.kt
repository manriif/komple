package komple.tool.extension

/**
 * Represents a Komple entity able to provide registered tool [Extension].
 */
public interface HasExtension<Extension : KompleToolExtension> {

    /**
     * The tool associated extension
     */
    public val extension: Extension
}