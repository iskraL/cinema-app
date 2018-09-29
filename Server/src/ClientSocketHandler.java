public interface ClientSocketHandler {
    void closeClientConnection(ClientConnection clientConnection);

    boolean checkRoomCapacity(int guestsExpected);

    int getRoomAndCheckInVisitor(int numOFGuests, ClientConnection clientConnection);

    boolean isMovieSeatAvailable(int movieId, int seatNumber);

    void buyTicket(int movieId, int seatNumber) throws Exception;
}
