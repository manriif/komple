package komple.integrity

import de.undercouch.gradle.tasks.download.VerifyAction
import org.gradle.api.Project

/**
 * Default implementation of [IntegrityCheckScope].
 */
internal class DefaultIntegrityCheckScope(override val project: Project) : IntegrityCheckScope {

    override fun checksum(
        checksum: String,
        algorithm: Algorithm
    ): IntegrityCheck {
        val action = VerifyAction(project.layout).apply {
            checksum(checksum)
            algorithm(algorithm.messageDigestName)
        }

        return IntegrityCheck { file ->
            action.src(file)
            action.execute()
        }
    }

    override fun custom(checker: IntegrityChecker): IntegrityCheck {
        return IntegrityCheck(checker)
    }
}