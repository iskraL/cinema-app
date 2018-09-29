import java.io.*;
import java.net.*;

public class Visitor {

    private String name;
    private int numOFGuests;
    private int stayDurationInSec;
    private int roomId;
    private int portNumber;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int state;
    private VisitorFrame frame;

    public Visitor(int portNumber,VisitorFrame frame) {
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

    public void setNumOFGuests(int numOFGuests) {
        this.numOFGuests = numOFGuests;
    }

    public int getNumOFGuests() {
        return numOFGuests;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setStayDurationInSec(int stayDurationInSec) {
        this.stayDurationInSec = stayDurationInSec;
    }

    public int getStayDurationInSec() {
        return stayDurationInSec;
    }

    public int getState() {
        return state;
    }

    public void setProgress(){
        frame.setProgress(state,this);
    }

    public Socket createSocket(){
        InetAddress address = getInetaddress();

        try {
            socket = new Socket(address,portNumber);
        } catch (IOException e) {
            System.out.println("Couldn't create Visitor socket!");
        }
        return socket;
    }

    public void createInputStream(){
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e){
            System.out.println("Couldn't create Input stream!");
        }
    }

    public void createOutputStream(){
        try {
            out = new PrintWriter(socket.getOutputStream(),true);
        } catch (IOException e) {
            System.out.println("Couldn't create Output stream!");
        }

    }

    public InetAddress getInetaddress(){
        InetAddress address = null;

        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("Couldn't get localhost InetAddress!");
        }
        return address;
    }

    public void createConnection(){
        createSocket();
        createInputStream();
        createOutputStream();
    }

    /*public void getInfo() throws IOException {
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("My name is ");
        setName(consoleInput.readLine());
        System.out.print("Number of guests: ");
        setNumOFGuests(Integer.parseInt(consoleInput.readLine()));
        System.out.print("Stay duration: ");
        setStayDurationInSec(Integer.parseInt(consoleInput.readLine()));
    }*/

    public void sendCheckInRequest(){
        state = 0;
        setProgress();
        System.out.println(name + " is checking in");
        StringBuilder message = new StringBuilder();

        message.append("CHECKINREQUEST:");
        message.append(name);
        message.append(":");
        message.append(numOFGuests);

        out.println(message.toString());
        state = 1;
        setProgress();
        waitForRoom();
    }

    public void sendCheckoutRequest(){
        state = 5;
        setProgress();
        System.out.println(name + " is checking out");
        out.println("CHECKOUT:" + name);
    }

    public void checkoutComplete(){
        readMessage();
    }

    public void waitForRoom(){
        state = 2;
        setProgress();
        readMessage();
    }

    public void readMessage(){
        String message;
        int processedMessage;

        do{
            try {
                message = in.readLine();
                System.out.println("Hotel: " + message);
            } catch (IOException e) {
                System.out.println("Connection failed!");
                closeSocket();
                break;
            }

            processedMessage = processInput(message);

            if (processedMessage > 0){
                state = 3;
                setProgress();
                break;
            }
        }while (message == null);
    }

    public int processInput(String message){
        String[] input = message.split(":");
        String command = input[0];


        switch (command){
            case "CHECKIN": {
                int inputInfo = Integer.parseInt(input[1]);
                setRoomId(inputInfo);
                return inputInfo;
            }
            case "CHECKOUTCOMPLETE": {
                System.out.println(name + " left.");
                state = 6;
                setProgress();
                closeSocket();
                return -1;
            }
            case "NOTEXISTINGROOM":
                System.out.println("There are no rooms with capacity " + this.numOFGuests + "!");
                state = 7;
                setProgress();
                return -1;
        }
        return 0;
    }

    public void stayInRoom(){
        System.out.println(name + " staying in room " + roomId + " for " + stayDurationInSec + " days...");
        state = 4;
        setProgress();

        try {
            Thread.sleep(1000 * stayDurationInSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket(){
        try {
            //System.out.println(name + " leaving...");
            socket.close();
        } catch (IOException e) {
            System.out.println("Couldn't close server socket!");
        }
    }
}
