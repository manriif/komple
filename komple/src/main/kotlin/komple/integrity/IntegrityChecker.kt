package komple.integrity

import java.io.File
import java.io.Serializable

/**
 * Function responsible for checking file integrity.
 */
public fun interface IntegrityChecker : Serializable {

    /**
     * Checks [file] integrity.
     * An exception is thrown if the file integrity was compromised.
     */
    public fun check(file: File)
}