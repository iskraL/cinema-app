import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        int portNumber = 1234;
        try {
            CinemaServer cinema = new CinemaServer("IMAX", portNumber);
            cinema.addMovies();

            cinema.openSocket();

            cinema.waitForConnections();

            cinema.closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
