/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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