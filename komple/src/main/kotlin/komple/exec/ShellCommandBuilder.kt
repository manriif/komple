package komple.exec

/**
 * Implementation of [CommandBuilder] .
 */
internal class ShellCommandBuilder(command: Array<out Any>) : CommandBuilder {

    private val command = command.toMutableList()

    ///////////////////////////////////////////////////////////////////////////
    // Alterations
    ///////////////////////////////////////////////////////////////////////////

    private inline fun addOperator(
        operator: String,
        insert: () -> Unit
    ) = apply {
        command.add(operator)
        insert()
    }

    private fun addOperator(
        operator: String,
        args: Array<out Any>
    ): CommandBuilder = addOperator(operator) {
        command.addAll(args)
    }

    private fun addOperator(
        operator: String,
        args: Iterable<Any>
    ): CommandBuilder = addOperator(operator) {
        command.addAll(args)
    }

    private fun addOperator(
        operator: String,
        command: Command
    ): CommandBuilder = addOperator(
        operator = operator,
        args = ForwardInterpreter.invoke(command).args
    )

    ///////////////////////////////////////////////////////////////////////////
    // Operations
    ///////////////////////////////////////////////////////////////////////////

    override fun pipe(vararg args: Any): CommandBuilder = addOperator(PIPE, args)

    override fun pipe(args: Iterable<Any>): CommandBuilder = addOperator(PIPE, args)

    override fun pipe(command: Command): CommandBuilder = addOperator(PIPE, command)

    override fun pipeAll(vararg args: Any): CommandBuilder = addOperator(PIPE_ALL, args)

    override fun pipeAll(args: Iterable<Any>): CommandBuilder = addOperator(PIPE_ALL, args)

    override fun pipeAll(command: Command): CommandBuilder = addOperator(PIPE_ALL, command)

    override fun then(vararg args: Any): CommandBuilder = addOperator(AND, args)

    override fun then(args: Iterable<Any>): CommandBuilder = addOperator(AND, args)

    override fun then(command: Command): CommandBuilder = addOperator(AND, command)

    override fun otherwise(vararg args: Any): CommandBuilder = addOperator(OR, args)

    override fun otherwise(args: Iterable<Any>): CommandBuilder = addOperator(OR, args)

    override fun otherwise(command: Command): CommandBuilder = addOperator(OR, command)

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