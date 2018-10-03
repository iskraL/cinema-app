import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public class CinemaServer implements ClientSocketHandler {
    private String name;
    private int portNumber;

    private ServerSocket cinemaSocket;
    private Socket clientSocket;
    private ArrayList<ClientConnection> clientConnections;
    private List<Movie> movies;
    private Set<Integer> currentlyViewedmMovies;

    public CinemaServer(String name, int portNumber) {
        setName(name);
        setPortNumber(portNumber);
        movies = new ArrayList<>();
        clientConnections = new ArrayList<>();
        currentlyViewedmMovies = new HashSet<>();
    }

    public void addMovies() throws IOException {
        System.out.println(" --- Add movies --- ");
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

        int lastId = 0;
        do {
            System.out.println("Enter movie name:");
            String name = consoleInput.readLine();
            System.out.println("Enter number of seats");
            int seatsCount = Integer.parseInt(consoleInput.readLine());
            Movie movie = new Movie(++lastId, name, seatsCount);
            movies.add(movie);
            System.out.print("Do you want to add new movie? y/n ");
        } while (!consoleInput.readLine().equalsIgnoreCase("n"));
    }

    public void waitForConnections() throws IOException {
        while (true) {
            clientSocket = createConnection(cinemaSocket);
            if(clientSocket == null) {
                continue;
            }

            ClientConnection clientConnection =
                    new ClientConnection(clientSocket, this);
            clientConnections.add(clientConnection);
            clientConnection.start();
        }
    }

    @Override
    public List<Integer> getFreeSeats(int movieId) {
        Movie movie = getMovieById(movieId);
        return movie.getFreeSeats();
    }

    @Override
    public List<Movie> getMovies() {
        return movies;
    }

    private Movie getMovieById(int movieId) {
        return movies.stream()
                .filter(movie -> movie.getId() == movieId)
                .findFirst()
                .orElseThrow();
    }

    @Override
    public synchronized void buyTicket(int movieId, int seatNumber) throws Exception {
        while(currentlyViewedmMovies.contains(movieId)) {
            
        }

        Movie movie = getMovieById(movieId);
        movie.takeSeat(seatNumber);
    }

    public synchronized void closeClientConnection(ClientConnection clientConnection) {
        clientConnection.disconnect();
        clientConnections.remove(clientConnection);
        System.out.println("Checked out visitor " + clientConnection.getName() + ". Total number of guests left: " + clientConnections.size());
    }

    public ServerSocket createServerSocket() throws IOException {
        cinemaSocket = new ServerSocket(getPortNumber());
        return cinemaSocket;
    }

    public Socket createConnection(ServerSocket serverSocket) {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            System.out.println("Connection failed on port " + getPortNumber());
        }

        return null;
    }


    public void openSocket() throws IOException {
        cinemaSocket = createServerSocket();
    }

    public void closeSocket() {
        try {
            System.out.println("Closing socket...");
            cinemaSocket.close();
        } catch (IOException e) {
            System.out.println("Couldn't close server socket!");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public int getPortNumber() {
        return portNumber;
    }
}
