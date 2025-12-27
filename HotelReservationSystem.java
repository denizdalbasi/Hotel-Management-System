import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class HotelReservationSystem extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea outputArea;

    public HotelReservationSystem() {
        setTitle("Hotel Reservation System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));

        // Login Panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(2, 2));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Login"));
        loginPanel.setBackground(new Color(255, 255, 255));
        loginPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginButtonListener());

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new RegisterButtonListener());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 255, 255));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(loginPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Output"));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(panel);
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = User.findUser(username);
            if (user != null && user.getPassword().equals(password)) {
                outputArea.setText("Login successful!\n");
                showReservationOptions();
            } else {
                outputArea.setText("Invalid username or password.\n");
            }
        }
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (User.findUser(username) == null) {
                User.addUser(username, password);
                outputArea.setText("Registration successful! Please login.\n");
            } else {
                outputArea.setText("Username already exists.\n");
            }
        }
    }

    private void showReservationOptions() {
        JFrame reservationFrame = new JFrame("Reservation Options");
        reservationFrame.setSize(400, 300);
        reservationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        reservationFrame.setLocationRelativeTo(null);

        JPanel reservationPanel = new JPanel();
        reservationPanel.setLayout(new GridLayout(0, 1));

        JButton viewReservationsButton = new JButton("View My Reservations");
        viewReservationsButton.addActionListener(e -> viewReservations());

        JButton makeReservationButton = new JButton("Make a Reservation");
        makeReservationButton.addActionListener(e -> makeReservation());

        JButton cancelReservationButton = new JButton("Cancel a Reservation");
        cancelReservationButton.addActionListener(e -> cancelReservation());

        JButton modifyReservationButton = new JButton("Modify a Reservation");
        modifyReservationButton.addActionListener(e -> modifyReservation());

        reservationPanel.add(viewReservationsButton);
        reservationPanel.add(makeReservationButton);
        reservationPanel.add(cancelReservationButton);
        reservationPanel.add(modifyReservationButton);

        reservationFrame.setContentPane(reservationPanel);
        reservationFrame.setVisible(true);
    }

    private void viewReservations() {
        String username = usernameField.getText();
        List<Reservation> reservations = Reservation.getReservations(username);
        if (reservations.isEmpty()) {
            outputArea.setText("No reservations found.\n");
        } else {
            StringBuilder sb = new StringBuilder("Reservations:\n");
            for (Reservation reservation : reservations) {
                sb.append("Hotel: ").append(reservation.getHotelName())
                        .append(", City: ").append(reservation.getCity())
                        .append(", Dates: ").append(reservation.getStartDate())
                        .append(" to ").append(reservation.getEndDate()).append("\n");
            }
            outputArea.setText(sb.toString());
        }
    }

    private void makeReservation() {
        String username = usernameField.getText();

        // Get the list of cities
        List<String> cities = Hotel.getCities();
        String[] cityArray = cities.toArray(new String[0]);

        // Show the city selection dialog
        String city = (String) JOptionPane.showInputDialog(this, "Select a city:", "City Selection",
                JOptionPane.QUESTION_MESSAGE, null, cityArray, cityArray[0]);

        if (city == null) {
            outputArea.setText("No city selected. Reservation cancelled.\n");
            return;
        }

        // Show hotel information
        String hotelInfo = Hotel.getHotelInfo(city, "");
        JOptionPane.showMessageDialog(this, hotelInfo, "Hotel Information", JOptionPane.INFORMATION_MESSAGE);

        // Get the list of hotels in the selected city
        List<String> hotels = Hotel.getHotelsInCity(city);
        String[] hotelArray = hotels.toArray(new String[0]);

        // Show the hotel selection dialog
        String hotelName = (String) JOptionPane.showInputDialog(this, "Select a hotel:", "Hotel Selection",
                JOptionPane.QUESTION_MESSAGE, null, hotelArray, hotelArray[0]);

        if (hotelName == null) {
            outputArea.setText("No hotel selected. Reservation cancelled.\n");
            return;
        }

        String startDate = JOptionPane.showInputDialog("Enter start date (yyyy-MM-dd):");
        String endDate = JOptionPane.showInputDialog("Enter end date (yyyy-MM-dd):");

        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            outputArea.setText("Invalid date format. Please use yyyy-MM-dd.\n");
            return;
        }

        if (Reservation.isRoomAvailable(city, hotelName, startDate, endDate)) {
            int paymentMethod = JOptionPane.showOptionDialog(this, "Select payment method:", "Payment Method",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                    new String[]{"Cash", "Online"}, "Cash");

            if (paymentMethod == 1) { // Online payment
                makeOnlinePayment();
            }

            Reservation reservation = new Reservation(username, city, hotelName, startDate, endDate);
            Reservation.addReservation(reservation);
            outputArea.setText("Reservation made successfully! Please provide your email for the booking confirmation.\n");
            String email = JOptionPane.showInputDialog("Enter your email address:");
            // Handle email sending here, if necessary
        } else {
            outputArea.setText("Room not available for the selected dates.\n");
        }
    }
    private void makeOnlinePayment() {
        JFrame paymentFrame = new JFrame("Online Payment");
        paymentFrame.setSize(400, 250);
        paymentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        paymentFrame.setLocationRelativeTo(null);

        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Credit Card Number
        gbc.gridx = 0;
        gbc.gridy = 0;
        paymentPanel.add(new JLabel("Credit Card Number:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField cardNumberField = new JTextField(20);
        paymentPanel.add(cardNumberField, gbc);

        // Expiry Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        paymentPanel.add(new JLabel("Expiry Date (MM/YY):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField expiryDateField = new JTextField(10);
        paymentPanel.add(expiryDateField, gbc);

        // CVV
        gbc.gridx = 0;
        gbc.gridy = 2;
        paymentPanel.add(new JLabel("CVV:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField cvvField = new JTextField(5);
        paymentPanel.add(cvvField, gbc);

        // Pay Button
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton payButton = new JButton("Pay");
        payButton.addActionListener(e -> {
            if (validatePaymentDetails(cardNumberField.getText(), expiryDateField.getText(), cvvField.getText())) {
                // Handle payment processing here
                JOptionPane.showMessageDialog(paymentFrame, "Payment processed successfully!", "Payment", JOptionPane.INFORMATION_MESSAGE);
                paymentFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(paymentFrame, "Please enter valid payment details.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        paymentPanel.add(payButton, gbc);

        paymentFrame.setContentPane(paymentPanel);
        paymentFrame.setVisible(true);
    }

    private boolean validatePaymentDetails(String cardNumber, String expiryDate, String cvv) {
        if (!cardNumber.matches("\\d{16}")) {
            return false;
        }
        if (!expiryDate.matches("\\d{2}/\\d{2}")) {
            return false;
        }
        if (!cvv.matches("\\d{3}")) {
            return false;
        }
        return true;
    }

    private void cancelReservation() {
        String username = usernameField.getText();
        String city = JOptionPane.showInputDialog("Enter city:");
        String hotelName = JOptionPane.showInputDialog("Enter hotel name:");
        String startDate = JOptionPane.showInputDialog("Enter start date (yyyy-MM-dd):");
        String endDate = JOptionPane.showInputDialog("Enter end date (yyyy-MM-dd):");

        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            outputArea.setText("Invalid date format. Please use yyyy-MM-dd.\n");
            return;
        }

        Reservation.deleteReservation(username, city, hotelName, startDate, endDate);
        outputArea.setText("Reservation cancelled successfully!\n");
    }

    private void modifyReservation() {
        String username = usernameField.getText();
        String city = JOptionPane.showInputDialog("Enter city:");
        String hotelName = JOptionPane.showInputDialog("Enter hotel name:");
        String startDate = JOptionPane.showInputDialog("Enter start date (yyyy-MM-dd):");
        String endDate = JOptionPane.showInputDialog("Enter end date (yyyy-MM-dd):");

        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            outputArea.setText("Invalid date format. Please use yyyy-MM-dd.\n");
            return;
        }

        // Delete the existing reservation
        Reservation.deleteReservation(username, city, hotelName, startDate, endDate);

        // Make a new reservation
        makeReservation();
    }

    private boolean isValidDate(String date) {
        // Simple date format validation
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelReservationSystem().setVisible(true));
    }
}
