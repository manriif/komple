package komple.gradle.integrity

import de.undercouch.gradle.tasks.download.VerifyAction
import komple.integrity.Algorithm
import komple.integrity.IntegrityCheckScope
import komple.integrity.IntegrityChecker
import org.gradle.api.Project

/**
 * Default implementation of [IntegrityCheckScope].
 */
internal class DefaultIntegrityCheckScope(override val project: Project) : IntegrityCheckScope {

    override fun checksum(
        checksum: String,
        algorithm: Algorithm
    ): IntegrityChecker {
        val action = VerifyAction(project.layout).apply {
            checksum(checksum)
            algorithm(algorithm.toMessageDigestConstant())
        }

        return IntegrityChecker { file ->
            action.src(file)
            action.execute()
        }
    }

    override fun forward(checker: IntegrityChecker): IntegrityChecker {
        return checker
    }
}