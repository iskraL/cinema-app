import java.util.List;

public interface ClientSocketHandler {
    void closeClientConnection(ClientConnection clientConnection);

    boolean checkRoomCapacity(int guestsExpected);

    int getRoomAndCheckInVisitor(int numOFGuests, ClientConnection clientConnection);

    void buyTicket(int movieId, int seatNumber) throws Exception;

    List<Integer> getFreeSeats(int movieId);
}
