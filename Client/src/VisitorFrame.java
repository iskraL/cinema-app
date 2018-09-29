import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class VisitorFrame extends JFrame {

    private JPanel panel;
    private JProgressBar progressBar;
    private JButton addVisitorButton;
    private JTextArea taskOutput;
    private JTextField nameField;
    private JTextField guestsField;
    private JTextField stayField;


    public VisitorFrame() {
        super("Hotel Visitor");
        initializeGui();
        initialzeActionListeners();
    }

    private void initialzeActionListeners() {
        addVisitorButton.addActionListener(new StartButtonListener(this));
    }

    private void initializeGui() {
        this.panel = new JPanel();
        this.panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.panel.setOpaque(true);
        this.setContentPane(panel);

        this.panel.add(createInputSection());

        this.panel.add(createButton());
        this.panel.add(createProgressBar());

        //pack();
        this.setBounds(100,100,400, 400);
        this.setVisible(true);
        this.panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
    }


    public JPanel createInputSection(){
        JPanel inputSection = new JPanel();
        JPanel input = new JPanel(new GridLayout(1,0));
        input.setBorder(new EmptyBorder(0,20,10,20));


        nameField = new JTextField(10);
        guestsField = new JTextField(10);
        stayField = new JTextField(10);

        JPanel labels = new JPanel(new GridLayout(0,1));
        JPanel controls = new JPanel(new GridLayout(0,1));

        labels.add(new JLabel("Name: "));
        controls.add(nameField);
        labels.add(new JLabel("Guests: "));
        controls.add(guestsField);
        labels.add(new JLabel("Stay: "));
        controls.add(stayField);

        input.add(labels, BorderLayout.WEST);
        input.add(controls, BorderLayout.EAST);

        inputSection.add(input);
        inputSection.setAlignmentX(CENTER_ALIGNMENT);
        inputSection.setAlignmentY(CENTER_ALIGNMENT);

        return inputSection;
    }

    public JPanel createButton(){
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        buttonPanel.setBorder(new EmptyBorder(0,20,30,20));

        addVisitorButton = new JButton("Add visitor");
        buttonPanel.add(addVisitorButton);
        buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
        buttonPanel.setAlignmentY(CENTER_ALIGNMENT);

        return buttonPanel;
    }

    public JPanel createProgressBar(){
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10,20,10,20));

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        panel.add(progressBar);
        panel.add(taskOutput);

        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        return panel;
    }


    public final void setProgress(int state,Visitor visitor) {
        switch (state) {
            case 0: // Waiting
                progressBar.setValue(0);
                taskOutput.append(visitor.getName() + " has arrived\n");
                break;
            case 1: // Staying in room
                progressBar.setValue(10);
                taskOutput.append(visitor.getName() + " is checking in...\n");
                break;
            case 2: // Waiting for checkout
                progressBar.setValue(20);
                taskOutput.append(visitor.getName() + " is waiting for room...\n");
                break;
            case 3: // Ready
                progressBar.setValue(30);
                taskOutput.append(visitor.getName() + " received room " + visitor.getRoomId() + "\n");
                break;
            case 4: // Ready
                progressBar.setValue(35);
                taskOutput.append(visitor.getName() + " is staying in room " + visitor.getRoomId() + " for " + visitor.getStayDurationInSec() + "\n");
                break;
            case 5:
                progressBar.setValue(90);
                taskOutput.append(visitor.getName() + " is checking out.\n");
                break;
            case 6:
                progressBar.setValue(100);
                taskOutput.append(visitor.getName() + " has checked out.\n");
                break;
            case 7:
                progressBar.setValue(10);
                taskOutput.append("No available rooms for " + visitor.getNumOFGuests() + " guests!\n" + visitor.getName() + " is leaving.\n");
                break;
        }
    }

    private class StartButtonListener extends Thread implements ActionListener {

        private VisitorFrame visitorFrame;
        StartButtonListener(VisitorFrame visitorFrame) {
            this.visitorFrame = visitorFrame;
        }

        @Override
        public void run() {
            addVisitorButton.setEnabled(false);
            int portNumber = 4321;
            Visitor visitor = new Visitor(portNumber,this.visitorFrame);

            visitor.setName(nameField.getText());
            visitor.setNumOFGuests(Integer.parseInt(guestsField.getText()));
            visitor.setStayDurationInSec(Integer.parseInt(stayField.getText()));

            visitor.sendCheckInRequest();
            if (visitor.getState() != 7){
                visitor.stayInRoom();
                visitor.sendCheckoutRequest();
                visitor.checkoutComplete();
            }

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // When start button is clicked
            this.start();

        }
    }
}
