package Project.Panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.border.Border;
import Project.Authentication.SQLConnection;

/**
 * ShowCart class displays the shopping cart with items and their details.
 */
public class ShowCart {
    private JPanel resultPanel;
    private SQLConnection dbHelper;
    private JPanel retPanel;
    private JButton buyButton;
    private Border border = BorderFactory.createLineBorder(Color.WHITE, 5);

    /**
     * Constructs a ShowCart panel and initializes its components.
     *
     * @throws SQLException if a database access error occurs
     */
    public ShowCart() throws SQLException {
        retPanel = new JPanel();
        dbHelper = new SQLConnection();
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        buyButton = new JButton("Buy");

        // Fetch items from the database and display them
        ResultSet results = dbHelper.getItemData3();
        while (results.next()) {
            Map<Integer, Item> itemMap = new HashMap<>();
            try (ResultSet rs = dbHelper.getItemData4(results.getInt("itemId"))) {
                while (rs.next()) {
                    int itemId = rs.getInt("itemId");
                    String name = rs.getString("itemName");
                    double price = rs.getDouble("itemPrice");
                    String detail = rs.getString("itemDetail");
                    byte[] imageBytes = rs.getBytes("picture");
                    ImageIcon imageIcon = new ImageIcon(imageBytes);

                    if (itemMap.containsKey(itemId)) {
                        itemMap.get(itemId).addImage(imageIcon);
                    } else {
                        Item item = new Item(itemId, name, price, detail);
                        item.addImage(imageIcon);
                        itemMap.put(itemId, item);
                    }
                }

                // Add item panels to the result panel
                for (Item item : itemMap.values()) {
                    JPanel itemPanel = new JPanel();
                    itemPanel.setLayout(new BorderLayout());

                    JPanel imagesPanel = new JPanel();
                    imagesPanel.setLayout(new FlowLayout());

                    // Add images to the images panel
                    for (ImageIcon imageIcon : item.getImages()) {
                        JLabel imageLabel = new JLabel(imageIcon);
                        Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        imageLabel.setIcon(new ImageIcon(image));
                        imageLabel.setPreferredSize(new Dimension(150, 150));
                        imagesPanel.add(imageLabel);
                    }

                    JLabel nameLabel = new JLabel(item.getName() + " - $" + item.getPrice());
                    itemPanel.add(imagesPanel, BorderLayout.WEST);
                    itemPanel.add(nameLabel, BorderLayout.SOUTH);
                    itemPanel.add(buyButton, BorderLayout.EAST);
                    itemPanel.setBorder(border);

                    // Add mouse listener to show item details on click
                    itemPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            showItemDetail(item.getName(), item.getPrice(), item.getDetail(), item.getId(),
                                    imagesPanel);
                        }
                    });

                    resultPanel.add(itemPanel);
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        retPanel.add(resultPanel);
    }

    /**
     * Shows the details of an item in a new window.
     *
     * @param name   the name of the item
     * @param price  the price of the item
     * @param detail the details of the item
     * @param id     the ID of the item
     * @param image  the panel containing item images
     */
    private void showItemDetail(String name, double price, String detail, Integer id, JPanel image) {
        JPanel label = new JPanel();
        label.setLayout(new BoxLayout(label, BoxLayout.Y_AXIS));
        label.add(new JLabel("Product Name: " + name));
        label.add(new JLabel("Price: " + price));
        label.add(new JLabel("Product Detail: " + detail));

        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.add(label, BorderLayout.WEST);
        frame.add(image, BorderLayout.EAST);
        frame.setSize(700, 400);
        frame.setVisible(true);
    }

    /**
     * Returns the panel containing the shopping cart.
     *
     * @return the JPanel containing the shopping cart
     */
    public JPanel returnPanel() {
        return retPanel;
    }

    /**
     * Main method to run the shopping cart GUI.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Shopping Cart");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);
                frame.add(new ShowCart().returnPanel());
                frame.setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
