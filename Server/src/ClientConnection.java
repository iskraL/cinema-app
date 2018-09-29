import java.io.*;
import java.net.*;
import java.util.Arrays;

public class ClientConnection extends Thread {
    private ClientSocketHandler clientSocketHandler;
    private Socket visitorSocket;
    private BufferedReader in;
    private PrintWriter out;
    private ConnectionState connectionState;

    public void setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public ClientConnection(Socket clientSocket, ClientSocketHandler clientSocketHandler) throws IOException {
        this.clientSocketHandler = clientSocketHandler;
        this.visitorSocket = clientSocket;
        this.setConnectionState(ConnectionState.OPENING);
        in = createInputStream();
        out = createOutputStream();
    }

    public BufferedReader createInputStream() throws IOException {
        return new BufferedReader(new InputStreamReader(visitorSocket.getInputStream()));
    }

    public PrintWriter createOutputStream() throws IOException {
        return new PrintWriter(visitorSocket.getOutputStream(), true);
    }

    public void readMessage(ConnectionState connectionState) {
        if (connectionState == ConnectionState.CLOSED) {
            return;
        }

        String message;
        do {
            try {
                message = in.readLine();
            } catch (IOException e) {
                clientSocketHandler.closeClientConnection(this);
                break;
            }

            processInput(message);
        } while (this.connectionState != connectionState);
    }

    public void buyTickets() {
        readMessage(ConnectionState.OPEN);
    }

    public void ticketsBought() {
        readMessage(ConnectionState.CLOSED);
    }

    private void processInput(String message) {
        Command command = this.parseCommand(message);
        switch (command.getCommandType()) {
            case BUY:
                int movieId = command.getArgs()[0];
                int seatNumber = command.getArgs()[1];

                boolean isSeatAvailable = this.clientSocketHandler.isMovieSeatAvailable(movieId, seatNumber);
                if (!isSeatAvailable) {
                    this.setConnectionState(ConnectionState.PENDING);
                    return;
                }
                boolean isTicketBought = false;
                while (!isTicketBought) {
                    try {
                        clientSocketHandler.buyTicket(movieId, seatNumber);
                        isTicketBought = true;
                    } catch (Exception ignored) {
                    }
                }
            case BOUGHT:
                clientSocketHandler.closeClientConnection(this);
                break;
        }
    }

    private Command parseCommand(String message) {
        String[] parts = message.split(":");
        CommandType commandType = CommandType.getFromString(parts[0]);
        int[] args = Arrays.stream(parts)
                .skip(1)
                .mapToInt(Integer::parseInt)
                .toArray();

        return new Command(commandType, args);
    }

    public void sendRoomId(int roomId) {
        String output;
        output = "CHECKIN:" + roomId;
        out.println(output);
    }

    public void sendCheckoutCompleteNotice() {
        out.println("CHECKOUTCOMPLETE:");
        connectionState = 2;
    }

    public void sendRejectionNotice() {
        out.println("NOTEXISTINGROOM:");
        connectionState = 3;
    }

    public void disconnect() {
        try {
            visitorSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        buyTickets();
        ticketsBought();
    }
}
