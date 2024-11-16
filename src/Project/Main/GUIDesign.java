package Project.Main;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.*;

import java.sql.*;

import java.util.*;
import java.util.List;

import Project.Authentication.*;
import Project.Panels.*;

public class GUIDesign implements ActionListener {
  static JFrame frame = new JFrame();
  private JButton companyButton, userButton, findButton;
  private JTextField searchBarHome;
  private static JPanel homePanel, semiHome;
  private static CardLayout cardLayout;

  private static SQLConnection conn = new SQLConnection();
  public static String input;
  protected static boolean loggedIn;
  public static Connection connection;

  // Static initializer block to establish the database connection and set up the
  // card layout
  static {
    connection = new SQLConnection().connect();
    cardLayout = new CardLayout();
  }

  /**
   * Returns a JLabel with the specified text, styled for the footer.
   * 
   * @param str the text for the JLabel
   * @return the styled JLabel
   */
  private JLabel ret(String str) {
    JLabel footerLabel = new JLabel(str);
    footerLabel.setForeground(Color.LIGHT_GRAY);
    return footerLabel;
  }

  /**
   * Initializes and returns the main panel for the GUI.
   * 
   * @return the main JPanel
   */
  private JPanel panel() {
    // Ensure the project setup for the database connection
    conn.project();
    frame.setLayout(cardLayout);

    // Initialize main panel with BorderLayout
    semiHome = new ImagePanel("Home3.jpg", new BorderLayout());
    semiHome.setBackground(Color.WHITE);

    // Footer panel setup
    JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
    footer.add(ret("© 2024 CSE Group Members"));
    footer.add(ret(" | Version 1.0.0"));
    footer.add(ret(" | Contact: https://t.me/ProjectBuddies"));
    footer.setPreferredSize(new Dimension(semiHome.getWidth(), 30));
    footer.setBackground(new Color(90, 0, 128));
    semiHome.add(footer, BorderLayout.SOUTH);

    // Header panel setup
    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false); // Make header transparent

    // Label panel for navigation bar
    JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    labelPanel.add(ret("Navigation Bar"));
    labelPanel.setBackground(new Color(70, 0, 200)); // Set background color

    // Button panel for navigation buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    companyButton = new JButton("Company");
    userButton = new JButton("User");
    findButton = new JButton("Find");
    searchBarHome = new JTextField(15);

    // Add action listeners to buttons
    companyButton.addActionListener(this);
    userButton.addActionListener(this);
    findButton.addActionListener(this);

    // Add buttons and search bar to the button panel
    buttonPanel.add(companyButton);
    buttonPanel.add(userButton);
    buttonPanel.add(findButton);
    buttonPanel.add(searchBarHome);
    buttonPanel.setBackground(new Color(70, 0, 200));

    // Add label and button panels to the header
    header.add(labelPanel, BorderLayout.WEST);
    header.add(buttonPanel, BorderLayout.EAST);
    semiHome.add(header, BorderLayout.NORTH);

    // Main panel with CardLayout to switch between different views
    homePanel = new JPanel(cardLayout);
    homePanel.add(semiHome, "Home");

    return homePanel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // Get the command from the event source
    String command = e.getActionCommand();
    System.out.println("button pressed: " + command);

    // Handle different button clicks
    switch (command) {
      case "Company":
        showPanel("Company", new Project.Panels.CompanyPanel().getPanel());
        break;
      case "User":
        showPanel("User", new Project.Panels.UserPanel().getPanel());
        break;
      case "Find":
        showPanel("Find", new SearchView(searchBarHome.getText()).getResultPanel());
        break;
      case "UFind":
        showPanel("See", new SearchView(UserPanel.searchBarUser.getText()).getResultPanel());
        break;
      case "NFind":
        showPanel("See", new SearchView(UserLoggedInPanel.searchBarUser.getText()).getResultPanel());
        break;
      case "← Back":
        showPanel(command, homePanel);
        break;
      case "Back1":
        showPanel(command, homePanel);
        break;
      case "View Cart":
        try {
          JPanel showCart = new ShowCart().returnPanel();
          homePanel.add(showCart, "View Cart");
          showPanel(command, homePanel);
        } catch (SQLException e1) {
          System.err.println("Error displaying the cart: " + e1.getMessage());
          e1.printStackTrace();
        }
        break;

      default:
        System.out.println("Unknown command: " + command);
        break;
    }
  }

  /**
   * Displays the specified panel in the CardLayout.
   * 
   * @param name  the name of the panel to show
   * @param panel the JPanel to show
   */
  private void showPanel(String name, JPanel panel) {
    frame.add(panel, name);
    cardLayout.show(frame.getContentPane(), name);
  }

  public static void main(String[] args) {
    // Set the frame icon
    ImageIcon img = new ImageIcon("AdamaPic.jpg");
    frame.setIconImage(img.getImage());

    // Set the frame size
    frame.setSize(700, 700);

    // Add the main panel to the frame
    frame.add(new GUIDesign().panel());

    // Make the frame visible
    frame.setVisible(true);

    // Close the application properly when the frame is closed
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}

class SearchView {
  private JPanel resultPanel;
  private SQLConnection dbHelper = new SQLConnection();
  private Border border = BorderFactory.createLineBorder(Color.WHITE, 5);

  /**
   * Constructor for SearchView.
   *
   * @param itemName the name of the item to search for
   */
  public SearchView(String itemName) {
    // Initialize the result panel
    resultPanel = new JPanel();
    resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
    // Display the items matching the search input
    displayItems(itemName);
  }

  /**
   * Displays items based on the input search term.
   *
   * @param input the search term
   */
  private void displayItems(String input) {
    // Map to store items by their IDs
    Map<Integer, Item> itemMap = new HashMap<>();

    // Retrieve items from the database
    try (ResultSet rs = dbHelper.getItemData2(input)) {
      while (rs.next()) {
        int itemId = rs.getInt("itemId");
        String name = rs.getString("itemName");
        double price = rs.getDouble("itemPrice");
        String detail = rs.getString("itemDetail");
        byte[] imageBytes = rs.getBytes("picture");

        ImageIcon imageIcon = new ImageIcon(imageBytes);

        itemMap
            .computeIfAbsent(itemId,
                id -> new Item(itemId, name, price, detail))
            .addImage(imageIcon);
      }

      // Add item panels to the result panel
      itemMap.values().forEach(this::addItemPanel);
    } catch (SQLException e) {
      e.printStackTrace(); // Consider using a logging framework
    }
  }

  /**
   * Adds a panel for each item to the result panel.
   *
   * @param item the item to display
   */
  private void addItemPanel(Item item) {
    JPanel itemPanel = new JPanel(new BorderLayout());
    JPanel imagesPanel = createImagesPanel(item.getImages());

    JLabel nameLabel = new JLabel(item.getName() + " - $" + item.getPrice());
    itemPanel.add(imagesPanel, BorderLayout.WEST);
    itemPanel.add(nameLabel, BorderLayout.SOUTH);
    itemPanel.setBorder(border);

    // Add mouse listener to display item details on click
    itemPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        showItemDetail(item.getName(), item.getPrice(), item.getDetail(), imagesPanel);
      }
    });

    resultPanel.add(itemPanel);
  }

  /**
   * Creates a panel to display images of an item.
   *
   * @param images the list of images to display
   * @return the panel containing the images
   */
  private JPanel createImagesPanel(List<ImageIcon> images) {
    JPanel imagesPanel = new JPanel(new FlowLayout());
    for (ImageIcon imageIcon : images) {
      JLabel imageLabel = new JLabel(new ImageIcon(imageIcon.getImage()
          .getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
      imageLabel.setPreferredSize(new Dimension(150, 150));
      imagesPanel.add(imageLabel);
    }
    return imagesPanel;
  }

  /**
   * Displays the details of an item in a new window.
   *
   * @param name        the name of the item
   * @param price       the price of the item
   * @param detail      the details of the item
   * @param imagesPanel the panel containing item images
   */
  private void showItemDetail(String name, double price, String detail, JPanel imagesPanel) {
    JPanel labelPanel = new JPanel();
    labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
    labelPanel.add(new JLabel("Product Name: " + name));
    labelPanel.add(new JLabel("Price: " + price));
    labelPanel.add(new JLabel("Product Detail: " + detail));

    JFrame detailFrame = new JFrame();
    detailFrame.setLayout(new BorderLayout());
    detailFrame.add(labelPanel, BorderLayout.WEST);
    detailFrame.add(imagesPanel, BorderLayout.EAST);
    detailFrame.setSize(700, 400);
    detailFrame.setVisible(true);
  }

  /**
   * Returns the result panel containing search results.
   *
   * @return the panel containing search results
   */
  public JPanel getResultPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(new JScrollPane(resultPanel), BorderLayout.CENTER);
    return panel;
  }
}

class Item {
  private int id;
  private String name;
  private double price;
  private String detail;
  private List<ImageIcon> images;

  /**
   * Constructor for the Item class.
   *
   * @param id     the ID of the item
   * @param name   the name of the item
   * @param price  the price of the item
   * @param detail the details of the item
   */
  public Item(int id, String name, double price, String detail) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.detail = detail;
    this.images = new ArrayList<>();
  }

  /**
   * Adds an image to the item's image list.
   *
   * @param image the ImageIcon to add
   */
  public void addImage(ImageIcon image) {
    images.add(image);
  }

  /**
   * Gets the list of images for the item.
   *
   * @return the list of ImageIcons
   */
  public List<ImageIcon> getImages() {
    return images;
  }

  /**
   * Gets the name of the item.
   *
   * @return the name of the item
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the price of the item.
   *
   * @return the price of the item
   */
  public double getPrice() {
    return price;
  }

  /**
   * Gets the details of the item.
   *
   * @return the details of the item
   */
  public String getDetail() {
    return detail;
  }

  /**
   * Gets the ID of the item.
   *
   * @return the ID of the item
   */
  protected Integer getId() {
    return id;
  }
}
