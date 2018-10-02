import java.io.*;
import java.net.*;
import java.util.*;

public class CinemaServer implements ClientSocketHandler {
    private String name;
    private int portNumber;

    private ServerSocket cinemaSocket;
    private Socket clientSocket;
    private ArrayList<ClientConnection> clientConnections;
    private List<Movie> movies;

    public CinemaServer(String name, int portNumber) throws IOException {
        setName(name);
        setPortNumber(portNumber);
        movies = new ArrayList<>();
        clientConnections = new ArrayList<>();
    }

    public void addMovies() throws IOException {
        System.out.println("Add movies");
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

        do {
            System.out.println("Enter movie name:");
            String name = consoleInput.readLine();
            System.out.println("Enter number of seats");
            int seatsCount = Integer.parseInt(consoleInput.readLine());
            Movie movie = new Movie(name, seatsCount);
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

    public synchronized int getRoomAndCheckInVisitor(int numOfGuests, ClientConnection clientConnection) {
        int roomId = 0;
//
//        while (roomId == 0) {
//            for (Room room : rooms) {
//                if (room.getCapacity() == numOfGuests
//                        && room.getIsAvailable()) {
//                    roomId = room.getId();
//                    break;
//                }
//            }
//
//            if (roomId != 0) {
//                System.out.println("Assigning room " + roomId + " to visitor " + clientConnection.getName());
//                clientConnection.setRoomId(roomId);
//                clientConnection.sendRoomId(roomId);
//                markRoomAsTaken(roomId);
//                return 1;
//            } else {
//                try {
//                    System.out.println(clientConnection.getName() + " is waiting for room...");
//                    wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
        return 0;
    }

    @Override
    public List<Integer> getFreeSeats(int movieId) {
        Movie movie = getMovieById(movieId);
        return movie.getFreeSeats();
    }

    private Movie getMovieById(int movieId) {
        return movies.stream()
                .filter(movie -> movie.getId() == movieId)
                .findFirst()
                .orElseThrow();
    }

    @Override
    public synchronized void buyTicket(int movieId, int seatNumber) throws Exception {
        Movie movie = getMovieById(movieId);
        movie.takeSeat(seatNumber);
    }

    public synchronized void closeClientConnection(ClientConnection clientConnection) {
        int roomIdToRelease;

        roomIdToRelease = clientConnection.getRoomId();

        // If visitor is already checked out don't try to check out again.
        if (roomIdToRelease == 0) {
            return;
        }
        // roomIdToRelease = -1 : No room with the capacity needed
        if (roomIdToRelease != -1) {
            System.out.println("Checking out visitor " + clientConnection.getName());
            clientConnection.setRoomId(0);
            markRoomAsAvailable(roomIdToRelease);
            clientConnection.sendCheckoutCompleteNotice();
            notifyAll();
        }

        clientConnection.disconnect();
        clientConnections.remove(clientConnection);
        System.out.println("Checked out visitor " + clientConnection.getName() + ". Total number of guests left: " + clientConnections.size());
    }

    public Room findRoomById(int roomId) {
//        for (Room room : this.rooms) {
//            if (room.getId() == roomId) {
//                return room;
//            }
//        }
        return null;
    }

    public void markRoomAsTaken(int roomId) {
        findRoomById(roomId).setIsAvailable(false);
    }

    public void markRoomAsAvailable(int roomId) {
        findRoomById(roomId).setIsAvailable(true);
    }

    public ServerSocket createServerSocket() throws IOException {
        cinemaSocket = new ServerSocket(getPortNumber());
        return cinemaSocket;
    }

    public Socket createVisitorSocket() {
        clientSocket = new Socket();
        return clientSocket;
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

    public boolean checkRoomCapacity(int guestsExpected) {
//        for (Room room : rooms) {
//            if (room.getCapacity() == guestsExpected) {
//                return true;
//            }
//        }
        return false;
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
