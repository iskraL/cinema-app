import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientSocket {
    private String name;
    private int numOFGuests;
    private int stayDurationInSec;
    private int roomId;
    private int portNumber;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int state;
    private ClientFrame frame;

    public ClientSocket(int portNumber, ClientFrame frame) {
        this.portNumber = portNumber;
        this.frame = frame;
        createConnection();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getState() {
        return state;
    }

    public Socket createSocket() {
        InetAddress address = getInetaddress();

        try {
            socket = new Socket(address, portNumber);
        } catch (IOException e) {
            System.out.println("Couldn't create ClientSocket socket!");
        }
        return socket;
    }

    public void createInputStream() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Couldn't create Input stream!");
        }
    }

    public void createOutputStream() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Couldn't create Output stream!");
        }
    }

    public InetAddress getInetaddress() {
        InetAddress address = null;

        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("Couldn't get localhost InetAddress!");
        }
        return address;
    }

    public void createConnection() {
        createSocket();
        createInputStream();
        createOutputStream();
    }

    public void loadMovies() {
        String message = frame.getId() + "/movies";
        out.println(message);
        waitForResponse();
    }

    public void getSeats(int movieId) {
        String message = frame.getId() + "/seats/" + movieId;
        out.println(message);
        waitForResponse();
    }

    public void buyTicket(int movieId, int seatNumber) {
        String message = frame.getId() + "/buy/" + movieId + "/" + seatNumber;
        out.println(message);
        waitForResponse();
    }

    public void waitForResponse() {
        String message;
        do {
            try {
                message = in.readLine();
                System.out.println(message);
            } catch (IOException e) {
                System.out.println("Connection failed!");
                closeSocket();
                break;
            }

            processInput(message);
        } while (message == null);
    }

    public void processInput(String message) {
        Command command = this.parseCommand(message);
        if (!command.isSuccess()) {
            // SHOW ERROR;
            return;
        }
        switch (command.getType()) {
            case BUY:
                loadMovies();
                break;
            case GET_MOVIES:
                List<Movie> movies = Arrays.stream(command.getArgs())
                        .map(idName -> idName.split("-"))
                        .map(idName -> new Movie(Integer.parseInt(idName[0]), idName[1])).collect(Collectors.toList());
                frame.setMovies(movies);

                break;
            case GET_SEATS:
                List<Integer> seats = Arrays.stream(command.getArgs())
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                frame.setSeats(seats);
                break;
        }
    }

    private Command parseCommand(String message) {
        String[] parts = message.split(":");
        boolean isSuccess = parts[0].equals("SUCCESS");
        CommandType commandType = null;
        int argsIndex = 1;
        if (isSuccess) {
            commandType = CommandType.getFromString(parts[1]);
            argsIndex += 1;
        }

        String[] args = parts[argsIndex].split(",");
        return new Command(isSuccess, commandType, args);
    }

    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Couldn't close server socket!");
        }
    }
}
