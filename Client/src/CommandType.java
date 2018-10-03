public enum CommandType {
    BUY, UNKNOWN, GET_MOVIES, GET_SEATS, CLOSE;

    public static CommandType getFromString(String str) {
        switch (str.toLowerCase()) {
            case "buy":
                return CommandType.BUY;
            case "seats":
                return CommandType.GET_SEATS;
            case "movies":
                return CommandType.GET_MOVIES;
            case "close":
                return CommandType.CLOSE;
            default:
                return CommandType.UNKNOWN;
        }
    }
}