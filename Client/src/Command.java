public class Command {
    private boolean success;
    private CommandType type;
    private String[] args;

    public Command(boolean success, CommandType commandType, String[] args) {
        setSuccess(success);
        setType(commandType);
        setArgs(args);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CommandType getType() {
        return type;
    }

    public void setType(CommandType type) {
        this.type = type;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }
}
