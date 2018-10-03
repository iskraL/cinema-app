import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientConnection extends Thread {
    private ClientSocketHandler clientSocketHandler;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientConnection(Socket clientSocket, ClientSocketHandler clientSocketHandler) throws IOException {
        this.clientSocketHandler = clientSocketHandler;
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
                String result = processInput(message);
                if (result == "") {
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
        int movieId;
        StringBuilder result = new StringBuilder();
        switch (command.getCommandType()) {
            case BUY:
                movieId = command.getArgs()[0];
                int seatNumber = command.getArgs()[1];
                this.clientSocketHandler.buyTicket(movieId, seatNumber);
                result.append("buy:");
                result.append(String.format("Seat %d for movie %d bough", seatNumber, movieId));
                break;
            case GET_MOVIES:
                List<Movie> movies = this.clientSocketHandler.getMovies();
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
                result.append("seats:");
                movieId = command.getArgs()[0];
                List<Integer> freeSeats = this.clientSocketHandler.getFreeSeats(movieId);
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
        CommandType commandType = CommandType.getFromString(parts[0]);
        int[] args = Arrays.stream(parts)
                .skip(1)
                .mapToInt(Integer::parseInt)
                .toArray();

        return new Command(commandType, args);
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
