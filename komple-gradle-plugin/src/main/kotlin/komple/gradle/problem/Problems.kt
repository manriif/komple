@file:Suppress("UnstableApiUsage")

package komple.gradle.problem

import org.gradle.api.problems.ProblemGroup
import org.gradle.api.problems.ProblemId

internal val KompleProblemGroup = ProblemGroup.create("komple", "Komple")

internal val KompleHostUnsupportedProblemId =
    ProblemId.create("komple-host", "Host is not supported", KompleProblemGroup)

internal val KompleUnsupportedOperationProblemId =
    ProblemId.create("komple-task", "Unsupported operation", KompleProblemGroup)