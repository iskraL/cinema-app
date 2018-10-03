import java.io.ByteArrayInputStream;
import java.io.IOException;

public class App {
    static void fakeInput() {
        String input = "Film\n" +
                "15\n" +
                "y\n" +
                "Drug film\n" +
                "3\n" +
                "y\n" +
                "bla\n" +
                "100\n" +
                "n\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    public static void main(String[] args) throws IOException {
        int portNumber = 8081;
        fakeInput();
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
