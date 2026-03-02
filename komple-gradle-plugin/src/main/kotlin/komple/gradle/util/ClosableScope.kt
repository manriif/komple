package komple.gradle.util

import java.lang.AutoCloseable

/**
 * Scope which can be closed.
 */
internal abstract class ClosableScope : AutoCloseable {

    protected var closed = false
        private set

    /**
     * Returns [block]'s result if the scope is not closed, throw otherwise.
     */
    protected inline fun <T, R> T.notClosed(block: T.() -> R): R {
        check(!closed) { "Scope is closed" }
        return block()
    }

    override fun close() {
        if (!closed) {
            closed = true
        }
    }
}