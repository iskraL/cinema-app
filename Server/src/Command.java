public class Command {
    private CommandType commandType;
    private int clientId;
    private int[] args;

    public Command(CommandType commandType, int clientId, int[] args) {
        setCommandType(commandType);
        setClientId(clientId);
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

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }
}
