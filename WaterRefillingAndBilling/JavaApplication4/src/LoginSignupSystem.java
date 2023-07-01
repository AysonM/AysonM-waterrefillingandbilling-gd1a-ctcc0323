import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginSignupSystem extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/marcius1";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public boolean login(String username, String password) {
        String query = "SELECT * FROM watersys1 WHERE username = ? AND password = ? AND password IS NOT NULL";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean signup(String username, String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        String query = "INSERT INTO watersys1 (username, password) VALUES (?, ?)";
        String grantQuery = "GRANT ALL PRIVILEGES ON marcius1.* TO ?@'localhost'";

        try (Connection conn = getConnection();
             PreparedStatement grantStatement = conn.prepareStatement(grantQuery)) {
            grantStatement.setString(1, username);
            grantStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public LoginSignupSystem(String username) {
        setTitle("Login and Signup System");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Signup");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(signupButton);

        add(panel);

        usernameField.setText(username);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());

                boolean loginResult = login(username, password);
                if (loginResult) {
                    openDesignFrame(username);
                } else {
                    JOptionPane.showMessageDialog(LoginSignupSystem.this, "Invalid username or password.");
                }
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());

                boolean signupResult = signup(username, password);
                if (signupResult) {
                    openDesignFrame(username);
                } else {
                    JOptionPane.showMessageDialog(LoginSignupSystem.this, "Signup failed.");
                }
            }
        });
    }

    private void openDesignFrame(String username) {
        design designFrame = new design(username);
        designFrame.setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String username = "";
                LoginSignupSystem loginSignupSystem = new LoginSignupSystem(username);
                loginSignupSystem.setVisible(true);
            }
        });
    }
}
