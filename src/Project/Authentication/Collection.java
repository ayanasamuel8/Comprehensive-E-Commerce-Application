package Project.Authentication;

import javax.swing.*;
import Project.Main.GUIDesign;
import java.sql.*;

/**
 * Collection class handles user and company authentication, including signup
 * and login.
 */
public class Collection {
    JPanel panel;
    JTextField userName, fName;
    JPasswordField password;
    JTextArea companyDetail, specificProduct;
    private String globalUsernameString;
    private Connection connection = GUIDesign.connection;

    /**
     * Initializes the input panel for user or company registration.
     *
     * @param input the type of registration (e.g., "Company Name" or "Full Name")
     */
    private void getPanel(String input) {
        panel = new JPanel();
        userName = new JTextField();
        password = new JPasswordField();
        fName = new JTextField();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Enter " + input + " (Required)"));
        panel.add(fName);
        panel.add(new JLabel("Create Username (Required)"));
        panel.add(userName);
        panel.add(new JLabel("Create Password (Required)"));
        panel.add(password);
    }

    /**
     * Handles company signup process.
     */
    public void cSignup() {
        companyDetail = new JTextArea(5, 20);
        companyDetail.setLineWrap(true);
        companyDetail.setWrapStyleWord(true);
        specificProduct = new JTextArea(3, 20);
        specificProduct.setLineWrap(true);
        specificProduct.setWrapStyleWord(true);

        getPanel("Company Name");
        panel.add(new JLabel("Enter Your Company's Specific Product (Required)"));
        panel.add(specificProduct);
        panel.add(new JLabel("Enter Your Company's Detail"));
        panel.add(companyDetail);
        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Company Information",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            register(true);
        } else {
            JOptionPane.showMessageDialog(null, "Required information cannot be null");
        }
    }

    /**
     * Handles user signup process.
     */
    public void uSignup() {
        getPanel("Full Name");
        int result = JOptionPane.showConfirmDialog(null, panel, "Enter User Information", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            register(false);
        } else {
            JOptionPane.showMessageDialog(null, "Required information cannot be null");
        }
    }

    /**
     * Registers the user or company in the database.
     *
     * @param isCompany true if registering a company, false if registering a user
     */
    private void register(boolean isCompany) {
        try {
            Statement st = connection.createStatement();
            st.executeUpdate("USE Project");
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO authentication(userName, fullName, Password) VALUES (?, ?, ?)");
            ps.setString(1, userName.getText());
            ps.setString(2, fName.getText());
            ps.setString(3, new String(password.getPassword()));
            int rowInserted = ps.executeUpdate();
            if (isCompany) {
                ps = connection.prepareStatement(
                        "INSERT INTO companyinformation(userName, companyName, companyDetail, SpecificProduct) VALUES (?, ?, ?, ?)");
                ps.setString(1, userName.getText());
                ps.setString(2, fName.getText());
                ps.setString(3, companyDetail.getText().isEmpty() ? null : companyDetail.getText());
                ps.setString(4, specificProduct.getText());
                ps.executeUpdate();
            }
            if (rowInserted > 0) {
                JOptionPane.showMessageDialog(null, "Registered Successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles user login process.
     *
     * @return true if login is successful, false otherwise
     */
    public boolean login() {
        boolean loggedIn = false;
        JTextField userName = new JTextField();
        JPasswordField password = new JPasswordField();
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.add(new JLabel("Enter Username (Required)"));
        loginPanel.add(userName);
        loginPanel.add(new JLabel("Enter Password (Required)"));
        loginPanel.add(password);

        int result = JOptionPane.showConfirmDialog(null, loginPanel, "Enter Required Information",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (userName.getText().isEmpty() || new String(password.getPassword()).isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username or Password is invalid!");
                System.exit(0);
            }
            try (PreparedStatement statement = connection
                    .prepareStatement(" SELECT password FROM AUTHENTICATION WHERE userName = ?")) {
                Statement st = connection.createStatement();
                st.executeUpdate("use project");
                statement.setString(1, userName.getText());
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");
                    if (storedPassword.equals(new String(password.getPassword()))) {
                        globalUsernameString = userName.getText();
                        loggedIn = true;
                        JOptionPane.showMessageDialog(null, "Login successful!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect password. Please try again.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No user found with username: " + userName.getText());
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage());
            }
        }

        return loggedIn;
    }

    /**
     * Returns the global username string.
     *
     * @return the global username string
     */
    public String retunString() {
        return globalUsernameString;
    }
}
