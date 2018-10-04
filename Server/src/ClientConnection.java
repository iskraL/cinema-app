import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ClientConnection extends Thread {
    private CinemaServer cinemaServer;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientConnection(Socket clientSocket, CinemaServer cinemaServer) throws IOException {
        this.cinemaServer = cinemaServer;
        this.clientSocket = clientSocket;
        in = createInputStream();
        out = createOutputStream();
    }

    public BufferedReader createInputStream() throws IOException {
        return new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public PrintWriter createOutputStream() throws IOException {
        return new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public void buyTickets() throws IOException {
        while (true) {
            String message = in.readLine();
            try {
                if(message.equals("")){
                    continue;
                }

                String result = processInput(message);

                if (Objects.equals(result, "")) {
                    return;
                }

                sendSuccess(result);
            } catch (Exception err) {
                sendError(err);
            }
        }
    }

    private void sendSuccess(String result) {
        out.printf("SUCCESS:%s%n", result);
    }

    private void sendError(Exception err) {
        out.printf("ERROR:%s%n", err.getMessage());
    }

    private String processInput(String message) throws Exception {
        Command command = this.parseCommand(message);

        cinemaServer.clearClient(command.getClientId());

        int movieId;
        StringBuilder result = new StringBuilder();
        switch (command.getCommandType()) {
            case BUY:
                movieId = command.getArgs()[0];
                Movie theMovie = cinemaServer.getMovieById(movieId);
                int seatNumber = command.getArgs()[1];
                this.cinemaServer.buyTicket(command.getClientId(), movieId, seatNumber);
                System.out.println("Client " + command.getClientId() + " trying to buy seat " + seatNumber + " for movie" + theMovie .getName());

                this.cinemaServer.addClientMovie(command.getClientId(), movieId);
                Thread.sleep(5000);
                this.cinemaServer.clearClient(command.getClientId());

                result.append("buy:");
                result.append(String.format("Seat %d for movie %d bough", seatNumber, movieId));
                break;
            case GET_MOVIES:
                List<Movie> movies = this.cinemaServer.getMovies();
                result.append("movies:");
                for (Movie movie : movies) {
                    result.append(movie.getId());
                    result.append("-");
                    result.append(movie.getName());
                    result.append(",");
                }

                result.deleteCharAt(result.length() - 1);
                break;
            case GET_SEATS:
                movieId = command.getArgs()[0];
                result.append("seats:");

                Movie movie = cinemaServer.getMovieById(movieId);
                List<Integer> freeSeats = movie.getFreeSeats();
                for (int seat : freeSeats) {
                    result.append(seat);
                    result.append(",");
                }

                result.deleteCharAt(result.length() - 1);
                break;
        }

        return result.toString();
    }

    private Command parseCommand(String message) {
        String[] parts = message.split("/");
        int clientId = Integer.parseInt(parts[0]);
        CommandType commandType = CommandType.getFromString(parts[1]);
        int[] args = Arrays.stream(parts)
                .skip(2)
                .mapToInt(Integer::parseInt)
                .toArray();

        return new Command(commandType, clientId, args);
    }

    public void disconnect() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            buyTickets();
        } catch (IOException e) {
            e.printStackTrace();
        }
        disconnect();
    }
}
