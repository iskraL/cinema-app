import java.io.*;
import java.net.*;
import java.util.*;

public class CinemaServer {
    private String name;
    private int portNumber;

    private ServerSocket cinemaSocket;
    private Socket clientSocket;
    private ArrayList<ClientConnection> clientConnections;
    private List<Movie> movies;

    private Set<Integer> currentlyViewedMovies;
    private Map<Integer, Integer> clientsToMoviesMap;

    public CinemaServer(String name, int portNumber) {
        setName(name);
        setPortNumber(portNumber);
        movies = new ArrayList<>();
        clientConnections = new ArrayList<>();
        currentlyViewedMovies = new HashSet<>();
        clientsToMoviesMap = new HashMap<>();
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
        System.out.println();
    }

    public void waitForConnections() throws IOException {
        while (true) {
            clientSocket = createConnection(cinemaSocket);
            if(clientSocket == null) {
                continue;
            }

            ClientConnection clientConnection = new ClientConnection(clientSocket, this);
            clientConnections.add(clientConnection);
            clientConnection.start();
        }
    }

    public synchronized List<Integer> getFreeSeats(int movieId) throws InterruptedException {
        Movie movie = getMovieById(movieId);
        return movie.getFreeSeats();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void clearClient(int clientId) {
        if(clientsToMoviesMap.containsKey(clientId)) {
            int movieID = clientsToMoviesMap.get(clientId);
            currentlyViewedMovies.remove(movieID);
            clientsToMoviesMap.remove(clientId);
        }
    }

    public void addClientMovie(int clientId, int movieId) {
        this.clearClient(clientId);
        clientsToMoviesMap.put(clientId, movieId);
        currentlyViewedMovies.add(movieId);
    }

    public Movie getMovieById(int movieId) {
        return movies.stream()
                .filter(movie -> movie.getId() == movieId)
                .findFirst()
                .orElseThrow();
    }

    public synchronized void buyTicket(int clientId, int movieId, int seatNumber) throws Exception {
        while(currentlyViewedMovies.contains(movieId)) {
            System.out.println("Client " + clientId + " waiting to release the movie");
            wait(100);
        }

        Movie movie = getMovieById(movieId);
        movie.takeSeat(seatNumber);
    }

    public synchronized void closeClientConnection(ClientConnection clientConnection) {
        clientConnection.disconnect();
        clientConnections.remove(clientConnection);
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
