package komple.integrity

/**
 * Available message digest algorithms.
 */
public enum class Algorithm(internal val messageDigestName: String) {
    MD2("MD2"),
    MD5("MD5"),
    SHA1("SHA-1"),
    SHA_224("SHA-224"),
    SHA_256("SHA-256"),
    SHA_384("SHA-384"),
    SHA_512("SHA-512"),
    SHA_512_224("SHA-512/224"),
    SHA_512_256("SHA-512/256"),
    SHA3_224("SHA3-224"),
    SHA3_256("SHA3-256"),
    SHA3_384("SHA3-384"),
    SHA3_512("SHA3-512"),
}