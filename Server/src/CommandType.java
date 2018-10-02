public enum CommandType {
    BUY, UNKNOWN, GET_SEATS, CLOSE;

    public static CommandType getFromString(String str) {
        switch (str.toLowerCase()) {
            case "buy":
                return CommandType.BUY;
            case "seats":
                return CommandType.GET_SEATS;
            case "close":
                return CommandType.CLOSE;
            default:
                return CommandType.UNKNOWN;
        }
    }
}
