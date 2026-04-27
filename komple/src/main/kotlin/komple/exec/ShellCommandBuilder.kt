package komple.exec

/**
 * Implementation of [CommandBuilder] .
 */
internal class ShellCommandBuilder(command: Array<out Any>) : CommandBuilder {

    private val command = command.toMutableList()

    ///////////////////////////////////////////////////////////////////////////
    // Insertions
    ///////////////////////////////////////////////////////////////////////////

    private inline fun insert(
        operator: String?,
        insert: () -> Unit
    ) = apply {
        operator?.let(command::add)
        insert()
    }

    private fun insert(
        operator: String?,
        args: Array<out Any>
    ): CommandBuilder = insert(operator) {
        command.addAll(args)
    }

    private fun insert(
        operator: String?,
        args: Iterable<Any>
    ): CommandBuilder = insert(operator) {
        command.addAll(args)
    }

    private fun insert(
        operator: String?,
        command: Command
    ): CommandBuilder = insert(
        operator = operator,
        args = command.interpret(ForwardInterpreter).args
    )

    ///////////////////////////////////////////////////////////////////////////
    // Operations
    ///////////////////////////////////////////////////////////////////////////

    override fun append(vararg args: Any): CommandBuilder = insert(null, args)

    override fun append(args: Iterable<Any>): CommandBuilder = insert(null, args)

    override fun append(command: Command): CommandBuilder = insert(null, command)

    override fun pipe(vararg args: Any): CommandBuilder = insert(PIPE, args)

    override fun pipe(args: Iterable<Any>): CommandBuilder = insert(PIPE, args)

    override fun pipe(command: Command): CommandBuilder = insert(PIPE, command)

    override fun pipeAll(vararg args: Any): CommandBuilder = insert(PIPE_ALL, args)

    override fun pipeAll(args: Iterable<Any>): CommandBuilder = insert(PIPE_ALL, args)

    override fun pipeAll(command: Command): CommandBuilder = insert(PIPE_ALL, command)

    override fun then(vararg args: Any): CommandBuilder = insert(AND, args)

    override fun then(args: Iterable<Any>): CommandBuilder = insert(AND, args)

    override fun then(command: Command): CommandBuilder = insert(AND, command)

    override fun otherwise(vararg args: Any): CommandBuilder = insert(OR, args)

    override fun otherwise(args: Iterable<Any>): CommandBuilder = insert(OR, args)

    override fun otherwise(command: Command): CommandBuilder = insert(OR, command)

    ///////////////////////////////////////////////////////////////////////////
    // Command
    ///////////////////////////////////////////////////////////////////////////

    override fun build(): Command = CommandImpl(command.map { it.toString() })

    /**
     * Implementation of [Command] which is serializable.
     */
    private data class CommandImpl(private val args: List<String>) : Command {

        override fun interpret(interpreter: CommandInterpreter): CommandLine {
            return interpreter.createCommandLine(args.toTypedArray())
        }

        override fun toBuilder(): CommandBuilder {
            return ShellCommandBuilder(args.toTypedArray())
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interpreter
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Interpreter not altering command.
     */
    private object ForwardInterpreter : CommandInterpreter {

        @Suppress("unused")
        private fun readResolve(): Any = ForwardInterpreter

        override fun createCommandLine(args: Array<String>): CommandLine {
            return DefaultCommandLine(args)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Companion
    ///////////////////////////////////////////////////////////////////////////

    private companion object {

        const val PIPE = "|"
        const val PIPE_ALL = "|&"
        const val AND = "&&"
        const val OR = "||"
    }
}