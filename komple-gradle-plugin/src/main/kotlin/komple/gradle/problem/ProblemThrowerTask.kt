package komple.gradle.problem

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.problems.ProblemId
import org.gradle.api.problems.Problems
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

/**
 * Task throwing an exception due to host being not supported.
 */
@DisableCachingByDefault
@Suppress("UnstableApiUsage")
internal abstract class ProblemThrowerTask : DefaultTask() {

    @get:Inject
    internal abstract val problems: Problems

    @get:Internal
    abstract val id: Property<ProblemId>

    @get:Internal
    abstract val message: Property<String>

    @get:Internal
    abstract val details: Property<String>

    @get:Internal
    abstract val solution: Property<String>

    @TaskAction
    fun report() {
        throw problems.reporter.throwing(
            GradleException(message.get()),
            id.get()
        ) {
            details(details.get())
            solution(solution.get())
        }
    }
}