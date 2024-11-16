package Project.Panels;

import javax.swing.*;
import Project.Main.GUIDesign;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Vector;

/**
 * PostPanel class handles the UI and logic for posting new items with details
 * and images.
 */
public class PostPanel extends JFrame implements ActionListener {
  private JTextField itemNameField, itemPriceField, itemDetailField;
  private JButton addImageButton, postButton, backButton;
  private JPanel postPanel;
  private Vector<File> images;
  private Connection connection;
  private String globalUsernameString;

  /**
   * Constructs a PostPanel with specified connection, username, and item details.
   *
   * @param connection           the database connection
   * @param globalUsernameString the username
   * @param barName              the name of the item
   * @param barPrice             the price of the item
   * @param barDetail            the details of the item
   */
  public PostPanel(Connection connection, String globalUsernameString, String barName, double barPrice,
      String barDetail) {
    this.connection = connection;
    this.globalUsernameString = globalUsernameString;
    this.images = new Vector<>();

    // Initialize and set up the panel
    postPanel = new JPanel();
    postPanel.setLayout(new GridLayout(5, 2));

    itemNameField = new JTextField(barName);
    itemPriceField = new JTextField(barPrice != 0 ? String.valueOf(barPrice) : "");
    itemDetailField = new JTextField(barDetail);

    addImageButton = new JButton("Add Image");
    postButton = new JButton("Post");
    backButton = new JButton("Back1");
    backButton.addActionListener(this);

    // Add components to the panel
    postPanel.add(new JLabel("Product Name (Required):"));
    postPanel.add(itemNameField);
    postPanel.add(new JLabel("Product Price (Required):"));
    postPanel.add(itemPriceField);
    postPanel.add(new JLabel("Product Detail (Optional):"));
    postPanel.add(itemDetailField);
    postPanel.add(addImageButton);
    postPanel.add(postButton);
    postPanel.add(backButton);

    addImageButton.addActionListener(this);
    postButton.addActionListener(this);
  }

  /**
   * Returns the panel for posting items.
   *
   * @return the post JPanel
   */
  public JPanel getPanel() {
    return postPanel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == addImageButton) {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setMultiSelectionEnabled(true);
      int returnValue = fileChooser.showOpenDialog(this);
      if (returnValue == JFileChooser.APPROVE_OPTION) {
        File[] selectedFiles = fileChooser.getSelectedFiles();
        for (File file : selectedFiles) {
          images.add(file);
        }
      }
    } else if (e.getSource() == postButton) {
      postProduct();
    } else if (e.getActionCommand().equals("Back1")) {
      new GUIDesign().actionPerformed(e);
    }
  }

  /**
   * Handles the logic for posting a new product to the database.
   */
  public void postProduct() {
    String itemName = itemNameField.getText();
    int itemPrice = Integer.parseInt(itemPriceField.getText());
    String detail = itemDetailField.getText();

    try {
      // Use the project database
      String sql = "USE project;";
      PreparedStatement ps = connection.prepareStatement(sql);
      ps.executeUpdate();

      // Insert item details into the database
      sql = "INSERT INTO Item (userName, ItemName, ItemPrice, ItemDetail) VALUES(?,?,?,?)";
      ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      System.out.println(globalUsernameString);
      ps.setString(1, globalUsernameString);
      ps.setString(2, itemName);
      ps.setInt(3, itemPrice);
      ps.setString(4, detail.isEmpty() ? null : detail);
      int rowsAffected = ps.executeUpdate();

      if (rowsAffected > 0) {
        JOptionPane.showMessageDialog(null, "Uploaded successfully!!!");
      } else {
        JOptionPane.showMessageDialog(null, "Error");
      }

      // Get the generated item ID
      ResultSet generatedKeys = ps.getGeneratedKeys();
      if (generatedKeys.next()) {
        int itemId = generatedKeys.getInt(1);

        // Insert images into the database
        for (File image : images) {
          sql = "INSERT INTO Picture (ItemID, Picture) VALUES(?, ?)";
          PreparedStatement psPic = connection.prepareStatement(sql);
          psPic.setInt(1, itemId);
          psPic.setBlob(2, new FileInputStream(image));
          psPic.executeUpdate();
        }
      }
    } catch (SQLException e) {
      System.out.println("Connection failed: " + e.getMessage());
    } catch (FileNotFoundException e) {
      System.out.println("File not found: " + e.getMessage());
    }
  }
}
