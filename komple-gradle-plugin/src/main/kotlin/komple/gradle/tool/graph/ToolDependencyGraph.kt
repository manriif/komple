package komple.gradle.tool.graph

import komple.tool.KompleTool

/**
 * A dependency graph that manages dependencies between tools.
 *
 * This class maintains a directed acyclic graph (DAG) where each tool can depend on other tools.
 * Cyclical dependencies are automatically detected and prohibited.
 *
 * @property dependencies internal map storing each tool and its set of direct dependencies
 *
 * ## Example Usage:
 * ```
 * val graph = DependencyGraph<String>()
 * graph.addTool("Compiler")
 * graph.addTool("Parser")
 * graph.addDependency("Compiler", "Parser") // Compiler depends on Parser
 *
 * val deps = graph.getAllDependencies("Compiler") // Returns all dependencies including nested ones
 * ```
 *
 * ## Key Operations:
 * - **Add tools**: [addTool] registers a tool in the graph
 * - **Create dependencies**: [addDependency] makes one tool depend on another (with cycle detection)
 * - **Query dependencies**: [getDirectDependencies] and [getAllDependencies] retrieve dependency information
 * - **Remove elements**: [removeTool] and [removeDependency] modify the graph structure
 * - **Topological sorting**: [topologicalSort] provides a valid execution order for all tools
 *
 * @throws CyclicDependencyException when attempting to add a dependency that would create a cycle
 *
 * @see addDependency
 * @see getAllDependencies
 * @see CyclicDependencyException
 *
 * @author Claude
 */
internal class ToolDependencyGraph {

    private val dependencies = mutableMapOf<KompleTool, MutableSet<KompleTool>>()
    
    /**
     * Adds a tool to the graph without any dependencies
     */
    fun addTool(tool: KompleTool) {
        dependencies.putIfAbsent(tool, mutableSetOf())
    }
    
    /**
     * Makes a tool depend on another tool
     * @throws CyclicDependencyException if this would create a cycle
     */
    fun addDependency(tool: KompleTool, dependsOn: KompleTool) {
        // Ensure both tools exist in the graph
        dependencies.putIfAbsent(tool, mutableSetOf())
        dependencies.putIfAbsent(dependsOn, mutableSetOf())
        
        // Check if adding this dependency would create a cycle
        if (wouldCreateCycle(tool, dependsOn)) {
            throw CyclicDependencyException("Adding dependency from $tool to $dependsOn would create a cycle")
        }
        
        dependencies[tool]?.add(dependsOn)
    }
    
    /**
     * Gets all direct dependencies of a tool
     */
    fun getDirectDependencies(tool: KompleTool): Set<KompleTool> {
        return dependencies[tool]?.toSet() ?: emptySet()
    }
    
    /**
     * Gets all dependencies of a tool (including nested/transitive dependencies)
     * Returns them in topological order (dependencies before dependents)
     */
    fun getAllDependencies(tool: KompleTool): Set<KompleTool> {
        val visited = mutableSetOf<KompleTool>()
        val result = mutableSetOf<KompleTool>()
        
        fun dfs(current: KompleTool) {
            if (current in visited) return
            visited.add(current)
            
            dependencies[current]?.forEach { dependency ->
                dfs(dependency)
                result.add(dependency)
            }
        }
        
        dfs(tool)
        return result
    }
    
    /**
     * Gets all dependencies in topological order (as a list)
     */
    fun getAllDependenciesOrdered(tool: KompleTool): List<KompleTool> {
        val visited = mutableSetOf<KompleTool>()
        val result = mutableListOf<KompleTool>()
        
        fun dfs(current: KompleTool) {
            if (current in visited) return
            visited.add(current)
            
            dependencies[current]?.forEach { dependency ->
                dfs(dependency)
            }
            
            if (current != tool) {
                result.add(current)
            }
        }
        
        dfs(tool)
        return result
    }
    
    /**
     * Checks if adding a dependency would create a cycle
     */
    private fun wouldCreateCycle(from: KompleTool, to: KompleTool): Boolean {
        // If 'to' can reach 'from', then adding 'from -> to' would create a cycle
        return canReach(to, from)
    }
    
    /**
     * Checks if there's a path from 'start' to 'target'
     */
    private fun canReach(start: KompleTool, target: KompleTool): Boolean {
        if (start == target) return true
        
        val visited = mutableSetOf<KompleTool>()
        val queue = ArrayDeque<KompleTool>()
        queue.add(start)
        
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current in visited) continue
            visited.add(current)
            
            if (current == target) return true
            
            dependencies[current]?.forEach { dependency ->
                if (dependency !in visited) {
                    queue.add(dependency)
                }
            }
        }
        
        return false
    }
    
    /**
     * Removes a tool and all its dependencies
     */
    fun removeTool(tool: KompleTool) {
        dependencies.remove(tool)
        // Remove this tool from other tools' dependency lists
        dependencies.values.forEach { it.remove(tool) }
    }
    
    /**
     * Removes a specific dependency relationship
     */
    fun removeDependency(tool: KompleTool, dependsOn: KompleTool) {
        dependencies[tool]?.remove(dependsOn)
    }
    
    /**
     * Gets all tools in the graph
     */
    fun getAllTools(): Set<KompleTool> {
        return dependencies.keys.toSet()
    }
    
    /**
     * Performs a topological sort of all tools
     * @throws CyclicDependencyException if the graph contains cycles
     */
    fun topologicalSort(): List<KompleTool> {
        val inDegree = mutableMapOf<KompleTool, Int>()
        val result = mutableListOf<KompleTool>()
        
        // Calculate in-degrees
        dependencies.keys.forEach { inDegree[it] = 0 }
        dependencies.values.flatten().forEach { dep ->
            inDegree[dep] = inDegree.getOrDefault(dep, 0) + 1
        }
        
        // Queue of tools with no dependencies
        val queue = ArrayDeque<KompleTool>()
        inDegree.filter { it.value == 0 }.forEach { queue.add(it.key) }
        
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            result.add(current)
            
            dependencies[current]?.forEach { dependency ->
                inDegree[dependency] = inDegree.getOrDefault(dependency, 0) - 1
                if (inDegree[dependency] == 0) {
                    queue.add(dependency)
                }
            }
        }
        
        if (result.size != dependencies.size) {
            throw CyclicDependencyException("Graph contains cycles")
        }
        
        return result.reversed() // Reverse to get dependencies first
    }
    
    override fun toString(): String {
        return dependencies.entries.joinToString("\n") { (tool, deps) ->
            "$tool -> ${deps.joinToString(", ")}"
        }
    }
}
