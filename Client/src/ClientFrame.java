import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ClientFrame extends JFrame implements WindowListener {
    private static final int PORT = 8081;

    private JPanel panel;
    private ClientSocket clientSocket;
    private JComboBox<Object> moviesComboBox;
    private JComboBox<Object> seatsComboBox;
    private JButton button;
    private int id;

    public ClientFrame(int id) {
        super("IMAX Client " + id);
        setId(id);
        initializeGui();
        initializeActionListeners();
    }

    private void initializeActionListeners() {
        addWindowListener(this);
        button.addActionListener(new StartButtonListener(this));
    }

    private void initializeGui() {
        this.panel = new JPanel();
        this.panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.panel.setOpaque(true);
        this.setContentPane(panel);

        this.panel.add(createMoviesPanel());
        this.panel.add(createSeatsPanel());
        button = new JButton("Buy Ticket");

        this.panel.add(button);
        this.setBounds(100, 100, 400, 400);
        this.setVisible(true);
    }

    private JPanel createMoviesPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 0));
        moviesComboBox = new JComboBox<>();
        moviesComboBox.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            Movie movie = (Movie) e.getItem();
            ClientFrame.this.showMovie(movie);
        });

        panel.add(moviesComboBox);
        return panel;
    }

    private JPanel createSeatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 0));
        seatsComboBox= new JComboBox<>();

        panel.add(seatsComboBox);
        return panel;
    }

    private void showMovie(Movie movie) {
        clientSocket.getSeats(movie.getId());
    }

    @Override
    public void windowOpened(WindowEvent e) {
        clientSocket = new ClientSocket(PORT, ClientFrame.this);
        clientSocket.loadMovies();
    }

    @Override
    public void windowClosing(WindowEvent e) {
        clientSocket.closeSocket();
        clientSocket = null;
    }

    public void setMovies(List<Movie> movies) {
        moviesComboBox.removeAllItems();
        movies.forEach(moviesComboBox::addItem);
    }

    public void setSeats(List<Integer> seats) {
        seatsComboBox.removeAllItems();
        seats.forEach(seatsComboBox::addItem);
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private class StartButtonListener extends Thread implements ActionListener {
        private ClientFrame clientFrame;

        StartButtonListener(ClientFrame clientFrame) {
            this.clientFrame = clientFrame;
        }

        @Override
        public void run() {
            int movieId = ((Movie) moviesComboBox.getSelectedItem()).getId();
            int seatNumber = (int) seatsComboBox.getSelectedItem();
            clientSocket.buyTicket(movieId, seatNumber);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // When start button is clicked
            this.start();
        }
    }
}
