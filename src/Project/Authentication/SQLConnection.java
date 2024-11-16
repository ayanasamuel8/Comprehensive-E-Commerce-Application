package Project.Authentication;

import java.sql.*;
import javax.swing.*;

import Project.Main.GUIDesign;
import Project.Panels.UserPanel;

public class SQLConnection {
    String sql;
    Statement st;
    Connection connection;

    /**
     * Establishes a connection to the database.
     *
     * @return the database connection
     */
    public Connection connect() {
        Connection connection;
        String url = "jdbc:mysql://localhost:3306/";

        // Components for the authentication panel
        JTextField userName = new JTextField();
        JPasswordField password = new JPasswordField();
        JPanel authenticationPanel = new JPanel();
        authenticationPanel.setLayout(new BoxLayout(authenticationPanel, BoxLayout.Y_AXIS));
        authenticationPanel.add(new JLabel("Enter Username of your server"));
        authenticationPanel.add(userName);
        authenticationPanel.add(new JLabel("Enter your server password"));
        authenticationPanel.add(password);

        // Show the authentication dialog
        int result = JOptionPane.showConfirmDialog(null, authenticationPanel, "Enter Connection Information",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (userName.getText().isEmpty() || new String(password.getPassword()).isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username or Password is invalid!");
                System.exit(0);
            }
            try {
                // Attempt to establish the connection
                connection = DriverManager.getConnection(url, userName.getText(), new String(password.getPassword()));
                return connection;
            } catch (Exception e) {
                // Handle connection failure
                JOptionPane.showMessageDialog(null, "Connection Failed: " + e.getMessage());
                System.exit(0);
            }
        }

        return null;
    }

    /**
     * Initializes the database connection and creates necessary tables.
     */
    public void project() {
        initializeConnection();
        createDatabaseAndTables();
    }

    /**
     * Initializes the database connection.
     */
    private void initializeConnection() {
        connection = GUIDesign.connection;
    }

    /**
     * Creates the database and tables if they do not exist.
     */
    private void createDatabaseAndTables() {
        try {
            st = connection.createStatement();
            createDatabase();
            useDatabase();
            createTables();
            addCascadeConstraints();
            System.out.println("Tables created successfully!");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    /**
     * Creates the database if it does not exist.
     *
     * @throws SQLException if a database access error occurs
     */
    private void createDatabase() throws SQLException {
        sql = "CREATE DATABASE IF NOT EXISTS Project";
        st.executeUpdate(sql);
    }

    /**
     * Uses the database.
     *
     * @throws SQLException if a database access error occurs
     */
    private void useDatabase() throws SQLException {
        sql = "USE Project";
        st.executeUpdate(sql);
    }

    /**
     * Creates the necessary tables if they do not exist.
     *
     * @throws SQLException if a database access error occurs
     */
    private void createTables() throws SQLException {
        createAuthenticationTable();
        createCompanyInformationTable();
        createItemTable();
        createPictureTable();
        createCartTable();
    }

    /**
     * Creates the authentication table if it does not exist.
     *
     * @throws SQLException if a database access error occurs
     */
    private void createAuthenticationTable() throws SQLException {
        sql = "CREATE TABLE IF NOT EXISTS AUTHENTICATION ("
                + "userName VARCHAR(70) PRIMARY KEY NOT NULL, "
                + "FullName VARCHAR(255) NOT NULL, "
                + "Password VARCHAR(255) NOT NULL);";
        st.executeUpdate(sql);
    }

    /**
     * Creates the company information table if it does not exist.
     *
     * @throws SQLException if a database access error occurs
     */
    private void createCompanyInformationTable() throws SQLException {
        sql = "CREATE TABLE IF NOT EXISTS CompanyInformation ("
                + "userName VARCHAR(70) NOT NULL PRIMARY KEY, "
                + "CompanyName VARCHAR(255) NOT NULL, "
                + "CompanyDetail TEXT, "
                + "SpecificProduct TEXT NOT NULL, "
                + "FOREIGN KEY (userName) REFERENCES AUTHENTICATION(userName));";
        st.executeUpdate(sql);
    }

    /**
     * Creates the item table if it does not exist.
     *
     * @throws SQLException if a database access error occurs
     */
    private void createItemTable() throws SQLException {
        sql = "CREATE TABLE IF NOT EXISTS Item ("
                + "userName VARCHAR(70) NOT NULL, "
                + "ItemID INT AUTO_INCREMENT PRIMARY KEY NOT NULL, "
                + "ItemName VARCHAR(255) NOT NULL, "
                + "ItemPrice INT NOT NULL, "
                + "ItemDetail TEXT, "
                + "FOREIGN KEY (userName) REFERENCES AUTHENTICATION(userName));";
        st.executeUpdate(sql);
    }

    /**
     * Creates the picture table if it does not exist.
     *
     * @throws SQLException if a database access error occurs
     */
    private void createPictureTable() throws SQLException {
        sql = "CREATE TABLE IF NOT EXISTS Picture ("
                + "ItemID INT NOT NULL, "
                + "Picture LONGBLOB NOT NULL, "
                + "FOREIGN KEY (ItemID) REFERENCES Item(ItemID));";
        st.executeUpdate(sql);
    }

    /**
     * Creates the cart table if it does not exist.
     *
     * @throws SQLException if a database access error occurs
     */
    private void createCartTable() throws SQLException {
        sql = "CREATE TABLE IF NOT EXISTS Cart ("
                + "ItemID INT NOT NULL, "
                + "userName VARCHAR(255) NOT NULL, "
                + "FOREIGN KEY (ItemID) REFERENCES Item(ItemID), "
                + "FOREIGN KEY (userName) REFERENCES Authentication(userName));";
        st.executeUpdate(sql);
    }

    /**
     * Adds CASCADE constraints to the tables.
     *
     * @throws SQLException if a database access error occurs
     */
    private void addCascadeConstraints() throws SQLException {
        addCascadeConstraint("FK_picture_item", "Picture", "ItemID", "Item", "ItemID");
        addCascadeConstraint("FK_item_Authentication", "Item", "userName", "Authentication", "userName");
        addCascadeConstraint("FK_companyinformation_Authentication", "CompanyInformation", "userName", "Authentication",
                "userName");
    }

    /**
     * Adds a CASCADE constraint if it does not already exist.
     *
     * @param constraintName   the name of the constraint
     * @param tableName        the name of the table
     * @param columnName       the name of the column
     * @param referencedTable  the name of the referenced table
     * @param referencedColumn the name of the referenced column
     * @throws SQLException if a database access error occurs
     */
    private void addCascadeConstraint(String constraintName, String tableName, String columnName,
            String referencedTable, String referencedColumn) throws SQLException {
        sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS "
                + "WHERE CONSTRAINT_NAME = ?";
        try (PreparedStatement checkConstraint = connection.prepareStatement(sql)) {
            checkConstraint.setString(1, constraintName);
            ResultSet resultSet = checkConstraint.executeQuery();
            resultSet.next();
            int existingConstraintCount = resultSet.getInt(1);
            if (existingConstraintCount == 0) {
                sql = String.format("ALTER TABLE %s "
                        + "ADD CONSTRAINT %s FOREIGN KEY (%s) "
                        + "REFERENCES %s(%s) ON DELETE CASCADE;",
                        tableName, constraintName, columnName, referencedTable, referencedColumn);
                st.executeUpdate(sql);
            }
        }
    }

    /**
     * Retrieves item data based on the specified input (company name).
     *
     * @param input the company name
     * @return the ResultSet containing the item data
     */
    public ResultSet getItemData(String input) {
        try {
            // Use the 'project' database
            String sql = "USE project";
            Statement st = GUIDesign.connection.createStatement();
            st.executeUpdate(sql);

            // Query to retrieve item data
            String query = "SELECT item.itemID, item.itemName, item.itemPrice, item.itemDetail, Picture.picture " +
                    "FROM item JOIN Picture ON item.itemID = Picture.itemID " +
                    "WHERE item.username = ?";
            PreparedStatement statement = GUIDesign.connection.prepareStatement(query);
            statement.setString(1, input);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves item data based on the specified input (item name).
     *
     * @param input the item name
     * @return the ResultSet containing the item data
     */
    public ResultSet getItemData2(String input) {
        try {
            // Use the 'project' database
            String sql = "USE project";
            Statement st = GUIDesign.connection.createStatement();
            st.executeUpdate(sql);

            // Query to retrieve item data with partial matching of item name
            String query = "SELECT item.itemID, item.itemName, item.itemPrice, item.itemDetail, Picture.picture " +
                    "FROM item JOIN Picture ON item.itemID = Picture.itemID " +
                    "WHERE item.itemName LIKE ?";
            PreparedStatement statement = GUIDesign.connection.prepareStatement(query);
            statement.setString(1, "%" + input + "%");
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves item IDs from the cart for the logged-in user.
     *
     * @return the ResultSet containing the item IDs
     */
    public ResultSet getItemData3() {
        try {
            // Use the 'project' database
            String sql = "USE project";
            Statement st = GUIDesign.connection.createStatement();
            st.executeUpdate(sql);

            // Query to retrieve item IDs from the cart for the logged-in user
            String query = "SELECT itemID FROM cart WHERE username = ?";
            PreparedStatement statement = GUIDesign.connection.prepareStatement(query);
            statement.setString(1, UserPanel.userUserName);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves detailed item data for the specified item ID.
     *
     * @param input the item ID
     * @return the ResultSet containing the item data
     */
    public ResultSet getItemData4(int input) {
        try {
            // Use the 'project' database
            String sql = "USE project";
            Statement st = GUIDesign.connection.createStatement();
            st.executeUpdate(sql);

            // Query to retrieve detailed item data for the specified item ID
            String query = "SELECT item.itemID, item.itemName, item.itemPrice, item.itemDetail, Picture.picture " +
                    "FROM item JOIN Picture ON item.itemID = Picture.itemID " +
                    "WHERE item.itemID = ?";
            PreparedStatement statement = GUIDesign.connection.prepareStatement(query);
            statement.setInt(1, input);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
