public class Command {
    private CommandType commandType;
    private int[] args;

    public Command(CommandType commandType, int[] args) {
        setCommandType(commandType);
        setArgs(args);
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setArgs(int[] args) {
        this.args = args;
    }

    public int[] getArgs() {
        return args;
    }
}
