package komple.integrity

/**
 * Available message digest algorithms.
 */
public enum class Algorithm {
    MD2,
    MD5,
    SHA1,
    SHA_224,
    SHA_256,
    SHA_384,
    SHA_512,
    SHA_512_224,
    SHA_512_256,
    SHA3_224,
    SHA3_256,
    SHA3_384,
    SHA3_512,
}

///////////////////////////////////////////////////////////////////////////
// Extensions
///////////////////////////////////////////////////////////////////////////

/**
 * Returns a constant usable with message digest for `this` [Algorithm].
 */
internal fun Algorithm.toMessageDigestConstant(): String = when (this) {
    Algorithm.MD2 -> "MD2"
    Algorithm.MD5 -> "MD5"
    Algorithm.SHA1 -> "SHA-1"
    Algorithm.SHA_224 -> "SHA-224"
    Algorithm.SHA_256 -> "SHA-256"
    Algorithm.SHA_384 -> "SHA-384"
    Algorithm.SHA_512 -> "SHA-512"
    Algorithm.SHA_512_224 -> "SHA-512/224"
    Algorithm.SHA_512_256 -> "SHA-512/256"
    Algorithm.SHA3_224 -> "SHA3-224"
    Algorithm.SHA3_256 -> "SHA3-256"
    Algorithm.SHA3_384 -> "SHA3-384"
    Algorithm.SHA3_512 -> "SHA3-512"
}