package komple.project.c

/**
 * Optimization for a C compilation.
 */
public enum class COptimization(internal val value: String) {

    /**
     * `-O0`.
     */
    Level0("0"),

    /**
     * `-O1`.
     */
    Level1("1"),

    /**
     * `-O2`.
     */
    Level2("2"),

    /**
     * `-O3`.
     */
    Level3("3"),

    /**
     * `-Os`.
     */
    Size("s")
}