package komple

/**
 * Annotates API which are exposed publicly but are reserved for internal use.
 */
@RequiresOptIn(
    "This API is internal and should not be used outside of Komple namespace",
    RequiresOptIn.Level.ERROR
)
public annotation class KompleInternalApi