package komple.exec

/**
 * Implementation of [CommandBuilder] for Unix shells.
 */
internal class ShellCommandBuilder(
    private val shellArgs: Array<String>,
    command: Array<out Any>
) : CommandBuilder {

    private val command = command.toMutableList()

    private fun addOperator(operator: String, args: Array<out Any>): CommandBuilder = apply {
        command.add(operator)
        command.addAll(args)
    }

    private fun addOperator(operator: String, args: Iterable<Any>): CommandBuilder = apply {
        command.add(operator)
        command.addAll(args)
    }

    override fun pipe(vararg args: Any): CommandBuilder = addOperator("|", args)

    override fun pipe(args: Iterable<Any>): CommandBuilder = addOperator("|", args)

    override fun pipeAll(vararg args: Any): CommandBuilder = addOperator("|&", args)

    override fun pipeAll(args: Iterable<Any>): CommandBuilder = addOperator("|&", args)

    override fun then(vararg args: Any): CommandBuilder = addOperator("&&", args)

    override fun then(args: Iterable<Any>): CommandBuilder = addOperator("&&", args)

    override fun otherwise(vararg args: Any): CommandBuilder = addOperator("||", args)

    override fun otherwise(args: Iterable<Any>): CommandBuilder = addOperator("||", args)

    fun build(): Command {
        return CommandImpl(shellArgs + command.joinToString(" "))
    }

    /**
     * Implementation of [Command].
     */
    private data class CommandImpl(override val args: Array<out String>) : Command {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CommandImpl

            return args.contentDeepEquals(other.args)
        }

        override fun hashCode(): Int {
            return args.contentDeepHashCode()
        }

        override fun toString(): String {
            return args.contentDeepToString()
        }
    }
}