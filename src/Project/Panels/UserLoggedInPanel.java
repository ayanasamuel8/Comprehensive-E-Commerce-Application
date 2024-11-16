package Project.Panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.border.Border;
import Project.Authentication.SQLConnection;
import Project.Main.GUIDesign;

/**
 * UserLoggedInPanel class handles the user interface and actions for a user who
 * is logged in.
 */
public class UserLoggedInPanel implements ActionListener {
  private JButton search, backButton, backButtonUser, viewCartButton;
  static public JTextField searchBarUser;
  static JPanel userLoggedInPanel;
  private static JPanel topPanel;
  private static JPanel buttonPanel;
  private static JPanel companyButtonPanel;
  private JScrollPane scrollPane;
  private Connection connection = GUIDesign.connection;
  private Vector<String> read = new Vector<>();
  private String sql;
  private Statement st;
  private PreparedStatement ps;

  /**
   * Constructs a UserLoggedInPanel and initializes its components.
   */
  public UserLoggedInPanel() {
    readTables();
    initializeComponents();
    setupPanel();
    addListeners();
  }

  /**
   * Reads company and user data from the database.
   */
  private void readTables() {
    String sql = "SELECT companyName,userName FROM companyInformation";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      Statement st = connection.createStatement();
      st.executeUpdate("use project");
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String fullName = rs.getString("companyName");
        String userName = rs.getString("username");
        read.add(fullName);
        read.add(userName);
      }
    } catch (SQLException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  /**
   * Initializes the components for the panel.
   */
  private void initializeComponents() {
    search = new JButton("Search");
    search.setActionCommand("NFind");
    search.addActionListener(this);
    backButton = new JButton("← Back");
    backButton.addActionListener(this);
    backButtonUser = new JButton("← Back");
    viewCartButton = new JButton("View Cart");
    searchBarUser = new JTextField(20);
    userLoggedInPanel = new JPanel(new BorderLayout());
    topPanel = new JPanel(new BorderLayout());
    buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    companyButtonPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for grid-like structure

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10); // Padding between buttons
    gbc.fill = GridBagConstraints.BOTH; // Make buttons fill their grid cells
    gbc.weightx = 1.0; // Make buttons resize horizontally
    gbc.weighty = 1.0; // Make buttons resize vertically

    int row = 0;
    int col = 0;
    int maxCols = 8; // Adjust this value to change the number of columns

    for (int i = 0; i < read.size() - 1; i += 2) {
      JButton button = new JButton("<html>" + (read.get(i).length() < 8
          ? "  " + read.get(i) + "<br>Company</html>"
          : read.get(i) + "<br>" + ret((read.get(i).length() - 8) / 2) + "Company</html>"));
      button.setPreferredSize(new Dimension(100, 100)); // Fixed size for each button
      button.setBorderPainted(false); // Remove border
      button.setBackground(Color.WHITE);
      button.setActionCommand(read.get(i + 1));
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          userLoggedInPanel.removeAll();
          userLoggedInPanel.add(addComponents(e.getActionCommand()));
          userLoggedInPanel.revalidate();
          userLoggedInPanel.repaint();
        }
      });

      gbc.gridx = col;
      gbc.gridy = row;
      companyButtonPanel.add(button, gbc);

      col++;
      if (col >= maxCols) {
        col = 0;
        row++;
      }
    }
  }

  /**
   * Returns a string of non-breaking spaces for spacing in HTML content.
   *
   * @param n the number of spaces
   * @return a string of non-breaking spaces
   */
  private String ret(int n) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < n; i++) {
      s.append(" ");
    }
    return s.toString();
  }

  /**
   * Sets up the main panel layout and adds components.
   */
  private void setupPanel() {
    JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    searchBarPanel.add(searchBarUser);
    searchBarPanel.add(search); // Add search button next to the text field
    topPanel.add(searchBarPanel, BorderLayout.WEST);
    buttonPanel.add(backButtonUser);
    buttonPanel.add(viewCartButton);
    topPanel.add(buttonPanel, BorderLayout.EAST);
    userLoggedInPanel.add(topPanel, BorderLayout.NORTH);

    scrollPane = new JScrollPane(companyButtonPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Ensure vertical scrollbar
                                                                                     // appears as needed
    userLoggedInPanel.add(scrollPane, BorderLayout.CENTER);
  }

  /**
   * Adds action listeners to the components.
   */
  private void addListeners() {
    backButtonUser.addActionListener(this);
    viewCartButton.addActionListener(this);
    searchBarUser.addActionListener(this);
    // GUIDesign.addFocusListeners(searchBarUser);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("NFind")) {
      new GUIDesign().actionPerformed(e);
    } else if (command.equals("← Back")) {
      new GUIDesign().actionPerformed(e);
    } else if (command.equals("View Cart")) {
      new GUIDesign().actionPerformed(e);
    }
  }

  /**
   * Adds company information and item components to the panel based on the
   * username.
   *
   * @param username the username of the company
   * @return the panel containing company and item components
   */
  private JPanel addComponents(String username) {
    System.out.println("in addcomponents " + username);
    JPanel addCom = new JPanel(new FlowLayout());
    JButton companyInfo = new JButton("Company's Information");
    companyInfo.setActionCommand(username);
    JButton itemInfo = new JButton("Item");
    itemInfo.setActionCommand(username);

    companyInfo.addActionListener(e -> {
      userLoggedInPanel.removeAll();
      JPanel labelslave = new JPanel();
      labelslave.setLayout(new BoxLayout(labelslave, BoxLayout.Y_AXIS));
      ResultSet rs = getCompanyInfo(e.getActionCommand());
      try {
        if (rs.next()) {
          String cspecific = rs.getString("Specificproduct");
          String cdetail = rs.getString("Companydetail");
          String cname = rs.getString("Companyname");

          labelslave.add(new JLabel("Company Name: " + cname));
          labelslave.add(new JLabel("Company's Specific Product: " + cspecific));
          labelslave.add(new JLabel(
              cdetail == null ? "This Company doesn't have Company detail currently!"
                  : "Company's Detail: " + cdetail));
        }
      } catch (SQLException e1) {
        labelslave.add(new JLabel("An error occurred: Try again later!"));
        e1.printStackTrace(); // Optional: Print stack trace for debugging
      }

      userLoggedInPanel.add(labelslave);

      userLoggedInPanel.revalidate();
      userLoggedInPanel.repaint();
    });

    itemInfo.addActionListener(e -> {
      userLoggedInPanel.removeAll();
      userLoggedInPanel.add(new ViewPanel2(itemInfo.getActionCommand()).getResultPanel());
      userLoggedInPanel.revalidate();
      userLoggedInPanel.repaint();
    });

    addCom.add(companyInfo);
    addCom.add(itemInfo);
    return addCom; // Return the newly created panel
  }

  /**
   * Retrieves company information from the database based on the username.
   *
   * @param username the username of the company
   * @return the ResultSet containing company information
   */
  private ResultSet getCompanyInfo(String username) {
    try {
      sql = "USE Project";
      st = GUIDesign.connection.createStatement();
      st.executeUpdate(sql);

      sql = "SELECT Companyname, Companydetail, Specificproduct FROM companyinformation WHERE username = ?";
      ps = GUIDesign.connection.prepareStatement(sql);
      ps.setString(1, username);
      ResultSet rs = ps.executeQuery();
      return rs;
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "Connection Failed: " + e.getMessage());
    }
    return null;
  }

  /**
   * Returns the main panel for the user logged-in view.
   *
   * @return the user logged-in JPanel
   */
  public JPanel getPanel() {
    new UserLoggedInPanel();
    return userLoggedInPanel;
  }
}

class ViewPanel2 {
  private JPanel resultPanel;
  private SQLConnection dbHelper;
  private JButton buyButton;
  private PreparedStatement ps;
  private Statement st;
  Border border = BorderFactory.createLineBorder(Color.WHITE, 5);

  /**
   * Constructs a ViewPanel2 for displaying items for the specified user.
   *
   * @param username the username to fetch items for
   */
  public ViewPanel2(String username) {
    dbHelper = new SQLConnection();
    resultPanel = new JPanel();
    resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
    displayItems(username);
  }

  /**
   * Displays items for the specified input (username).
   *
   * @param input the username to fetch items for
   */
  private void displayItems(String input) {
    buyButton = new JButton("Buy");
    Map<Integer, Item> itemMap = new HashMap<>();

    try (ResultSet rs = dbHelper.getItemData(input)) {
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

      for (Item item : itemMap.values()) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BorderLayout());

        JPanel imagesPanel = new JPanel();
        imagesPanel.setLayout(new FlowLayout());
        for (ImageIcon imageIcon : item.getImages()) {
          JLabel imageLabel = new JLabel(imageIcon);
          imageLabel.setPreferredSize(new Dimension(imageIcon.getIconWidth(),
              imageIcon.getIconHeight()));
          ImageIcon icon = (ImageIcon) imageLabel.getIcon();
          Image image = icon.getImage().getScaledInstance(150, 150,
              Image.SCALE_SMOOTH);
          imageLabel.setIcon(new ImageIcon(image));
          imageLabel.setPreferredSize(new Dimension(150, 150));
          imagesPanel.add(imageLabel);
        }

        JLabel nameLabel = new JLabel(
            item.getName() + " - $" + item.getPrice());

        itemPanel.add(imagesPanel, BorderLayout.WEST);
        itemPanel.add(nameLabel, BorderLayout.SOUTH);
        itemPanel.add(buyButton, BorderLayout.EAST);
        itemPanel.setBorder(border);

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

  /**
   * Displays the details of the specified item in a new window.
   *
   * @param name   the name of the item
   * @param price  the price of the item
   * @param detail the details of the item
   * @param id     the ID of the item
   * @param image  the panel containing item images
   */
  private void showItemDetail(String name, double price, String detail, Integer id,
      JPanel image) {
    JButton button = new JButton("Add to Cart");
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addToCart(id, UserPanel.userUserName);
      }
    });
    JPanel labelPanel = new JPanel();
    labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
    labelPanel.add(new JLabel("Product Name: " + name));
    labelPanel.add(new JLabel("Price: " + price));
    labelPanel.add(new JLabel("Product Detail: " + detail));
    JFrame detailFrame = new JFrame();
    detailFrame.setLayout(new BorderLayout());
    detailFrame.add(labelPanel, BorderLayout.WEST);
    detailFrame.add(image, BorderLayout.EAST);
    detailFrame.add(button, BorderLayout.CENTER);
    detailFrame.setSize(700, 400);
    detailFrame.setVisible(true);
  }

  /**
   * Returns the result panel containing the items.
   *
   * @return the result panel
   */
  public JPanel getResultPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(new JScrollPane(resultPanel), BorderLayout.CENTER);
    return panel;
  }

  /**
   * Adds the specified item to the cart for the specified user.
   *
   * @param itemId   the ID of the item to add
   * @param userName the username of the user
   */
  private void addToCart(Integer itemId, String userName) {
    String sql;
    try {
      sql = "USE Project";
      st = GUIDesign.connection.createStatement();
      st.executeUpdate(sql);

      sql = "INSERT INTO cart(itemid, username) VALUES(?,?)";
      ps = GUIDesign.connection.prepareStatement(sql);
      ps.setInt(1, itemId);
      ps.setString(2, userName);
      ps.executeUpdate();
      JOptionPane.showMessageDialog(null, "Added to Cart successfully!!");
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "Sorry, try again later: " + e.getMessage());
    }
  }
}
