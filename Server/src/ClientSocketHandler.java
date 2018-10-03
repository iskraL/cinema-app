import java.util.List;

public interface ClientSocketHandler {
    void closeClientConnection(ClientConnection clientConnection);

    void buyTicket(int movieId, int seatNumber) throws Exception;

    List<Integer> getFreeSeats(int movieId);

    List<Movie> getMovies();
}
