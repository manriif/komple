package komple.gradle.deps

/**
 * A dependency graph that tracks dependencies between items and prevents cycles.
 *
 * @param E the type of items in the graph
 *
 * @author Claude
 */
internal class DependencyGraph<E> {

    private val dependencies = mutableMapOf<E, MutableSet<E>>()

    /**
     * Adds a dependency relationship where [dependent] depends on [dependency].
     *
     * @throws CyclicDependencyException if adding this dependency would create a cycle
     */
    fun addDependency(dependent: E, dependency: E) {
        require(dependent != dependency) { "Item cannot depend on itself" }

        // Check if adding this dependency would create a cycle
        if (wouldCreateCycle(dependent, dependency)) {
            throw CyclicDependencyException(
                "Adding dependency from $dependent to $dependency would create a cycle"
            )
        }

        dependencies.getOrPut(dependent) { mutableSetOf() }.add(dependency)
    }

    /**
     * Returns all dependencies of [element], including transitive dependencies.
     *
     * @return a set of all items that [element] depends on, directly or indirectly
     */
    fun getDependencies(element: E): Set<E> {
        val result = mutableSetOf<E>()
        val visited = mutableSetOf<E>()
        collectDependencies(element, result, visited)
        return result
    }

    private fun collectDependencies(element: E, result: MutableSet<E>, visited: MutableSet<E>) {
        if (!visited.add(element)) return

        dependencies[element]?.forEach { dependency ->
            result.add(dependency)
            collectDependencies(dependency, result, visited)
        }
    }

    private fun wouldCreateCycle(from: E, to: E): Boolean {
        // If 'to' already depends on 'from' (directly or transitively),
        // then adding 'from -> to' would create a cycle
        return getDependencies(to).contains(from)
    }
}