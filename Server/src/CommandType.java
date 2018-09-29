public enum CommandType {
    BUY, UNKNOWN, BOUGHT;

    public static CommandType getFromString(String str) {
        switch (str.toLowerCase()) {
            case "buy":
                return CommandType.BUY;
            default:
                return CommandType.UNKNOWN;
        }
    }
}
